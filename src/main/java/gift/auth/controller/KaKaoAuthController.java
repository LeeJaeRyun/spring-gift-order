package gift.auth.controller;

import gift.auth.service.KaKaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth/kakao")
public class KaKaoAuthController {

    private final KaKaoAuthService kaKaoAuthService;

    public KaKaoAuthController(KaKaoAuthService kaKaoAuthService) {
        this.kaKaoAuthService = kaKaoAuthService;
    }

    // 클라이언트가 호출해서 카카오 로그인 화면 URL을 받아갈 수 있게 제공
    @GetMapping("/login-url")
    public ResponseEntity<String> getKaKaoLoginUrl() {
        String url = kaKaoAuthService.createKaKaoLoginUrl();
        return ResponseEntity.ok(url);
    }

    // 카카오에서 인가코드 받고 로그인 처리 후 JWT 반환
    @GetMapping("/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam("code") String code) {
        String jwt = kaKaoAuthService.loginWithCode(code);
        return ResponseEntity.ok(jwt);
    }
}
