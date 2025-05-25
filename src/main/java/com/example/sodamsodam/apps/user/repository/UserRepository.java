package com.example.sodamsodam.apps.user.repository;

import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserPersonalInfo, Long> {
    Optional<UserPersonalInfo> findByEmail(String email);
    Optional<UserPersonalInfo> findByKakaoId(String kakaoId);  // 카카오 ID로 조회

    // 중복 검사를 위한 메서드들 추가
    Optional<UserPersonalInfo> findByNickname(String nickname);
    Optional<UserPersonalInfo> findByPhoneNumber(String phoneNumber);

    // 본인을 제외한 중복 검사를 위한 메서드들 (수정 시 사용)
    Optional<UserPersonalInfo> findByNicknameAndUserIdNot(String nickname, Long userId);
    Optional<UserPersonalInfo> findByPhoneNumberAndUserIdNot(String phoneNumber, Long userId);
}