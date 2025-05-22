package com.example.sodamsodam.apps.user.jwt;

import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import com.example.sodamsodam.apps.user.repository.UserRepository;
import com.example.sodamsodam.apps.user.service.TokenBlacklistService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);

            // 토큰이 있고 유효한 경우에만 블랙리스트 확인
            if (token != null && jwtProvider.validateToken(token)) {

                // 블랙리스트 확인 (안전하게 처리)
                boolean isBlacklisted = false;
                try {
                    isBlacklisted = tokenBlacklistService.isBlacklisted(token);
                } catch (Exception e) {
                    log.warn("블랙리스트 확인 중 오류 발생 (무시하고 계속 진행): {}", e.getMessage());
                    // Redis 오류가 발생해도 계속 진행
                    isBlacklisted = false;
                }

                // 블랙리스트에 없는 경우에만 인증 처리
                if (!isBlacklisted) {
                    try {
                        Long userId = jwtProvider.getUserIdFromToken(token);
                        UserPersonalInfo user = userRepository.findById(userId).orElse(null);

                        if (user != null) {
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(user, null, List.of());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            log.debug("사용자 인증 성공: {}", user.getEmail());
                        }
                    } catch (Exception e) {
                        log.warn("사용자 인증 처리 중 오류: {}", e.getMessage());
                        // 인증 실패해도 필터 체인은 계속 진행
                    }
                } else {
                    log.debug("블랙리스트에 있는 토큰: {}", token.substring(0, 10) + "...");
                }
            }
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 예외 발생: {}", e.getMessage());
            // 예외가 발생해도 필터 체인은 계속 진행
        }

        // 항상 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}