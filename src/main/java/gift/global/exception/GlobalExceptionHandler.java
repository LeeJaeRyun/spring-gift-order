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
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponseDto(
                        errorCode.getMessage(),
                        errorCode.getHttpStatus().value()
                ));
    }

    @ExceptionHandler(KaKaoException.class)
    public ResponseEntity<ErrorResponseDto> handleKaKaoException(KaKaoException e) {
        KaKaoErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponseDto(
                        errorCode.getMessage(),
                        errorCode.getHttpStatus().value()
                ));
    }

}
