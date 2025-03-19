package team7.hrbank.domain.change_log.service;


import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.common.exception.change_log.ChangeLogNoDetectedException;
import team7.hrbank.common.exception.change_log.ChangeLogNotFoundException;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.ChangeLogRequestDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.repository.ChangeLogRepository;
import team7.hrbank.domain.employee.dto.EmployeeDto;


@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

  private final ChangeLogRepository changeLogRepository;

  //직원 생성 시 로그 저장
  @Override
  @Transactional // todo : findById 전부 수정
  public void logEmployeeCreated(EmployeeDto employee, String memo, String ipAddress) {
    List<DiffDto> details = new ArrayList<>();
    details.add(new DiffDto("hireDate", "-", employee.hireDate().toString()));
    details.add(new DiffDto("name", "-", employee.name()));
    details.add(new DiffDto("position", "-", employee.position()));
    details.add(new DiffDto("departmentName", "-", employee.departmentName()));
    details.add(new DiffDto("email", "-", employee.email()));
    details.add(new DiffDto("employeeNumber", "-", employee.employeeNumber()));
    details.add(new DiffDto("status", "-", employee.status().toString()));

    ChangeLog log = new ChangeLog(
        employee.employeeNumber(),
        ChangeLogType.CREATED,
        memo,
        ipAddress,
        details
    );
    changeLogRepository.save(log);
  }

  //직원 수정 시 로그 저장
  @Override
  @Transactional
  public void logEmployeeUpdated(EmployeeDto before, EmployeeDto after, String memo, String ipAddress) {
    List<DiffDto> details = new ArrayList<>();
    if (!before.hireDate().equals(after.hireDate())) {
      details.add(
          new DiffDto("hireDate", before.hireDate().toString(), after.hireDate().toString()));
    }
    if (!before.name().equals(after.name())) {
      details.add(new DiffDto("name", before.name(), after.name()));
    }
    if (!before.position().equals(after.position())) {
      details.add(new DiffDto("position", before.position(), after.position()));
    }
    if (!before.departmentName().equals(after.departmentName())){
      details.add(new DiffDto("departmentName", before.departmentName(), after.departmentName()));
    }
    if (!before.email().equals(after.email())) {
      details.add(new DiffDto("email", before.email(), after.email()));
    }
    if (!before.status().equals(after.status())) {
      details.add(
          new DiffDto("status", before.status().toString(), after.status().toString()));
    }

    if (details.isEmpty()) {
      throw new ChangeLogNoDetectedException();
    }

      ChangeLog log = new ChangeLog(
          after.employeeNumber(),
          ChangeLogType.UPDATED,
          memo,
          ipAddress,
          details
      );
      changeLogRepository.save(log);
  }

  //직원 삭제 시 로그 저장
  @Override
  @Transactional
  public void logEmployeeDeleted(EmployeeDto employee, String ipAddress) {
    String memo = "직원 삭제";
    String departmentName = employee.departmentName();
    List<DiffDto> details = new ArrayList<>();
    details.add(new DiffDto("hireDate", employee.hireDate().toString(), "-"));
    details.add(new DiffDto("name", employee.name(), "-"));
    details.add(new DiffDto("position", employee.position(), "-"));
    details.add(new DiffDto("departmentName", departmentName, "-"));
    details.add(new DiffDto("email", employee.email(), "-"));
    details.add(new DiffDto("status", employee.status().toString(), "-"));

    ChangeLog log = new ChangeLog(
        employee.employeeNumber(),
        ChangeLogType.DELETED,
        memo,
        ipAddress,
        details
    );
    changeLogRepository.save(log);
  }

  //수정 이력 로그 조회
  @Override
  @Transactional
  public PageResponse<ChangeLogDto> getChangeLogs(ChangeLogRequestDto dto) {

    List<ChangeLog> changeLogs = changeLogRepository.findChangeLogs(dto);

    String nextCursor = null;
    Long nextIdAfter = null;
    boolean hasNext = false;

    int totalElements = changeLogRepository.countChangeLogs(dto);

    if (changeLogs.size() > dto.size()) {
      changeLogs.remove(changeLogs.size() - 1);
      ChangeLog lastChangeLog = changeLogs.get(changeLogs.size() - 1);
      nextIdAfter = lastChangeLog.getId();
      nextCursor = lastChangeLog.getCreatedAt().toString();
      hasNext = true;
    }

    List<ChangeLogDto> content = changeLogs.stream()
        .map(ChangeLogDto::fromEntity)
        .toList();

    return new PageResponse<>(
        content,
        nextCursor,
        nextIdAfter,
        dto.size(),
        totalElements,
        hasNext
    );
  }

  //상세 정보 조회
  @Override
  @Transactional
  public List<DiffDto> getChangeLogDetails(Long id) {
    ChangeLog changeLog = changeLogRepository.findById(id)
        .orElseThrow(ChangeLogNotFoundException::new);

    return changeLog.getDetails();
  }

  //설정 날짜에 따른 수정 이력 건수 카운팅
  @Override
  public Long getChangeLogsCount(Instant fromDate, Instant toDate) {
    if(fromDate==null){
      fromDate = Instant.now().minus(7, ChronoUnit.DAYS);
    }
    if(toDate==null){
      toDate = Instant.now();
    }

    return changeLogRepository.countChangeLogs(fromDate, toDate);
  }

  @Override
  public Instant getLatestChannelLogUpdateTime() {
    ChangeLog latestLog = changeLogRepository.findFirstByOrderByCreatedAtDesc().orElse(null);
    return latestLog == null ? Instant.EPOCH : latestLog.getCreatedAt();
  }
}
