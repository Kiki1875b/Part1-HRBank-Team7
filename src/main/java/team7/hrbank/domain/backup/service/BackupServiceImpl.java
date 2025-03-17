package team7.hrbank.domain.backup.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.dto.BackupListRequestDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.common.exception.BackupException;
import team7.hrbank.domain.backup.mapper.BackupMapper;
import team7.hrbank.domain.backup.repository.BackupRepository;
import team7.hrbank.domain.change_log.service.ChangeLogService;

@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;

  private final ChangeLogService changeLogService;

  @PersistenceContext
  private final EntityManager em;

  // TODO : 최적화 필요
  @Override
  public PageResponse<BackupDto> findBackupsOfCondition(
      BackupListRequestDto dto,
      int size,
      String sortField,
      String sortDirection
  ) {


    List<Backup> backups = backupRepository.findBackups(
        dto, size, sortField, sortDirection
    );

    if (backups.isEmpty()) {
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

    if (hasNext) {
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

    Instant latestBackupTime = getLatestBackupTime();
    Instant latestChangeLogTime = changeLogService.getLatestChannelLogUpdateTime();

    // 가장 최근 Backup 시간이 가장 최근 ChangeLog 보다 크다면 변경사항이 없다는 뜻. Instant.EPOCH 비교에 대해선 좀 더 생각
    if (latestBackupTime.isAfter(latestChangeLogTime) && !latestBackupTime.equals(Instant.EPOCH)) {
      Backup backup = backupRepository.save(new Backup(Instant.now(), BackupStatus.SKIPPED));
      return backupMapper.fromEntity(backup);
    }

    Backup backup = backupRepository.save(new Backup(Instant.now(), BackupStatus.IN_PROGRESS));
    em.flush();

    // TODO : EMPLYEE 도메인 완료시 나머지 로직


    return null;
  }

  @Override
  public BackupDto findLatestBackupByStatus(BackupStatus status) {
    // 에러 처리 방식 논의
    Backup backup = backupRepository.findFirstByStatusOrderByStartedAtDesc(status)
        .orElseThrow(() -> new BackupException());
    return backupMapper.fromEntity(backup);
  }

  private Instant getLatestBackupTime() {
    Backup latestBackup = backupRepository.findFirstByOrderByStartedAtDesc().orElse(null);
    return latestBackup == null ? Instant.EPOCH : latestBackup.getStartedAt();
  }


}
