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