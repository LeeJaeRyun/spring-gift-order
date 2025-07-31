package gift.order.exception;

import org.springframework.http.HttpStatus;

public enum OrderErrorCode {
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "옵션을 찾을 수 없습니다."),
    EXCEEDS_PURCHASABLE_QUANTITY(HttpStatus.BAD_REQUEST, "주문 수량이 재고 수량보다 많습니다.");

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String message; // 에러 메시지

    OrderErrorCode(HttpStatus httpStatus, String message) {
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
