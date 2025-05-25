package com.example.sodamsodam.apps.reservation.controller;

import com.example.sodamsodam.apps.reservation.dto.ReservationListRequest;
import com.example.sodamsodam.apps.reservation.dto.ReservationListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "예약 관련 API")
public class ReservationController {

    @Operation(summary = "예약 목록 조회", description = "로그인한 사용자의 예약 목록 조회 API입니다")
    @GetMapping
    public ResponseEntity<List<ReservationListResponse>> getReservations(
            @ModelAttribute ReservationListRequest request
    ){
        // 실제 서비스 구현 전, 더미 데이터로 응답
        log.info("예약 목록 조회 요청 - status: {}, page: {}, size: {}",
                request.getStatus(), request.getPage(), request.getSize());

        List<ReservationListResponse> dummyList = List.of(
                ReservationListResponse.builder()
                        .reservationId(1L)
                        .placeName("카페 온더문")
                        .reservedAt(LocalDateTime.of(2025, 6, 1, 14, 0))
                        .status("UPCOMING")
                        .build()
        );

        return ResponseEntity.ok(dummyList);
    }
}
