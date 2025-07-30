package gift.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 400 BadRequest
    ITEM_KEYWORD_INVALID(HttpStatus.BAD_REQUEST, "'카카오' 단어는 MD와 협의 후 사용 가능합니다."),
    WISH_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 위시리스트에 존재하는 상품입니다."),
    OPTION_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "해당 상품에 동일한 옵션명이 이미 존재합니다."),
    INVALID_QUANTITY_DECREASE(HttpStatus.BAD_REQUEST, "차감 수량은 음수일 수 없습니다."),

    // 401 Unauthorized
    WRONG_HEADER_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),

    // 403 Forbidden
    EMAIL_DUPLICATE(HttpStatus.FORBIDDEN, "이미 존재하는 이메일입니다."),
    EMAIL_NOT_FOUND(HttpStatus.FORBIDDEN, "존재하지 않는 이메일입니다."),
    WRONG_PASSWORD(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."),

    // 404 NOT FOUND
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND, "위시리스트에 존재하지 않는 상품입니다."),

    // 500 Internal Server Error
    KAKAO_TOKEN_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 토큰 요청에 실패했습니다."),
    KAKAO_USER_INFO_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 사용자 정보 요청에 실패했습니다."),
    KAKAO_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 서버와의 연결에 실패했습니다."),

    // 503 Service Unavailable
    KAKAO_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "카카오 서비스가 일시적으로 불가능합니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String message; // 에러 메시지

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
