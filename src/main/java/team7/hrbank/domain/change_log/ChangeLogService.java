package team7.hrbank.domain.change_log;


import jakarta.transaction.Transactional;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.employee.repository.EmployeeRepository;

import java.time.LocalDateTime;
import java.util.List;
import team7.hrbank.domain.change_log.entity.ChangeLogType;



@Service
@RequiredArgsConstructor
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  // private final EmployeeRepository employeeRepository;

//Todo - Employee서비스의 각 사용자 생성, 수정, 삭제에서  ChangeLogService.save 메서드 호출시 시 변경사항 확인 후 회원수정로그 등록.


  @Transactional
  public Page<ChangeLogDto> getChangeLogs(
      String employeeNumber,
      ChangeLogType type,
      String memo,
      String ipAddress,
      LocalDateTime atFrom,
      LocalDateTime atTo,
      Long idAfter,
      Integer size,
      String sortField,
      String sortDirection,
      Pageable pageable) {

    if (!sortField.equals("ipAddress") && !sortField.equals("createdAt")) {
      sortField = "createdAt";
    }

    Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

    Pageable sortedPageable = (pageable == null || pageable.isUnpaged())
        ? PageRequest.of(0, size, Sort.by(direction, sortField))
        : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortField));

    Page<ChangeLog> changeLogs = changeLogRepository.searchChangeLogs(
        employeeNumber, type, memo, ipAddress, atFrom, atTo, idAfter, sortedPageable);

    return changeLogs.map(changeLog -> new ChangeLogDto(
        changeLog.getId(),
        changeLog.getEmployee().getEmployeeNumber(),
        changeLog.getType(),
        changeLog.getMemo(),
        changeLog.getIpAddress(),
        changeLog.getCreatedAt()
    ));
  }

  @Transactional
  public List<DiffDto> getChangeLogDetails(Long id) {
    ChangeLog changeLog = changeLogRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("이력을 찾을 수 없습니다."));

    return changeLog.getDetails();
  }


  public Instant getLatestChannelLogUpdateTime(){
    ChangeLog latestLog = changeLogRepository.findFirstByOrderByCreatedAtDesc().orElse(null);
    return latestLog == null ? Instant.EPOCH : latestLog.getCreatedAt();
  }
}
