package gift.auth.client;

import gift.auth.config.KaKaoProperties;
import gift.auth.dto.KaKaoTokenResponse;
import gift.auth.dto.KaKaoUserInfoResponse;
import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
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

@Component
public class KaKaoAuthClient {

    private final KaKaoProperties kaKaoProperties;
    private final RestTemplate restTemplate;

    public KaKaoAuthClient(KaKaoProperties kaKaoProperties, RestTemplate restTemplate) {
        this.kaKaoProperties = kaKaoProperties;
        this.restTemplate = restTemplate;
    }

    public String buildLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + kaKaoProperties.getClientId()
                + "&redirect_uri=" + kaKaoProperties.getRedirectUri();
    }

    @Retryable(
            value = {CustomException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kaKaoProperties.getClientId());
        body.add("redirect_uri", kaKaoProperties.getRedirectUri());
        body.add("code", code);
        body.add("client_secret", kaKaoProperties.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KaKaoTokenResponse> response = restTemplate.postForEntity(
                    "https://kauth.kakao.com/oauth/token", request, KaKaoTokenResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
            }

            KaKaoTokenResponse tokenResponse = response.getBody();
            if (tokenResponse == null || tokenResponse.accessToken() == null) {
                throw new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
            }

            return tokenResponse.accessToken();

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
        } catch (ResourceAccessException ex) {
            throw new CustomException(ErrorCode.KAKAO_CONNECTION_FAILED);
        }

    }

    @Retryable(
            value = {CustomException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String requestUserEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<KaKaoUserInfoResponse> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    request,
                    KaKaoUserInfoResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new CustomException(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
            }

            KaKaoUserInfoResponse userInfo = response.getBody();
            if (userInfo == null || userInfo.kakaoAccount() == null || userInfo.kakaoAccount().get("email") == null) {
                throw new CustomException(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
            }

            return (String) userInfo.kakaoAccount().get("email");

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new CustomException(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
        } catch (ResourceAccessException ex) {
            throw new CustomException(ErrorCode.KAKAO_CONNECTION_FAILED);
        }
    }

}
