package com.example.sodamsodam.apps.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "user_personal_info")
@Getter @Setter
@NoArgsConstructor
public class UserPersonalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;
    private String password;
    private String userName;
    private String kakaoId;
    private String nickname;
    private String phoneNumber;
    private LocalDate birthDate;
    private boolean privacyAgreement;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}