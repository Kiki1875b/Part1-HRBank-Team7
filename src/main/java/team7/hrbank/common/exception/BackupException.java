package team7.hrbank.common.exception;

import lombok.Getter;

@Getter
public class BackupException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String detailMessage;

  public BackupException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.detailMessage = errorCode.getMessage();
  }

  public BackupException(ErrorCode errorCode, String detailMessage) {
    super(errorCode.getMessage() + " - " + detailMessage);
    this.errorCode = errorCode;
    this.detailMessage = detailMessage;
  }
}
