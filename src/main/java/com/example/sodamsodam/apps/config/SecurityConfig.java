package com.example.sodamsodam.apps.config;

import com.example.sodamsodam.apps.user.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 기본 API 엔드포인트 허용
                        .requestMatchers("/api/v1/users/signup", "/api/v1/users/login").permitAll()

                        // 카카오 인증 엔드포인트 허용
                        .requestMatchers("/api/v1/users/kakao/**").permitAll()

                        // 장소 검색 API 허용
                        .requestMatchers("/api/v1/places/**").permitAll()

                        // 테스트 API 허용
                        .requestMatchers("/api/test/**").permitAll()

                        // Swagger UI 관련 경로들
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // 루트 경로와 파비콘
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()

                        // 정적 리소스 허용 (프론트엔드용)
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // 인증이 필요한 엔드포인트들
                        .requestMatchers("/api/v1/users/logout").authenticated()  // 기존 로그아웃
                        .requestMatchers("/api/v1/users/auth/logout").authenticated()  // 호환성 로그아웃
                        .requestMatchers("/api/v1/users/auth/verify").authenticated()  // 토큰 검증
                        .requestMatchers("/api/v1/users/me").authenticated()  // 내 정보 조회

                        // 기타 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

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