package team7.hrbank.common.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {
  public static String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
      return ip.split(",")[0].trim();
    }

    ip = request.getHeader("Proxy-Client-IP");
    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
      return ip;
    }

    ip = request.getHeader("WL-Proxy-Client-IP");
    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
      return ip;
    }

    return request.getRemoteAddr();
  }
}