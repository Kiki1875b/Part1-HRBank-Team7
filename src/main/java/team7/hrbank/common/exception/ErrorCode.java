package team7.hrbank.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorCode {
    // 공통
    INTERNAL_SERVER_ERROR(500, "서버 오류"),
    BAD_REQUEST(400, "잘못된 입력"),
    NOT_FOUND(404, "찾을 수 없음");

    private final int status;
    private final String message;
}
