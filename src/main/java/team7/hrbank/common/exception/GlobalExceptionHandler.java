package team7.hrbank.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team7.hrbank.common.dto.ErrorResponse;
import team7.hrbank.common.utils.ExceptionUtil;

import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(IllegalArgumentException e, HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ExceptionUtil.getRequestTime(request),            // 오류 발생 시간
                ErrorCode.BAD_REQUEST.getStatus(),  // 오류 코드
                ErrorCode.BAD_REQUEST.getMessage(), // 오류 메시지
                e.getMessage()                      // 오류 자세한 메시지
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 404 - Not Found
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundEmployee(NoSuchElementException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                ExceptionUtil.getRequestTime(request),
                ErrorCode.NOT_FOUND.getStatus(),
                ErrorCode.NOT_FOUND.getMessage(),
                e.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 위에 정의해둔 400이 나와야 할 때도 500 나와서 주석 처리
    //  - 404의 경우 잘 뜸
    //  - URL 잘못 작성했을 때 404 나와야하는데 500 나옴
    // 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                ExceptionUtil.getRequestTime(request),
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                e.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}