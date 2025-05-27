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
@Schema(description = "예약 상세 정보 응답 DTO")
public class ReservationDetailResponse {
    @Schema(description = "예약 ID", example = "1")
    private Long reservationId;

    @Schema(description = "장소 이름", example = "카페 온더문")
    private String placeName;

    @Schema(description = "장소 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "예약 상태", example = "completed")
    private String status;

    @Schema(description = "예약 일시", example = "2025-06-01T14:00:00")
    private LocalDateTime reservedAt;

    public static ReservationDetailResponse from(Reservation reservation) {
        return ReservationDetailResponse.builder()
                .reservationId(reservation.getReservationId())
                .placeName(reservation.getPlaceName())
                .address(reservation.getAddress())
                .reservedAt(reservation.getReservedAt())
                .status(reservation.getStatus().name().toLowerCase())
                .build();
    }




}
