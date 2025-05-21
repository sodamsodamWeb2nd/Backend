package com.example.sodamsodam.apps.user.dto;

import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import lombok.*;

@Getter
public class UserResponse {
    private Long userId;
    private String email;
    private String userName;

    public UserResponse(UserPersonalInfo user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.userName = user.getUserName();
    }
}
