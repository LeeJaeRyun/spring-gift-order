package gift.global.exception;

public class ErrorResponseDto {
    private final String message;
    private final int status;  // HTTP 상태 코드
    private final int code;    // 커스텀 에러 코드

    public ErrorResponseDto(String message, int status, int code) {
        this.message = message;
        this.status = status;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }
}
