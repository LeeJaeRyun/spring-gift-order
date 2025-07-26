package gift.auth.client;

import gift.auth.dto.KaKaoTokenResponse;
import gift.auth.dto.KaKaoUserInfoResponse;
import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KaKaoAuthClient {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public String buildLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
    }

    public String requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<KaKaoTokenResponse> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token", request, KaKaoTokenResponse.class
        );

        KaKaoTokenResponse tokenResponse = response.getBody();
        if (tokenResponse == null || tokenResponse.accessToken() == null) {
            throw new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
        }

        return tokenResponse.accessToken();
    }

    public String requestUserEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<KaKaoUserInfoResponse> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                KaKaoUserInfoResponse.class
        );

        KaKaoUserInfoResponse userInfo = response.getBody();
        if (userInfo == null || userInfo.kakaoAccount() == null || userInfo.kakaoAccount().get("email") == null) {
            throw new CustomException(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
        }

        return (String) userInfo.kakaoAccount().get("email");
    }
}