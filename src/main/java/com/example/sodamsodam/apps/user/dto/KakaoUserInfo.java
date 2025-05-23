package com.example.sodamsodam.apps.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfo {
    private String kakaoId;      // 카카오 고유 ID
    private String email;        // 이메일 (동의한 경우에만 제공)
    private String nickname;     // 닉네임
}