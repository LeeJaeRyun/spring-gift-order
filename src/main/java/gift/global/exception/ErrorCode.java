package gift.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 400 BadRequest
    ITEM_KEYWORD_INVALID(40000, HttpStatus.BAD_REQUEST, "'카카오' 단어는 MD와 협의 후 사용 가능합니다."),
    WISH_ALREADY_EXISTS(40002, HttpStatus.BAD_REQUEST, "이미 위시리스트에 존재하는 상품입니다."),
    OPTION_NAME_DUPLICATE(40003, HttpStatus.BAD_REQUEST, "해당 상품에 동일한 옵션명이 이미 존재합니다."),
    INVALID_QUANTITY_DECREASE(40004, HttpStatus.BAD_REQUEST, "차감 수량은 음수일 수 없습니다."),

    // 401 Unauthorized
    WRONG_HEADER_TOKEN(40100, HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),

    // 403 Forbidden
    EMAIL_DUPLICATE(40301, HttpStatus.FORBIDDEN, "이미 존재하는 이메일입니다."),
    EMAIL_NOT_FOUND(40302, HttpStatus.FORBIDDEN, "존재하지 않는 이메일입니다."),
    WRONG_PASSWORD(40303, HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."),

    // 404 NOT FOUND
    ITEM_NOT_FOUND(40400, HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
    MEMBER_NOT_FOUND(40401, HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),
    WISH_NOT_FOUND(40402, HttpStatus.NOT_FOUND, "위시리스트에 존재하지 않는 상품입니다.");


    private final int code; // 커스텀 에러 코드
    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String message; // 에러 메시지

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
