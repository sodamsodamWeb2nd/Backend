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
@Table(name = "reservation")
public class Reservation {
    @JsonProperty("reservation_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserPersonalInfo user;

    @Column(name = "kakao_place_id")
    private String kakaoPlaceId;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "address")
    private String address;

    private LocalDateTime reservedAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;


    public enum ReservationStatus {
        UPCOMING, COMPLETED, CANCELLED
    }


    // 리뷰 작성 여부(리뷰 작성 가능한 예약 조건에 필요함)
    //리뷰작성 완료시 해당 예약의 reviewed필드를 true로 저장해줘야됨 -> COMPLETED로 대체
    private boolean reviewed;


    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }







}
