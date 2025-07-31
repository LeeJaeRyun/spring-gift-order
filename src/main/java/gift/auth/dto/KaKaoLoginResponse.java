package gift.auth.dto;

public record KaKaoLoginResponse (
        String jwtToken,
        String kakaoAccessToken
){
}
