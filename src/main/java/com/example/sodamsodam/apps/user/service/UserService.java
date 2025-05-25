package com.example.sodamsodam.apps.user.service;

import com.example.sodamsodam.apps.user.dto.*;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import com.example.sodamsodam.apps.user.jwt.JwtProvider;
import com.example.sodamsodam.apps.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.sodamsodam.apps.common.exception.AuthenticationException;
import com.example.sodamsodam.apps.common.exception.DuplicateEmailException;
import com.example.sodamsodam.apps.common.exception.DuplicateNicknameException;
import com.example.sodamsodam.apps.common.exception.DuplicatePhoneNumberException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;

    public void signup(UserSignupRequest request) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 중복 검사 (닉네임이 있는 경우만)
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            if (userRepository.findByNickname(request.getNickname()).isPresent()) {
                throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
            }
        }

        // 전화번호 중복 검사 (전화번호가 있는 경우만)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
                throw new DuplicatePhoneNumberException("이미 사용 중인 전화번호입니다.");
            }
        }

        UserPersonalInfo user = new UserPersonalInfo();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserName(request.getUserName());
        user.setNickname(request.getNickname());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBirthDate(request.getBirthDate());
        user.setPrivacyAgreement(request.isPrivacyAgreement());
        user.setCreatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
    }

    public String login(UserLoginRequest request) {
        UserPersonalInfo user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("사용자를 찾을 수 없습니다"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("이메일 또는 비밀번호가 일치하지 않습니다");
        }
        return jwtProvider.generateToken(user.getUserId());
    }

    // 로그아웃 메서드
    public void logout(String token) {
        if (token != null && jwtProvider.validateToken(token)) {
            // 토큰의 만료시간을 가져와서 블랙리스트에 추가
            long expirationTime = jwtProvider.getExpirationFromToken(token).getTime();
            tokenBlacklistService.addToBlacklist(token, expirationTime);
        }
    }

    // 회원정보 수정 메서드 추가
    @Transactional
    public UserResponse updateUserInfo(Long userId, UserUpdateRequest request) {
        log.info("회원정보 수정 시작 - 사용자 ID: {}", userId);

        // 1. 요청이 비어있는지 확인
        if (request.isEmpty()) {
            throw new IllegalArgumentException("수정할 정보가 없습니다.");
        }

        // 2. 사용자 조회
        UserPersonalInfo user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("사용자를 찾을 수 없습니다."));

        // 3. 닉네임 중복 검사 및 업데이트
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            String newNickname = request.getNickname().trim();

            // 현재 닉네임과 다른 경우에만 중복 검사
            if (!newNickname.equals(user.getNickname())) {
                Optional<UserPersonalInfo> existingUser =
                        userRepository.findByNicknameAndUserIdNot(newNickname, userId);
                if (existingUser.isPresent()) {
                    throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
                }
                user.setNickname(newNickname);
                log.info("닉네임 수정: {} -> {}", user.getNickname(), newNickname);
            }
        }

        // 4. 전화번호 중복 검사 및 업데이트
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            String newPhoneNumber = request.getPhoneNumber().trim();

            // 현재 전화번호와 다른 경우에만 중복 검사
            if (!newPhoneNumber.equals(user.getPhoneNumber())) {
                Optional<UserPersonalInfo> existingUser =
                        userRepository.findByPhoneNumberAndUserIdNot(newPhoneNumber, userId);
                if (existingUser.isPresent()) {
                    throw new DuplicatePhoneNumberException("이미 사용 중인 전화번호입니다.");
                }
                user.setPhoneNumber(newPhoneNumber);
                log.info("전화번호 수정: {} -> {}", user.getPhoneNumber(), newPhoneNumber);
            }
        }

        // 5. 수정 시간 업데이트
        user.setUpdatedAt(java.time.LocalDateTime.now());

        // 6. 저장 및 응답 반환
        UserPersonalInfo savedUser = userRepository.save(user);
        log.info("회원정보 수정 완료 - 사용자 ID: {}", userId);

        return new UserResponse(savedUser);
    }
}