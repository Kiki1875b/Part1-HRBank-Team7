package team7.hrbank.csv;


import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@SpringBootTest
@Transactional
public class CsvTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntityManager em;

    private static final String FILE_NAME = "test.csv";


    @BeforeEach
    void reset() throws IOException {
        //루트 디렉토리를 초기화합니다.
        Path filePath = Path.of(FILE_NAME);
        Files.deleteIfExists(filePath);
        Files.createFile(filePath);
    }

    @Test
    void testCsv() throws IOException {

        //    private String name;
        //    private String email;
        //    private String jobTitle;
        //    private Instant hireDate;
        for (int i = 0; i < 15; i++) {
            Employee testEmployee = new Employee("name" + i, "email" + i, "jobTitle" + i, Instant.now());
            employeeRepository.save(testEmployee);
        }


        //    private String name;
        //    private String email;
        //    private String jobTitle;
        //    private Instant hireDate;
        em.flush();
        em.clear();


        ICSVWriter writer = new CSVWriterBuilder(new FileWriter(FILE_NAME))
                .withSeparator(',')
                .build();

        String[] headers = {"ID", "Name", "Email"};
        writer.writeNext(headers);

        employeeRepository.findAll().forEach(employee -> {
            String[] data = {employee.getId().toString(), employee.getName(), employee.getEmail(), employee.getJobTitle(), employee.getJobTitle(), employee.getHireDate().toString()};
            writer.writeNext(data);
        });

        writer.close();

    }
}
