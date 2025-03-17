package team7.hrbank.domain.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import team7.hrbank.domain.backup.entity.BackupStatus;


public record BackupListRequestDto(

    String worker,
    BackupStatus status,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant startedAtFrom,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant startedAtTo,
    Long idAfter,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant cursor
) {
}
