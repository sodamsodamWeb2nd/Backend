package com.example.sodamsodam.apps.reservation.controller;

import com.example.sodamsodam.apps.reservation.dto.*;
import com.example.sodamsodam.apps.reservation.entity.Reservation;
//import com.example.sodamsodam.apps.reservation.repository.ReservationRepository;
import com.example.sodamsodam.apps.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "예약 관련 API")
public class ReservationController {

    private final ReservationService reservationService;


    @Operation(summary = "예약 등록", description = "사용자가 새로운 예약을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"message\": \"예약 완료\", \"reservationId\": 42}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid data\", \"code\": 400}")))
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(@RequestBody ReservationCreateRequest request) {
        Long reservationId = reservationService.createReservation(request);
        return ResponseEntity.ok(Map.of(
                    "message", "예약 완료",
                "reservationId", reservationId));
    }



    @Operation(summary = "유저 ID 기반 예약 목록 조회 (상태 필터링 가능)", description = "특정 유저의 예약 목록을 상태에 따라 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReservationListResponse.class))),
            @ApiResponse(responseCode = "404", description = "유저 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"User not found\", \"code\": 404}")))
    })
    @GetMapping("/user/{userId}/filter")
    public ResponseEntity<List<ReservationListResponse>> getUserReservationsWithStatus(
            @PathVariable Long userId,
            @RequestParam(required = false) Reservation.ReservationStatus status
    ) {
        List<Reservation> reservations = (status == null)
                ? reservationService.getReservations(userId)
                : reservationService.getReservationsByStatus(userId, status);

        List<ReservationListResponse> result = reservations.stream()
                .map(ReservationListResponse::from)
                .toList();

        return ResponseEntity.ok(result);
    }





    @Operation(summary = "예약 목록 상세 조회", description = "로그인한 사용자의 예약 상세 정보 조회 API입니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReservationDetailResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"code\": 401}"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 예약",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Reservation not found\", \"code\": 404}")))
    })
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponse> getReservationDetail(
            @PathVariable Long reservationId
    ) {
        log.info("예약 상세 조회 요청 - reservationId: {}", reservationId);

        Reservation reservation = reservationService.getReservationDetail(reservationId);
        ReservationDetailResponse response = ReservationDetailResponse.from(reservation);

        return ResponseEntity.ok(response);
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

        Reservation cancelled = reservationService.cancelReservation(reservationId);
        ReservationCancelResponse response = new ReservationCancelResponse(
                reservationId,
                "예약 취소 완료",
                cancelled.getStatus().name().toLowerCase()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 작성 가능 예약 목록 조회", description = "리뷰를 작성할 수 있는 예약 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 작성 가능 예약 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReviewableReservationResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"code\": 401}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Internal Server Error\", \"code\": 500}")))
    })
    @GetMapping("/writeable")
    public ResponseEntity<List<ReviewableReservationResponse>> getReviewableReservations(
            @Parameter(description = "유저 ID", example = "7", required = true)
            @RequestParam Long userId  // 추후 인증 붙이면 @AuthenticationPrincipal로 교체
    ) {
        log.info("리뷰작성 가능 예약 목록 조회 - userId: {}", userId);

        List<Reservation> reservations = reservationService.getNotReviewedReservations(userId);

        List<ReviewableReservationResponse> result = reservations.stream()
                .map(ReviewableReservationResponse::from)// 만약 리뷰 작성 가능한 예약이 없다면 빈배열로 뜹니다.
                .toList();

        return ResponseEntity.ok(result);
    }





}
