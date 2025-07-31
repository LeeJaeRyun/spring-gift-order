package gift.global.exception;

import gift.auth.exception.KaKaoErrorCode;
import gift.auth.exception.KaKaoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException e) {
        return buildErrorResponse(
                e.getErrorCode().getMessage(),
                e.getErrorCode().getHttpStatus().value()
        );
    }

    @ExceptionHandler(KaKaoException.class)
    public ResponseEntity<ErrorResponseDto> handleKaKaoException(KaKaoException e) {
        return buildErrorResponse(
                e.getErrorCode().getMessage(),
                e.getErrorCode().getHttpStatus().value()
        );
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(String message, int statusCode) {
        return ResponseEntity
                .status(statusCode)
                .body(new ErrorResponseDto(message, statusCode));
    }
}
