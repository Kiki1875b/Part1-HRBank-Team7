package team7.hrbank.domain.backup.dto;

import java.net.InetAddress;
import java.time.Instant;
import team7.hrbank.domain.backup.entity.BackupStatus;

public record BackupDto(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    BackupStatus status,
    Long fileId
) {

}
