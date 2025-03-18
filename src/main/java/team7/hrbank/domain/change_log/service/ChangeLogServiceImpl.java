package team7.hrbank.domain.change_log.service;


import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import team7.hrbank.domain.department.entity.Department;
import team7.hrbank.domain.department.repository.DepartmentRepository;
import team7.hrbank.domain.change_log.dto.ChangeLogDto;
import team7.hrbank.domain.change_log.dto.DiffDto;
import team7.hrbank.domain.change_log.entity.ChangeLog;
import team7.hrbank.domain.change_log.entity.ChangeLogType;
import team7.hrbank.domain.change_log.repository.ChangeLogRepository;
import team7.hrbank.domain.employee.entity.Employee;


@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  private final DepartmentRepository departmentRepository;

  @Override
  @Transactional
  public void logEmployeeCreated(Employee employee, String memo, String ipAddress) {
    Department department = departmentRepository.findById(employee.getDepartmentId())
        .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다.")); //todo-전역예외처리

    List<DiffDto> details = new ArrayList<>();
    details.add(new DiffDto("hireDate", "-", employee.getHireDate().toString()));
    details.add(new DiffDto("name", "-", employee.getName()));
    details.add(new DiffDto("position", "-", employee.getPosition()));
    details.add(new DiffDto("departmentName", "-", department.getName()));
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
    Department departmentBefore = departmentRepository.findById(before.getDepartmentId())
        .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다.")); //todo-전역예외처리
    Department departmentAfter = departmentRepository.findById(after.getDepartmentId())
        .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다.")); //todo-전역예외처리

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
    if (!departmentBefore.getName().equals(departmentAfter.getName())){
      details.add(new DiffDto("departmentName", departmentBefore.getName(), departmentAfter.getName()));
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
  public void logEmployeeDeleted(Employee employee, String memo, String ipAddress) {
    Department department = departmentRepository.findById(employee.getDepartmentId())
        .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다.")); //todo-전역예외처리

    List<DiffDto> details = new ArrayList<>();
    details.add(new DiffDto("hireDate", employee.getHireDate().toString(), "-"));
    details.add(new DiffDto("name", employee.getName(), "-"));
    details.add(new DiffDto("position", employee.getPosition(), "-"));
    details.add(new DiffDto("departmentName", department.getName(), "-"));
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
  public Page<ChangeLogDto> getChangeLogs(
      String employeeNumber,
      ChangeLogType type,
      String memo,
      String ipAddress,
      Instant atFrom,
      Instant atTo,
      Long idAfter,
      Integer size,
      String sortField,
      String sortDirection,
      Pageable pageable) {

    if (!sortField.equals("ipAddress") && !sortField.equals("createdAt")) {
      sortField = "createdAt";
    }

    Sort.Direction direction =
        sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

    Pageable sortedPageable = (pageable == null || pageable.isUnpaged())
        ? PageRequest.of(0, size, Sort.by(direction, sortField))
        : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            Sort.by(direction, sortField));

    Page<ChangeLog> changeLogs = changeLogRepository.searchChangeLogs(
        employeeNumber, type, memo, ipAddress, atFrom, atTo, idAfter, sortedPageable);

    return changeLogs.map(changeLog -> new ChangeLogDto(
        changeLog.getId(),
        changeLog.getType(),
        changeLog.getEmployee().getEmployeeNumber(),
        changeLog.getMemo(),
        changeLog.getIpAddress(),
        changeLog.getCreatedAt()
    ));
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
