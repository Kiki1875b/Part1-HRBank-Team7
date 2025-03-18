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

    //로컬에서 테스트를 위한 IPv6 -> IPv4 변환 로직
    //todo - 로컬 아닌 환경에서 테스트 필요
    ip = request.getRemoteAddr();
    if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
      return "127.0.0.1";
    }

    return request.getRemoteAddr();
  }
}