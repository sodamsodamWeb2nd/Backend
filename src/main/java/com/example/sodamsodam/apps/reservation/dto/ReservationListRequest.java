package com.example.sodamsodam.apps.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "예약 목록 조회 요청 DTO")
public class ReservationListRequest {

    public enum ReservationStatusType{
        UPCOMING, COMPLETED, CANCELLED

    }
    @Schema(description = "예약 상태 (upcoming, completed, canceled 중 하나)", example = "upcoming")
    private ReservationStatusType status;

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
    private int page = 1;

    @Schema(description = "페이지 당 항목 수", example = "10")
    private int size = 10;

}
