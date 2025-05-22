package com.example.sodamsodam.apps.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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

    @Schema(description = "개인정보 동의 여부", example = "true", required = true)
    private boolean privacyAgreement;
}