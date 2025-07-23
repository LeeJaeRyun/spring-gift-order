package gift.member.dto;

public record MemberLoginRequestDto (
        String email,
        String password
) {}
