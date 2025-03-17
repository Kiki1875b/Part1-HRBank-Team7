package team7.hrbank.domain.employee.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team7.hrbank.common.dto.PageResponse;
import team7.hrbank.domain.employee.entity.EmployeeStatus;
import team7.hrbank.domain.employee.dto.EmployeeCreateRequest;
import team7.hrbank.domain.employee.dto.EmployeeDto;
import team7.hrbank.domain.employee.dto.EmployeeUpdateRequest;
import team7.hrbank.domain.employee.service.EmployeeService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // 직원 등록
    @PostMapping
    public ResponseEntity<EmployeeDto> create(@RequestPart(value = "employee") EmployeeCreateRequest employee,
                                              @RequestPart(value = "profile", required = false) MultipartFile profile) {

        // 직원 생성 로직
        EmployeeDto employeeDto = employeeService.create(employee, profile);

        return ResponseEntity.ok(employeeDto);
    }

    // 직원 목록 조회
    @GetMapping
    public ResponseEntity<PageResponse<EmployeeDto>> read(@RequestParam(required = false) String nameOrEmail,
                                       @RequestParam(required = false) String employeeNumber,
                                       @RequestParam(required = false) String departmentName,
                                       @RequestParam(required = false) String position,
                                       @RequestParam(required = false) LocalDate hireDateFrom,
                                       @RequestParam(required = false) LocalDate hireDateTo,
                                       @RequestParam(required = false) EmployeeStatus status,
                                       @RequestParam(required = false) Long idAfter,
                                       @RequestParam(required = false) String cursor,
                                       @RequestParam(defaultValue = "25") int size,
                                       @RequestParam(defaultValue = "name") String sortField,
                                       @RequestParam(defaultValue = "asc") String sortDirection) {
        
        // 직원 목록 조회 로직
        PageResponse<EmployeeDto> pageResponse = employeeService.find(nameOrEmail, employeeNumber, departmentName, position, hireDateFrom, hireDateTo, status, idAfter, cursor, size, sortField, sortDirection);

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
                                         @RequestPart(value = "employee") EmployeeUpdateRequest employee,
                                         @RequestPart(value = "profile", required = false) MultipartFile profile) {

        // 직원 수정 로직
        EmployeeDto employeeDto = employeeService.updateById(id, employee, profile);

        return ResponseEntity.ok(employeeDto);
    }

    // 직원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        // 삭제 로직
        employeeService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
