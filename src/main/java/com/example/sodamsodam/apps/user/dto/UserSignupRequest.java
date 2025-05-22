package com.example.sodamsodam.apps.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class UserSignupRequest {
    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "사용자 비밀번호", example = "password123", required = true)
    private String password;

    @Schema(description = "사용자 이름", example = "김소담", required = true)
    private String userName;

    @Schema(description = "닉네임", example = "소담이", required = false)
    private String nickname;

    @Schema(description = "전화번호", example = "010-1234-5678", required = false)
    private String phoneNumber;

    @Schema(description = "생년월일", example = "1995-01-15", required = false)
    private LocalDate birthDate;

    @Schema(description = "개인정보 동의 여부", example = "true", required = true)
    private boolean privacyAgreement;
}