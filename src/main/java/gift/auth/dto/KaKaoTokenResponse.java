package gift.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KaKaoTokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("expires_in")
        int expiresIn
) {
}
