package com.example.sodamsodam.apps.user.service;

import com.example.sodamsodam.apps.user.dto.KakaoUserInfo;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import com.example.sodamsodam.apps.user.jwt.JwtProvider;
import com.example.sodamsodam.apps.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client.id:}")
    private String clientId;

    @Value("${kakao.redirect.uri:http://localhost:8080/api/users/kakao/callback}")
    private String redirectUri;

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 카카오 인가 코드로 액세스 토큰 받기
    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        // 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            log.error("카카오 액세스 토큰 획득 실패", e);
            throw new RuntimeException("카카오 로그인 실패");
        }
    }

    // 액세스 토큰으로 사용자 정보 가져오기
    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, request, String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            // 카카오 사용자 정보 파싱
            Long kakaoId = jsonNode.get("id").asLong();
            String email = null;
            String nickname = null;

            // 카카오 계정 정보
            if (jsonNode.has("kakao_account")) {
                JsonNode kakaoAccount = jsonNode.get("kakao_account");

                // 이메일 정보 (동의한 경우에만 제공됨)
                if (kakaoAccount.has("email")) {
                    email = kakaoAccount.get("email").asText();
                }

                // 프로필 정보
                if (kakaoAccount.has("profile")) {
                    JsonNode profile = kakaoAccount.get("profile");
                    if (profile.has("nickname")) {
                        nickname = profile.get("nickname").asText();
                    }
                }
            }

            return new KakaoUserInfo(kakaoId.toString(), email, nickname);

        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패", e);
            throw new RuntimeException("카카오 사용자 정보 조회 실패");
        }
    }

    // 카카오 로그인 처리 (신규 가입 또는 기존 회원 로그인)
    public String processKakaoLogin(String code) {
        // 1. 인가 코드로 액세스 토큰 받기
        String accessToken = getAccessToken(code);

        // 2. 액세스 토큰으로 사용자 정보 가져오기
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 카카오 ID로 기존 회원 조회
        Optional<UserPersonalInfo> existingUser = userRepository.findByKakaoId(kakaoUserInfo.getKakaoId());

        UserPersonalInfo user;
        if (existingUser.isPresent()) {
            // 기존 회원인 경우
            user = existingUser.get();
            log.info("기존 카카오 회원 로그인: {}", user.getEmail());
        } else {
            // 신규 회원인 경우 자동 가입
            user = new UserPersonalInfo();
            user.setKakaoId(kakaoUserInfo.getKakaoId());
            user.setEmail(kakaoUserInfo.getEmail() != null ?
                    kakaoUserInfo.getEmail() : "kakao_" + kakaoUserInfo.getKakaoId() + "@kakao.com");
            user.setUserName(kakaoUserInfo.getNickname() != null ?
                    kakaoUserInfo.getNickname() : "카카오사용자");
            user.setNickname(kakaoUserInfo.getNickname());
            user.setPrivacyAgreement(true); // 카카오 로그인 시 기본 동의
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);
            log.info("신규 카카오 회원 가입: {}", user.getEmail());
        }

        // 4. JWT 토큰 생성 및 반환
        return jwtProvider.generateToken(user.getUserId());
    }
}