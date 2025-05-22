package com.example.sodamsodam.apps.user.dto;

import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Schema(description = "사용자 정보 응답 DTO")
public class UserResponse {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사용자 이름", example = "김소담")
    private String userName;

    public UserResponse(UserPersonalInfo user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.userName = user.getUserName();
    }
}