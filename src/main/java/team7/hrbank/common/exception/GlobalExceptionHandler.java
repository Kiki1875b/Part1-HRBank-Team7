package team7.hrbank.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import team7.hrbank.common.dto.ErrorResponse;
import team7.hrbank.common.exception.binaryContent.BinaryCustomErrorResponse;
import team7.hrbank.common.exception.binaryContent.BinaryCustomException;
import team7.hrbank.common.utils.ExceptionUtil;

@Slf4j  // 500 에러 시 콘솔에 로그 찍기위한 용도
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 400 - Bad Request (IllegalArgumentException이 발생한 경우)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(IllegalArgumentException e,
      HttpServletRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
        ExceptionUtil.getRequestTime(request),  // 오류 발생 시간
        ErrorCode.BAD_REQUEST.getStatus(),      // 오류 코드
        ErrorCode.BAD_REQUEST.getMessage(),     // 오류 간단한 메시지
        e.getMessage()                          // 오류 자세한 메시지
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // 400 - Bad Request (IllegalStateException이 발생한 경우. 아무값도 안넣었을때?)
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e,
      HttpServletRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
        ExceptionUtil.getRequestTime(request),
        ErrorCode.BAD_REQUEST.getStatus(),
        ErrorCode.BAD_REQUEST.getMessage(),
        e.getMessage()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // 400 - Bad Request (validation에 의한 유효성 검사를 통과하지 못한 경우)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(MethodArgumentNotValidException e,
      HttpServletRequest request) {

    String errorMessage = e.getBindingResult().getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .findFirst()
        .orElse("잘못된 입력입니다.");

    ErrorResponse errorResponse = new ErrorResponse(
        ExceptionUtil.getRequestTime(request),
        ErrorCode.BAD_REQUEST.getStatus(),
        ErrorCode.BAD_REQUEST.getMessage(),
        errorMessage
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
  public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementException e,
      HttpServletRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
        ExceptionUtil.getRequestTime(request),
        ErrorCode.NOT_FOUND.getStatus(),
        ErrorCode.NOT_FOUND.getMessage(),
        e.getMessage()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }


  @ExceptionHandler(BackupException.class)
  public ResponseEntity<ErrorResponse> handleBackupException(BackupException e,
      HttpServletRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
        ExceptionUtil.getRequestTime(request),
        e.getErrorCode().getStatus(),
        e.getDetailMessage(),
        e.getMessage()
    );

    return new ResponseEntity<>(errorResponse,
        HttpStatusCode.valueOf(e.getErrorCode().getStatus()));
  }

  // 500 - Internal Server Error (위에서 정의하지 않은 예외가 발생하는 경우)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleInternalServerError(Exception e,
      HttpServletRequest request) {

    // 콘솔에 log 남기기
    log.error("[INTERNAL SERVER ERROR] - {} : {}", request.getRequestURI(), e.getMessage(), e);

    ErrorResponse errorResponse = new ErrorResponse(
        ExceptionUtil.getRequestTime(request),
        ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
        ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
        e.getMessage()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BinaryCustomException.class)
  public ResponseEntity<BinaryCustomErrorResponse> handleBinaryCustomException(BinaryCustomException e) {
      return BinaryCustomErrorResponse.toResponseEntity(e.getErrorCode());
  }
}