package team7.hrbank.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

public class ExceptionUtil {

    // 예외 발생한 요청 시간 반환
    public static Instant getRequestTime(HttpServletRequest request) {
        return Instant.ofEpochMilli(request.getAttribute("startTime") != null
                ? (long) request.getAttribute("startTime")
                : System.currentTimeMillis());
    }
}
