package team7.hrbank;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import team7.hrbank.domain.Department.DepartmentService;
import team7.hrbank.domain.Department.dto.DepartmentCreateRequest;

@SpringBootApplication
public class HrbankApplication {

    public static void main(String[] args) { SpringApplication.run(HrbankApplication.class, args);}

}
