package team7.hrbank.common.exception.binaryContent;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

@Data
@Builder
public class BinaryCustomErrorResponse {

    private Instant timestamp;
    private int status;
    private String message;
    private String details;

    public static ResponseEntity<BinaryCustomErrorResponse> toResponseEntity(ErrorCode e) {
        HttpStatus eStatus = e.getStatus();
        return ResponseEntity
                .status(eStatus)
                .header(MediaType.APPLICATION_JSON_VALUE)
                .body(BinaryCustomErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(eStatus.value())
                        .message(e.getMessage())
                        .details(eStatus.getReasonPhrase())
                        .build());
    }
}


//{
//  "timestamp": "2025-03-06T05:39:06.152068Z",
//  "status": 400,
//  "message": "잘못된 요청입니다.",
//  "details": "부서 코드는 필수입니다."
//}