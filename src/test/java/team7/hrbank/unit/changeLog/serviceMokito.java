package team7.hrbank.unit.changeLog;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class serviceMokito {


    @Test
    void create(){
        // given
        // when
        // then
    }
}


//public record EmployeeDto(
//    Long id,
//    String name,
//    String email,
//    String employeeNumber,
//    Long departmentId,
//    String departmentName,
//    String position,
//    LocalDate hireDate,
//    EmployeeStatus status,
//    Long profileImageId
//) {
//
//  // 컴팩트 생성자
//  public EmployeeDto {
//    if (profileImageId == -1L) {
//      profileImageId = null;
//    }
//  }