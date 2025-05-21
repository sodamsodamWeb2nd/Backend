package com.example.sodamsodam.apps.user.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class UserSignupRequest {
    private String email;
    private String password;
    private String userName;
    private boolean privacyAgreement;
}