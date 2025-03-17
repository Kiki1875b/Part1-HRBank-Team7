package team7.hrbank.domain.backup.service;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.common.exception.BackupException;
import team7.hrbank.domain.backup.mapper.BackupMapper;
import team7.hrbank.domain.backup.repository.BackupRepository;

@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService{

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;

  // TODO : 최적화 필요
  @Override
  public PageResponse<BackupDto> findBackupsOfCondition(
      String worker,
      BackupStatus status,
      Instant startedAtFrom,
      Instant startedAtTo,
      Long idAfter,
      Instant cursor,
      int size,
      String sortField,
      String sortDirection
  ) {

    List<Backup> backups = backupRepository.findBackups(
        worker,
        status,
        startedAtFrom,
        startedAtTo,
        idAfter,
        cursor,
        size + 1,
        sortField,
        sortDirection
    );

    if(backups.isEmpty()) {
      return new PageResponse<>(
          Collections.emptyList(),
          null,
          null,
          size,
          0,
          false
      );
    }


    boolean hasNext = backups.size() == size + 1;

    if(hasNext){
      backups.remove(size);
    }

    Long nextIdAfter = backups.get(backups.size() - 1).getId();

    Instant nextCursor = backups.stream()
        .map(Backup::getStartedAt)
        .sorted(
            "DESC".equalsIgnoreCase(sortDirection)
            ? Comparator.reverseOrder() : Comparator.naturalOrder()
        ).findFirst()
        .orElse(null);

    return new PageResponse<>(
        backupMapper.fromEntityList(backups),
        nextCursor,
        nextIdAfter,
        size,
        0,
        hasNext
    );
  }

  @Override
  public BackupDto startBackup() {
    return null;
  }

  @Override
  public BackupDto findLatestBackupByStatus(BackupStatus status) {
    // 에러 처리 방식 논의
    Backup backup = backupRepository.findFirstByStatusOrderByStartedAtDesc(status)
        .orElseThrow(() -> new BackupException());

    return backupMapper.fromEntity(backup);
  }
}
