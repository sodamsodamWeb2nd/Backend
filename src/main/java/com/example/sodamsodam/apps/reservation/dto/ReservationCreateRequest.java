package com.example.sodamsodam.apps.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "예약 생성 요청 DTO")
public class ReservationCreateRequest {
    @Schema(description = "유저 ID", required = true)
    private Long userId;

    @Schema(description = "카카오 장소 ID", required = true)
    private String kakaoPlaceId;

    @Schema(description = "장소 이름",required = true)
    private String placeName;

    @Schema(description = "장소 주소", required = true)
    private String address;

    @Schema(description = "예약 일시", example = "2025-06-01T14:00:00",required = true)
    private LocalDateTime reservedAt;

}
