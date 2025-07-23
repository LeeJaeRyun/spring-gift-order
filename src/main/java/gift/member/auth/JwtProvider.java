package gift.member.auth;

import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import gift.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        // UTF-8 인코딩을 명시적으로 설정
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰 생성
     */
    public String createToken(Member member) {
        return Jwts.builder()
                .subject(String.valueOf(member.getId()))
                .claim("email", member.getEmail())
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰 파싱 및 검증
     * @param token 클라이언트로부터 받은 JWT
     * @return Claims (payload)
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.WRONG_HEADER_TOKEN);
        }
    }

    public Long getMemberId(String token) {
        Claims claims = parseToken(token);
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.WRONG_HEADER_TOKEN);
        }
    }


}
