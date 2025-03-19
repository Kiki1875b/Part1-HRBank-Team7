package team7.hrbank.common.exception.employee;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team7.hrbank.common.dto.ErrorResponse;
import team7.hrbank.common.exception.ErrorCode;
import team7.hrbank.common.utils.ExceptionUtil;

@RestControllerAdvice(basePackages = "team7.hrbank.domain.employee")
public class EmployeeException {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handlerEmailDuplication(DataIntegrityViolationException e, HttpServletRequest request) {
        String message = e.getMostSpecificCause().getMessage();

        // 이메일 중복 예외처리
        if (message.contains("employees_email_key")) {  // 에러 메시지에 고유 제약 조건 이름 포함되어 있을 때
            ErrorResponse errorResponse = new ErrorResponse(
                    ExceptionUtil.getRequestTime(request),
                    ErrorCode.BAD_REQUEST.getStatus(),
                    ErrorCode.BAD_REQUEST.getMessage(),
                    "이미 존재하는 이메일입니다."
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                ExceptionUtil.getRequestTime(request),
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
