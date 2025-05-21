package com.example.sodamsodam.apps.user.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class UserLoginRequest {
    private String email;
    private String password;
}
