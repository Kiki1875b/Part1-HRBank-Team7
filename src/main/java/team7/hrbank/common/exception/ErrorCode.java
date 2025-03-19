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
    NOT_FOUND(404, "찾을 수 없음"),
    BACKUP_FAILED(500, "백업에 실패했습니다."),
    // Employee
    EMAIL_DUPLICATION(400, "이메일 중복");

    private final int status;
    private final String message;
}
