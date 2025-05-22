package com.example.sodamsodam.apps.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class UserLoginRequest {
    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "사용자 비밀번호", example = "password123", required = true)
    private String password;
}