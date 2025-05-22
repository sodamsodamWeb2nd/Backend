package com.example.sodamsodam.apps.user.config;

import com.example.sodamsodam.apps.user.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Security 설정 초기화 중...");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    log.info("HTTP 요청 권한 설정 중...");
                    auth
                            // 인증이 필요 없는 엔드포인트들
                            .requestMatchers("/api/users/signup", "/api/users/login").permitAll()
                            .requestMatchers("/api/health").permitAll()  // 헬스체크도 허용

                            // Swagger UI 관련 경로들
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/swagger-ui.html").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll()
                            .requestMatchers("/swagger-resources/**").permitAll()
                            .requestMatchers("/webjars/**").permitAll()

                            // 기본 경로들
                            .requestMatchers("/").permitAll()
                            .requestMatchers("/favicon.ico").permitAll()
                            .requestMatchers("/error").permitAll()  // 에러 페이지도 허용

                            // 로그아웃은 인증이 필요한 엔드포인트
                            .requestMatchers("/api/users/logout").authenticated()

                            // 기타 모든 요청은 인증 필요
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("Security 설정 완료");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}