package team7.hrbank.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import team7.hrbank.common.dto.ErrorResponse;
import team7.hrbank.common.utils.ExceptionUtil;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Bad Request (IllegalArgumentException이 발생한 경우)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(IllegalArgumentException e, HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ExceptionUtil.getRequestTime(request),  // 오류 발생 시간
                ErrorCode.BAD_REQUEST.getStatus(),      // 오류 코드
                ErrorCode.BAD_REQUEST.getMessage(),     // 오류 메시지
                e.getMessage()                          // 오류 자세한 메시지
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 404 - Not Found (엔드포인트를 찾을 수 없는 경우)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                ExceptionUtil.getRequestTime(request),
                ErrorCode.NOT_FOUND.getStatus(),
                ErrorCode.NOT_FOUND.getMessage(),
                "해당 경로를 찾을 수 없습니다."
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 404 - Not Found (NoSuchElementException이나 해당 예외 클래스를 상속한 예외가 발생한 경우)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementException e, HttpServletRequest request) {
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
    //  - NoSuchElementException의 경우에도 Exception을 상속받은 건 동일한데 왜 400은 안되고 404는 되지??
    // 500 - Internal Server Error
//    @ExceptionHandler(Exception.class)
//    @Order()
//    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception e, HttpServletRequest request) {
//
//        ErrorResponse errorResponse = new ErrorResponse(
//                ExceptionUtil.getRequestTime(request),
//                ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
//                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
//                e.getMessage()
//        );
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}