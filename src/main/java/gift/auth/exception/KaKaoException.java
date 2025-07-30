package gift.auth.exception;

public class KaKaoException extends RuntimeException {

  private final KaKaoErrorCode errorCode;

  public KaKaoException(KaKaoErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public KaKaoErrorCode getErrorCode() {
    return errorCode;
  }

}
