package com.example.sodamsodam.apps.reservation.dto;

import com.example.sodamsodam.apps.reservation.entity.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "예약 목록 응답 DTO")
public class ReservationListResponse {

    @Schema(description =  "예약 ID", example = "1")
    private Long reservationId;

    @Schema(description = "장소 이름", example = "카페 온더문")
    private String placeName;

    @Schema(description = "장소 주소", example = "주소 정보")
    private String address;

    @Schema(description = "예약 일시", example = "2025-06-01T14:00:00")
    private LocalDateTime reservedAt;

    @Schema(description = "예약 상태", example = "upcoming")
    private String status;

    public static ReservationListResponse from(Reservation reservation) {
        return ReservationListResponse.builder()
                .reservationId(reservation.getReservationId())
                .placeName(reservation.getPlaceName())
                .reservedAt(reservation.getReservedAt())
                .status(reservation.getStatus().name().toLowerCase())
                .build();
    }
}
