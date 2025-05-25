package com.example.sodamsodam.apps.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Schema(description = "예약 취소 응답 DTO")
public class ReservationCancelResponse {

    @Schema(description = "예약 ID", example = "1")
    private Long reservationId;

    @Schema(description = "응답 메시지", example = "예약이 취소 완료")
    private String message;

    @Schema(description = "예약 상태", example = "canceled")
    private String status;

}
