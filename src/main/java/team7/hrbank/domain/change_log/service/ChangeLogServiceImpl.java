package team7.hrbank.domain.change_log.service;


import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.CursorPageResponseChangeLogDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.repository.ChangeLogRepository;
import team7.hrbank.domain.employee.entity.Employee;


@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

  private final ChangeLogRepository changeLogRepository;

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

  @Override
  @Transactional
  public CursorPageResponseChangeLogDto<ChangeLogDto> getChangeLogs(
      String employeeNumber,
      ChangeLogType type,
      String memo,
      String ipAddress,
      Instant atFrom,
      Instant atTo,
      Long idAfter,
      Integer size,
      String sortField,
      String sortDirection) {

    if (!sortField.equals("ipAddress") && !sortField.equals("createdAt")) {
      sortField = "createdAt";
    }

    Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

    Pageable pageable = PageRequest.of(0, size + 1, Sort.by(direction, sortField));

    List<ChangeLog> changeLogs = changeLogRepository.findChangeLogs(
        employeeNumber, type, memo, ipAddress, atFrom, atTo, idAfter, pageable);

    boolean hasNext = changeLogs.size() > size;
    List<ChangeLogDto> content = changeLogs.stream()
        .limit(size)
        .map(changeLog -> new ChangeLogDto(
            changeLog.getId(),
            changeLog.getType(),
            changeLog.getEmployee().getEmployeeNumber(),
            changeLog.getMemo(),
            changeLog.getIpAddress(),
            changeLog.getCreatedAt()
        ))
        .toList();

    Long lastId =  hasNext ? content.get(content.size() - 1).id() : null;

    return new CursorPageResponseChangeLogDto<>(
        content,
        lastId != null ? String.valueOf(lastId) : null,
        lastId,
        size,
        content.size(),
        hasNext
    );
  }

  @Override
  @Transactional
  public List<DiffDto> getChangeLogDetails(Long id) {
    ChangeLog changeLog = changeLogRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("이력을 찾을 수 없습니다."));

    return changeLog.getDetails();
  }

  @Override
  public Instant getLatestChannelLogUpdateTime() {
    ChangeLog latestLog = changeLogRepository.findFirstByOrderByCreatedAtDesc().orElse(null);
    return latestLog == null ? Instant.EPOCH : latestLog.getCreatedAt();
  }
}
