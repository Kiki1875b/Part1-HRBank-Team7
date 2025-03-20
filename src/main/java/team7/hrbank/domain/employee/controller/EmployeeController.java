package team7.hrbank.domain.employee.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.common.utils.IpUtil;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.dto.EmployeeUpdateRequest;
import team7.hrbank.domain.employee.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;
  private final HttpServletRequest request;

  // 직원 등록
  @PostMapping
  public ResponseEntity<EmployeeDto> create(
      @Valid @RequestPart(value = "employee") EmployeeCreateRequest employee,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    //IP 주소 받기
    String ipAddress = IpUtil.getClientIp(request);

    // 직원 생성 로직
    EmployeeDto employeeDto = employeeService.create(employee, profile, ipAddress);

    return ResponseEntity.ok(employeeDto);
  }

  // 직원 목록 조회
  @GetMapping
  public ResponseEntity<PageResponse<EmployeeDto>> read(
      @ModelAttribute EmployeeFindRequest request) {
    PageResponse<EmployeeDto> pageResponse = employeeService.find(request);

    return ResponseEntity.ok(pageResponse);
  }

  // 직원 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> readById(@PathVariable Long id) {

    // 조회 로직
    EmployeeDto employeeDto = employeeService.findById(id);

    return ResponseEntity.ok(employeeDto);
  }

  // 직원 수정
  @PatchMapping("/{id}")
  public ResponseEntity<EmployeeDto> update(@PathVariable Long id,
      @Valid @RequestPart(value = "employee") EmployeeUpdateRequest employee,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    //IP 주소 받기
    String ipAddress = IpUtil.getClientIp(request);
    // 직원 수정 로직
    EmployeeDto employeeDto = employeeService.updateById(id, employee, profile, ipAddress);

    return ResponseEntity.ok(employeeDto);
  }

  // 직원 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {

    //IP 주소 받기
    String ipAddress = IpUtil.getClientIp(request);
    // 삭제 로직
    employeeService.deleteById(id, ipAddress);

    return ResponseEntity.noContent().build();
  }
}
