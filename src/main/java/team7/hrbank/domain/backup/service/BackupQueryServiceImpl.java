package team7.hrbank.domain.backup.service;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.common.exception.BackupException;
import team7.hrbank.common.exception.ErrorCode;
import team7.hrbank.domain.backup.dto.BackupDto;
import team7.hrbank.domain.backup.dto.BackupListRequestDto;
import team7.hrbank.domain.backup.entity.Backup;
import team7.hrbank.domain.backup.entity.BackupStatus;
import team7.hrbank.domain.backup.mapper.BackupMapper;
import team7.hrbank.domain.backup.repository.BackupRepository;

@Service
@RequiredArgsConstructor
public class BackupQueryServiceImpl implements
    BackupQueryService {

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;

  /**
   * Retrieves a paginated list of backups based on the given filtering criteria.
   * @param dto request DTO containing filter conditions
   * @param size maximum size of data to retrieve
   * @param sortField field used to sort
   * @param sortDirection direction for sorting
   * @return Paginated response based on parameters
   */
  @Override
  @Transactional(readOnly = true)
  public PageResponse<BackupDto> findBackupsOfCondition(BackupListRequestDto dto, int size,
      String sortField, String sortDirection) {
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
    Instant nextCursor = calculateNextCursor(sortField, sortDirection, backups);
    int totalElements = (int) backupRepository.getTotalElements();

    return new PageResponse<>(
        backupMapper.fromEntityList(backups),
        nextCursor,
        nextIdAfter,
        size,
        totalElements,
        hasNext
    );
  }


  /**
   * Finds the most recent backup with the specified status
   *
   * @param status The backup status to filter by
   * @return The latest backup DTO matching the status
   */
  @Override
  @Transactional(readOnly = true)
  public BackupDto findLatestBackupByStatus(BackupStatus status) {
    Backup backup = backupRepository.findFirstByStatusOrderByStartedAtDesc(status)
        .orElseThrow(() -> new BackupException(ErrorCode.NOT_FOUND));
    return backupMapper.fromEntity(backup);
  }



  /**
   * Determines the next cursor value based on sorting field and direction.
   *
   * @param sortField The field used for sorting.
   * @param sortDirection The sorting direction ("ASC" or "DESC").
   * @param backups The list of backup records.
   * @return The next cursor value.
   */
  private Instant calculateNextCursor(String sortField, String sortDirection,
      List<Backup> backups) {
    Instant nextCursor = null;

    if ("startedat".equalsIgnoreCase(sortField)) {
      nextCursor = backups.stream()
          .map(Backup::getStartedAt)
          .sorted(
              "DESC".equalsIgnoreCase(sortDirection)
                  ? Comparator.naturalOrder() : Comparator.reverseOrder()
          ).findFirst()
          .orElse(null);
    } else if ("endedat".equalsIgnoreCase(sortField)) {
      nextCursor = backups.stream()
          .map(Backup::getEndedAt)
          .sorted(
              "ASC".equalsIgnoreCase(sortDirection)
                  ? Comparator.nullsFirst(Comparator.reverseOrder())
                  : Comparator.nullsLast(Comparator.naturalOrder())
          ).findFirst()
          .orElse(null);
    }

    return nextCursor;
  }
}
