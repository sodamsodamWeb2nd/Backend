package com.example.sodamsodam.apps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 오리진 (프론트엔드 주소)
        configuration.addAllowedOrigin("http://localhost:5173"); // Vite 개발 서버
        configuration.addAllowedOrigin("http://127.0.0.1:5173"); // 다른 형태의 로컬호스트
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:8080");

        // 허용할 HTTP 메서드
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");

        // 허용할 헤더
        configuration.addAllowedHeader("*");

        // 인증 정보 포함 허용 (JWT 토큰 등)
        configuration.setAllowCredentials(true);

        // 브라우저가 캐시할 preflight 요청 시간 (초)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용

        return source;
    }
}