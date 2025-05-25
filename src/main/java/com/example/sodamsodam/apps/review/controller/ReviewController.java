package com.example.sodamsodam.apps.review.controller;

import com.example.sodamsodam.apps.review.dto.*;
import com.example.sodamsodam.apps.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "해당 장소에 리뷰를 작성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰작성 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 장소"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping(value = "/{place_id}/reviews",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewCreateResponse> createReview(@PathVariable Long place_id,
                                                             @ModelAttribute ("request") @Valid ReviewCreateRequest request,
                                                             @RequestParam(value = "images",required = false) List<MultipartFile> images) { // 이미지 파일 업로드 방식 S3로 변경
        if (images == null || images.size() > 3) {
            throw new IllegalArgumentException("이미지는 최대 3장가지 업로드할 수 있습니다");
        }
        ReviewCreateResponse response = reviewService.createReview(place_id, request, images);
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

    @Operation(summary = "로그인 유저의 리뷰 전체 조회", description = "로그인한 유저가 작성한 리뷰를 전체 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 장소"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{place_id}/reviews/me")
    public ResponseEntity<List<ReviewSummaryResponse>> getMyReviews(@PathVariable Long place_id) {
        // 현재는 아무런 값이 없는 응답 DTO만 반환 -> 추후 로직 구현 단계에서 추가 예정
        ReviewSummaryResponse response = new ReviewSummaryResponse();
        return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonList(response));
    }

    @Operation(summary = "리뷰 단건 조회", description = "특정 리뷰를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 장소/존재하지 않는 리뷰"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{place_id}/reviews/{review_id}")
    public ResponseEntity<ReviewDetailResponse> getReview(@PathVariable Long place_id,@PathVariable Long review_id) {
        // 현재는 아무런 값이 없는 응답 DTO만 반환 -> 추후 로직 구현 단계에서 추가 예정
        ReviewDetailResponse response = new ReviewDetailResponse();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "리뷰 개수 조회", description = "특정 장소에 작성된 리뷰의 개수를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 개수 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 장소"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{place_id}/reviews/count")
    public ResponseEntity<ReviewCountResponse> getReviewCount(@PathVariable Long place_id) {
        // 현재는 아무런 값이 없는 응답 DTO만 반환 -> 추후 로직 구현 단계에서 추가 예정
        ReviewCountResponse response = new ReviewCountResponse();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
