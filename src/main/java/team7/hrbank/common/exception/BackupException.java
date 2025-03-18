package team7.hrbank.common.exception;

import lombok.Getter;

@Getter
public class BackupException extends RuntimeException {

  private final ErrorCode errorCode;
  public BackupException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public int getStatusCode(){
    return errorCode.getStatus();
  }
}
