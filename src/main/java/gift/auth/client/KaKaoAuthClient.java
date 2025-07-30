package gift.auth.client;

import gift.auth.config.KaKaoProperties;
import gift.auth.dto.KaKaoTokenResponse;
import gift.auth.dto.KaKaoUserInfoResponse;
import gift.auth.exception.KaKaoErrorCode;
import gift.auth.exception.KaKaoException;
import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KaKaoAuthClient {

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_LOGIN_BASE_URL = "https://kauth.kakao.com/oauth/authorize?response_type=code";

    private final KaKaoProperties kaKaoProperties;
    private final RestTemplate restTemplate;

    public KaKaoAuthClient(KaKaoProperties kaKaoProperties, RestTemplate restTemplate) {
        this.kaKaoProperties = kaKaoProperties;
        this.restTemplate = restTemplate;
    }

    public String buildLoginUrl() {
        return KAKAO_LOGIN_BASE_URL
                + "&client_id=" + kaKaoProperties.getClientId()
                + "&redirect_uri=" + kaKaoProperties.getRedirectUri();
    }

    @Retryable(
            value = {CustomException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @CircuitBreaker(name = "kakaoAccessToken", fallbackMethod = "fallbackAccessToken")
    public String requestAccessToken(String code) {
        log.info("[카카오] 액세스 토큰 요청 시작, code: {}", code);

        HttpEntity<MultiValueMap<String, String>> request = buildTokenRequestEntity(code);

        try {
            ResponseEntity<KaKaoTokenResponse> response = restTemplate.exchange(
                    KAKAO_TOKEN_URL,
                    HttpMethod.POST,
                    request,
                    KaKaoTokenResponse.class
            );

            log.info("[카카오] 응답 상태 코드: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new KaKaoException(KaKaoErrorCode.TOKEN_REQUEST_FAILED);
            }

            KaKaoTokenResponse tokenResponse = response.getBody();
            log.debug("[카카오] 토큰 응답 바디: {}", tokenResponse);

            if (tokenResponse == null || tokenResponse.accessToken() == null) {
                log.error("[카카오] 토큰 응답 실패: {}", response);
                throw new KaKaoException(KaKaoErrorCode.TOKEN_REQUEST_FAILED);
            }

            return tokenResponse.accessToken();

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("[카카오] API 요청 실패: {}", ex.getMessage());
            throw new KaKaoException(KaKaoErrorCode.TOKEN_REQUEST_FAILED);
        } catch (ResourceAccessException ex) {
            log.error("[카카오] 연결 실패: {}", ex.getMessage());
            throw new KaKaoException(KaKaoErrorCode.CONNECTION_FAILED);
        }

    }

    public String fallbackAccessToken(String code, Throwable t) {
        throw new KaKaoException(KaKaoErrorCode.SERVICE_UNAVAILABLE);
    }

    @Retryable(
            value = {CustomException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @CircuitBreaker(name = "kakaoUserEmail", fallbackMethod = "fallbackUserEmail")
    public String requestUserEmail(String accessToken) {
        log.info("[카카오] 사용자 이메일 요청 시작");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<KaKaoUserInfoResponse> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    request,
                    KaKaoUserInfoResponse.class
            );
            log.info("[카카오] 응답 상태: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new KaKaoException(KaKaoErrorCode.USER_INFO_REQUEST_FAILED);
            }

            KaKaoUserInfoResponse userInfo = response.getBody();
            if (userInfo == null || userInfo.kakaoAccount() == null || userInfo.kakaoAccount().get("email") == null) {
                throw new KaKaoException(KaKaoErrorCode.USER_INFO_REQUEST_FAILED);
            }

            return (String) userInfo.kakaoAccount().get("email");

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("[카카오] API 요청 실패: {}", ex.getMessage());
            throw new KaKaoException(KaKaoErrorCode.USER_INFO_REQUEST_FAILED);
        } catch (ResourceAccessException ex) {
            log.error("[카카오] 연결 실패: {}", ex.getMessage());
            throw new KaKaoException(KaKaoErrorCode.CONNECTION_FAILED);
        }
    }

    public String fallbackUserEmail(String accessToken, Throwable t) {
        throw new KaKaoException(KaKaoErrorCode.SERVICE_UNAVAILABLE);
    }

    private HttpEntity<MultiValueMap<String, String>> buildTokenRequestEntity(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kaKaoProperties.getClientId());
        body.add("redirect_uri", kaKaoProperties.getRedirectUri());
        body.add("code", code);
        body.add("client_secret", kaKaoProperties.getClientSecret());

        return new HttpEntity<>(body, headers);
    }

}
