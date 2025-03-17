package team7.hrbank.common.exception.employee;


import java.util.NoSuchElementException;

public class NotFoundEmployeeException extends NoSuchElementException {
    public NotFoundEmployeeException() {
        super("존재하지 않는 직원입니다.");
    }
}
