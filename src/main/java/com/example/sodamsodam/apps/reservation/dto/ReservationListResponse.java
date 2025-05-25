package com.example.sodamsodam.apps.reservation.dto;

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
    private Long reservation_id;

    @Schema(description = "장소 이름", example = "카페 온더문")
    private String place_name;

    @Schema(description = "예약 일시", example = "2025-06-01T14:00:00")
    private LocalDateTime reservation_date;

    @Schema(description = "예약 상태", example = "upcoming")
    private String status;

}
