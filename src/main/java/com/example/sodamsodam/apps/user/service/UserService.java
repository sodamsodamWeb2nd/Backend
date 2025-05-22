package com.example.sodamsodam.apps.user.service;

import com.example.sodamsodam.apps.user.dto.*;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import com.example.sodamsodam.apps.user.jwt.JwtProvider;
import com.example.sodamsodam.apps.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.sodamsodam.apps.common.exception.AuthenticationException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;

    public void signup(UserSignupRequest request) {
        UserPersonalInfo user = new UserPersonalInfo();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserName(request.getUserName());
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

    // 로그아웃 메서드 추가
    public void logout(String token) {
        if (token != null && jwtProvider.validateToken(token)) {
            // 토큰의 만료시간을 가져와서 블랙리스트에 추가
            long expirationTime = jwtProvider.getExpirationFromToken(token).getTime();
            tokenBlacklistService.addToBlacklist(token, expirationTime);
        }
    }
}