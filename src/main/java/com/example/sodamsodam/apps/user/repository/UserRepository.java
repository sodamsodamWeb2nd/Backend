package com.example.sodamsodam.apps.user.repository;

import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserPersonalInfo, Long> {
    Optional<UserPersonalInfo> findByEmail(String email);
    Optional<UserPersonalInfo> findByKakaoId(String kakaoId);  // 카카오 ID로 조회
}