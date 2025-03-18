package team7.hrbank.common.dto;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,  // 오류 발생 시간
        int status,         // 오류 코드
        String message,     // 오류 메시지
        String details      // 오류 자세한 메시지
) {
}
