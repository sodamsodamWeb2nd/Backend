package com.example.sodamsodam.apps.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원정보 수정 요청 DTO")
public class UserUpdateRequest {

    @Schema(description = "수정할 닉네임", example = "새로운닉네임", required = false)
    private String nickname;

    @Schema(description = "수정할 전화번호", example = "010-9876-5432", required = false)
    private String phoneNumber;

    // 두 필드 모두 비어있는지 확인하는 메서드
    public boolean isEmpty() {
        return (nickname == null || nickname.trim().isEmpty()) &&
                (phoneNumber == null || phoneNumber.trim().isEmpty());
    }
}