package team7.hrbank.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    // 400 - Bad Request (입력 시 null 체크)
    // 디버깅 과정에서 HttpMessageNotReadableException로 throw되는 것 발견해서 이렇게 처리
    // Json이 잘못된 형식일 때 발생하는 예외로, request dto에서 컴팩트 생성자를 통해 예외처리를 할 때 이 메서드를 통해 처리될 것으로 예상
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(HttpMessageNotReadableException e, HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ExceptionUtil.getRequestTime(request),  // 오류 발생 시간
                ErrorCode.BAD_REQUEST.getStatus(),      // 오류 코드
                ErrorCode.BAD_REQUEST.getMessage(),     // 오류 메시지
                e.getMostSpecificCause().getMessage()   // 오류 자세한 메시지
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 404 - Not Found (엔드포인트를 찾을 수 없는 경우)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(HttpServletRequest request) {
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