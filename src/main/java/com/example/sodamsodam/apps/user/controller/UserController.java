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

    @Value("${kakao.redirect.uri:}")
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
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        // HTTP 헤더에서 토큰 추출
        String token = resolveToken(request);
        userService.logout(token);

        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃 성공");
        return ResponseEntity.ok(response);
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
    public ResponseEntity<Map<String, Object>> kakaoCallback(@RequestParam String code) {
        try {
            log.info("카카오 로그인 콜백 처리 시작 - code: {}", code);

            // 카카오 로그인 처리 및 JWT 토큰 생성
            String jwt = kakaoService.processKakaoLogin(code);

            // JSON 응답으로 토큰 반환
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", jwt);
            response.put("tokenType", "Bearer");
            response.put("message", "카카오 로그인 성공");

            log.info("카카오 로그인 성공");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "카카오 로그인 처리 중 오류가 발생했습니다.");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    // HTTP 헤더에서 토큰을 추출하는 헬퍼 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}