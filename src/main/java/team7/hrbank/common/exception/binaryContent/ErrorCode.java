package team7.hrbank.common.exception.binaryContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    NOT_ALLOWED_FILE_TYPE(BAD_REQUEST, "허용하지 않는 파일 타입입니다."),
    NO_SUCH_BINARY_CONTENT(BAD_REQUEST, "해당하는 바이너리 컨텐츠가 없습니다."),
    FILE_CREATE_ERROR(INTERNAL_SERVER_ERROR, "파일 생성에 실패했습니다."),
    FILE_WRITE_ERROR(INTERNAL_SERVER_ERROR, "파일 쓰기에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}