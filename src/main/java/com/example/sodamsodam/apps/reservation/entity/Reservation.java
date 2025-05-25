package com.example.sodamsodam.apps.reservation.entity;

import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @JsonProperty("reservation_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserPersonalInfo user;

    private LocalDateTime reservedAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;


    public enum ReservationStatus {
        UPCOMING, COMPLETED, CANCELLED
    }


    // 리뷰 작성 여부(리뷰 작성 가능한 예약 조건에 필요함)
    private boolean reviewed;
    public void markReviewed() {
        this.reviewed = true;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }







}
