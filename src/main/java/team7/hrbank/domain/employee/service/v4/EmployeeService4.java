package team7.hrbank.domain.employee.service.v4;

import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.domain.employee.dto.EmployeeFindRequest;
import team7.hrbank.domain.employee.dto.EmployeeUpdateRequest;

public interface EmployeeService4 {

  // 직원 등록
  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile, String ipAddress);

  // 직원 목록 조회
  PageResponse<EmployeeDto> find(EmployeeFindRequest request);

  // 직원 상세 조회
  EmployeeDto findById(Long id);

  // 직원 수정
  EmployeeDto updateById(Long id, EmployeeUpdateRequest request, MultipartFile profile,
      String ipAddress);

  // 직원 삭제
  void deleteById(Long id, String ipAddress);
}