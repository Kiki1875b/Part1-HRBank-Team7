package team7.hrbank.unit.employee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import team7.hrbank.domain.employee.mapper.EmployeeMapperImpl;
import team7.hrbank.domain.employee.service.EmployeeServicePojo;


@ExtendWith(MockitoExtension.class)
public class PojoServiceTest {


    @Spy
    private EmployeeMapperImpl employeeMapper = new EmployeeMapperImpl();

    @InjectMocks
    private EmployeeServicePojo employeeServicePojo;

    @Test
    void getEmployeeNumber(){
        // given
        System.out.println("getEmployeeNumber");
        // when

        // then

    }

    @Test
    void createWithProfile(){

    }

}
