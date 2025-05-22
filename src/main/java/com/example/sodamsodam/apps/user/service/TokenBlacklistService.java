package com.example.sodamsodam.apps.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;

    public void addToBlacklist(String token, long expirationTime) {
        try {
            // 토큰의 남은 유효시간만큼만 블랙리스트에 보관
            long remainingTime = expirationTime - System.currentTimeMillis();
            if (remainingTime > 0) {
                String key = "blacklist:" + token;
                redisTemplate.opsForValue().set(key, "true", remainingTime, TimeUnit.MILLISECONDS);
                log.info("토큰이 블랙리스트에 추가되었습니다. 만료시간: {}ms", remainingTime);
            }
        } catch (Exception e) {
            log.error("Redis 연결 실패로 토큰을 블랙리스트에 추가할 수 없습니다: {}", e.getMessage());
            // Redis 실패 시에도 애플리케이션은 계속 동작
            // 실제 운영환경에서는 다른 저장소를 사용하거나 알림을 보낼 수 있습니다
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            String key = "blacklist:" + token;
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Redis 연결 실패로 블랙리스트 확인이 불가능합니다: {}", e.getMessage());
            // Redis 실패 시 안전하게 false 반환 (보안상 더 엄격하게 하려면 true 반환도 가능)
            return false;
        }
    }

    // Redis 연결 상태 확인 메서드
    public boolean isRedisAvailable() {
        try {
            redisTemplate.opsForValue().set("health:check", "ok", 1, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.warn("Redis 연결 상태: 비정상 - {}", e.getMessage());
            return false;
        }
    }
}