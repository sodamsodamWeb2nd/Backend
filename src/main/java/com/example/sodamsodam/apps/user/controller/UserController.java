package com.example.sodamsodam.apps.user.controller;

import com.example.sodamsodam.apps.user.dto.*;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import com.example.sodamsodam.apps.user.service.UserService;
import com.example.sodamsodam.apps.user.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import java.net.URI;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 인증 및 관리 API")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;

    @Value("${kakao.client.id:}")
    private String clientId;

    @Value("${kakao.redirect.uri:http://localhost:8080/api/users/kakao/callback}")
    private String redirectUri;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일/닉네임/전화번호")
    })
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 인증하고 JWT 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest request) {
        String jwt = userService.login(request);
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @Operation(summary = "로그아웃", description = "현재 토큰을 무효화하여 로그아웃합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // HTTP 헤더에서 토큰 추출
        String token = resolveToken(request);
        userService.logout(token);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserPersonalInfo user) {
        return ResponseEntity.ok(new UserResponse(user));
    }

    // 회원정보 수정 엔드포인트 추가
    @Operation(summary = "회원정보 수정", description = "현재 로그인한 사용자의 닉네임과 전화번호를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임 또는 전화번호")
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUserInfo(
            @AuthenticationPrincipal UserPersonalInfo user,
            @RequestBody UserUpdateRequest request) {

        log.info("회원정보 수정 요청 - 사용자: {}, 요청 데이터: nickname={}, phoneNumber={}",
                user.getEmail(), request.getNickname(), request.getPhoneNumber());

        UserResponse updatedUser = userService.updateUserInfo(user.getUserId(), request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "카카오 로그인 페이지 URL 조회",
            description = "카카오 로그인 페이지로 리다이렉트할 URL을 반환합니다.")
    @GetMapping("/kakao/login-url")
    public ResponseEntity<Map<String, String>> getKakaoLoginUrl() {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";

        Map<String, String> response = new HashMap<>();
        response.put("loginUrl", kakaoLoginUrl);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카카오 로그인 콜백",
            description = "카카오 인가 코드를 받아 로그인 처리 후 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인가 코드"),
            @ApiResponse(responseCode = "500", description = "카카오 API 오류")
    })

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        try {
            String jwt = kakaoService.processKakaoLogin(code);

            // 로그인 성공 시 프론트엔드로 리다이렉트 (토큰 포함)
            String redirectUrl = "http://localhost:3000/login/success?token=" + jwt;

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류", e);
            // 실패 시 프론트엔드 로그인 페이지로 리다이렉트
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("http://localhost:3000/login?error=kakao_login_failed"));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    // HTTP 헤더에서 토큰을 추출하는 헬퍼 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}