package team7.hrbank.common.dto;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String message,
        String details
) {
}
