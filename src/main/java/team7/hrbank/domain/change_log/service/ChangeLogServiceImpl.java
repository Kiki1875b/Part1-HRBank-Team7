package team7.hrbank.domain.change_log.service;


import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.ChangeLogRequestDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.repository.ChangeLogRepository;
import team7.hrbank.domain.employee.entity.Employee;


@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

  private final ChangeLogRepository changeLogRepository;

  //직원 생성 시 로그 저장
  @Override
  @Transactional // todo : findById 전부 수정
  public void logEmployeeCreated(Employee employee, String memo, String ipAddress) {
    List<DiffDto> details = new ArrayList<>();
    details.add(new DiffDto("hireDate", "-", employee.getHireDate().toString()));
    details.add(new DiffDto("name", "-", employee.getName()));
    details.add(new DiffDto("position", "-", employee.getPosition()));
    details.add(new DiffDto("departmentName", "-", employee.getDepartment().getName()));
    details.add(new DiffDto("email", "-", employee.getEmail()));
    details.add(new DiffDto("employeeNumber", "-", employee.getEmployeeNumber()));
    details.add(new DiffDto("status", "-", employee.getStatus().toString()));

    ChangeLog log = new ChangeLog(
        employee,
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
  public void logEmployeeUpdated(Employee before, Employee after, String memo, String ipAddress) {
    List<DiffDto> details = new ArrayList<>();
    if (!before.getHireDate().equals(after.getHireDate())) {
      details.add(
          new DiffDto("hireDate", before.getHireDate().toString(), after.getHireDate().toString()));
    }
    if (!before.getName().equals(after.getName())) {
      details.add(new DiffDto("name", before.getName(), after.getName()));
    }
    if (!before.getPosition().equals(after.getPosition())) {
      details.add(new DiffDto("position", before.getPosition(), after.getPosition()));
    }
    if (!before.getDepartment().getName().equals(after.getDepartment().getName())){
      details.add(new DiffDto("departmentName", before.getDepartment().getName(), after.getDepartment().getName()));
    }
    if (!before.getEmail().equals(after.getEmail())) {
      details.add(new DiffDto("email", before.getEmail(), after.getEmail()));
    }
    if (!before.getStatus().equals(after.getStatus())) {
      details.add(
          new DiffDto("status", before.getStatus().toString(), after.getStatus().toString()));
    }

    if (details.isEmpty()) {
      throw new IllegalStateException("변경된 사항이 없습니다.");
    }

      ChangeLog log = new ChangeLog(
          after,
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
  public void logEmployeeDeleted(Employee employee, String ipAddress) {
    String memo = "직원 삭제";
    List<DiffDto> details = new ArrayList<>();
    details.add(new DiffDto("hireDate", employee.getHireDate().toString(), "-"));
    details.add(new DiffDto("name", employee.getName(), "-"));
    details.add(new DiffDto("position", employee.getPosition(), "-"));
    details.add(new DiffDto("departmentName", employee.getDepartment().getName(), "-"));
    details.add(new DiffDto("email", employee.getEmail(), "-"));
    details.add(new DiffDto("status", employee.getStatus().toString(), "-"));

    ChangeLog log = new ChangeLog(
        employee,
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
        .orElseThrow(() -> new NoSuchElementException("이력을 찾을 수 없습니다."));

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
