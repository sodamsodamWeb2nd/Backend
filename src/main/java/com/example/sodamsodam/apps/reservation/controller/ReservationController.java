package com.example.sodamsodam.apps.reservation.controller;

import com.example.sodamsodam.apps.reservation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @Operation(summary = "예약 목록 상세 조회", description = "로그인한 사용자의 예약 상세 정보 조회 API 입니다")
    @GetMapping("/{reservation_id}")
    public ResponseEntity<ReservationDetailResponse> getReservationDetail(
            //@PathVariable Long reservationId

    ){
        Long reservationId = 1L;//나중에 예약API만들고 추가할 예정
        log.info("예약 상세 조회 요청 - reservationId: {}", reservationId);
        ReservationDetailResponse dummy = ReservationDetailResponse.builder()
                .reservationId(reservationId)
                .placeName("카페 온더문")
                .address("서울시 성동구...")
                .reservedAt(LocalDateTime.of(2025, 6, 1, 14, 0))
                .status("UPCOMING")
                .build();

        return ResponseEntity.ok(dummy);

    }

    @Operation(summary = "예약 취소", description = "사용자가 선택한 예약을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 취소 성공",
                    content = @Content(schema = @Schema(implementation = ReservationCancelResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"code\": 401}"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 예약",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Reservation not found\", \"code\": 404}")))
    })
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationCancelResponse> cancelReservation(
            @PathVariable Long reservationId
    ) {
        log.info("예약 취소 요청 - reservationId: {}", reservationId);

        ReservationCancelResponse response = new ReservationCancelResponse(
                reservationId,
                "예약 취소 완료",
                "canceled"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 작성 가능 예약 목록 조회", description = "리뷰를 작성할 수 있는 예약 목록을 조회합니다.")
    @GetMapping("/reviewable")
    public ResponseEntity<List<ReviewableReservationResponse>> getReviewableReservations(
            //@AuthenticationPrincipal UserPersonalInfo user 유저

    ) {
        Long dummyUserId = 1L;//나중에 인증 붙이면 바꿀 예정
        log.info("리뷰 가능 예약 목록 조회 - userId: {}", dummyUserId);

        // 더미 응답 리스트
        List<ReviewableReservationResponse> dummyList = List.of(
                ReviewableReservationResponse.builder()
                        .reservationId(2L)
                        .placeName("루프탑 바")
                        .reservedAt(LocalDateTime.of(2025, 5, 10, 19, 0))
                        .reviewed(false)
                        .build(),
                ReviewableReservationResponse.builder()
                        .reservationId(3L)
                        .placeName("카페 몽블랑")
                        .reservedAt(LocalDateTime.of(2025, 5, 12, 15, 0))
                        .reviewed(true)// 서비스 구현할때 reviewed == false로 할 예정
                        .build()
        );

        return ResponseEntity.ok(dummyList);
    }






}
