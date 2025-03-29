package team7.hrbank.domain.employee.service.v4.support;
import org.springframework.util.StringUtils;

public class EmployeeNumberGenerator {

    public String generateEmployeeNumber(String lastEmployeeNumber) {
        long lastNumber = 0;
        Integer year = Integer.valueOf(lastEmployeeNumber.split("-")[1]);
        if (StringUtils.hasText(lastEmployeeNumber)) {
            lastNumber = Long.parseLong(
                    lastEmployeeNumber.split("-")[2]);     // EMP-YYYY-001에서 001 부분 분리하여 long 타입으로 변환}
        }
        return String.format("EMP-%d-%03d", year, lastNumber + 1);
    }
}
