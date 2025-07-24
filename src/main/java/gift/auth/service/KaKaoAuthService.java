package gift.auth.service;

import gift.member.auth.JwtProvider;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class KaKaoAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public KaKaoAuthService(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    // 카카오 로그인 화면 URL 생성
    public String createKaKaoLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
    }

    // 인가 코드를 받아 액세스 토큰 요청 및 사용자 정보 가져와 로그인 처리
    public String loginWithCode(String authorizationCode) {
        String accessToken = requestAccessToken(authorizationCode);
        String email = requestUserEmail(accessToken);

        Optional<Member> memberOpt = memberRepository.findByEmail(email);
        Member member = memberOpt.orElseGet(() -> {
            Member newMember = new Member(email, UUID.randomUUID().toString());
            return memberRepository.save(newMember);
        });

        return jwtProvider.createToken(member);
    }

    // 토큰 발급 요청
    private String requestAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", authorizationCode);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token", request, Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || responseBody.get("access_token") == null) {
            throw new RuntimeException("카카오 토큰 요청 실패");
        }
        return (String) responseBody.get("access_token");
    }

    // 사용자 정보 요청 (이메일)
    private String requestUserEmail(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패");
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
        if (kakaoAccount == null || kakaoAccount.get("email") == null) {
            throw new RuntimeException("카카오 사용자 이메일 정보를 찾을 수 없습니다.");
        }

        return (String) kakaoAccount.get("email");
    }



}
