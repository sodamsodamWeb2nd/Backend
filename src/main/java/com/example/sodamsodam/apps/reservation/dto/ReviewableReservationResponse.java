package com.example.sodamsodam.apps.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "리뷰 작성 가능한 예약 응답 DTO")
public class ReviewableReservationResponse {
    @Schema(description = "예약ID", example = "2")
    private Long reservationId;

    @Schema(description = "장소 이름", example = "루프탑 바")
    private String placeName;

    @Schema(description = "방문 일시", example = "2025-05-10T19:00:00")
    private LocalDateTime visitedAt;

    @Schema(description = "리뷰 작성 여부", example = "false")
    private boolean reviewed;

}
