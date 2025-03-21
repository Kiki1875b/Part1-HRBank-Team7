package team7.hrbank;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 모든 경로에 대해
//                .allowedOrigins("http://localhost:8080", "https://project-hrbank-production.up.railway.app") // 허용할 도메인 (Swagger UI가 실행되는 도메인)
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP 메서드
//                .allowedHeaders("*") // 모든 헤더 허용
//                .allowCredentials(true); // 쿠키를 허용하려면 true로 설정
//    }
//}
