package com.example.sodamsodam.apps.review.controller;

import com.example.sodamsodam.apps.review.dto.ReviewCreateRequest;
import com.example.sodamsodam.apps.review.dto.ReviewCreateResponse;
import com.example.sodamsodam.apps.review.dto.ReviewSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {

    @Operation(summary = "리뷰 작성", description = "해당 장소에 리뷰를 작성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰작성 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 장소"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{place_id}/reviews")
    public ResponseEntity<ReviewCreateResponse> createReview(@PathVariable Long place_id, @RequestBody @Valid ReviewCreateRequest request) {
        // 리뷰 ID 값은 나중에 기능 구현후 생성된 리뷰 ID 값으로 대체 예정
        ReviewCreateResponse response = new ReviewCreateResponse(1L,"리뷰가 성공적으로 등록되었습니다");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "리뷰 전체 조회", description = "해당 장소에 리뷰를 전체 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 장소"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{place_id}/reviews")
    public ResponseEntity<List<ReviewSummaryResponse>> getReviews(@PathVariable Long place_id) {
        // 현재는 아무런 값이 없는 응답 DTO만 반환 -> 추후 로직 구현 단계에서 추가 예정
        ReviewSummaryResponse response = new ReviewSummaryResponse();
        return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonList(response));
    }
}
