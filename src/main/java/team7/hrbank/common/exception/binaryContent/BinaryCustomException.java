package team7.hrbank.common.exception.binaryContent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BinaryCustomException extends RuntimeException{
    ErrorCode errorCode;
}
