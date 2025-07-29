package gift.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record KaKaoUserInfoResponse(
        @JsonProperty("kakao_account")
        Map<String, Object> kakaoAccount
) {
}