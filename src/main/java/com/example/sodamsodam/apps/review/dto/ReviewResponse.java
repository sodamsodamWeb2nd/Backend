package com.example.sodamsodam.apps.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "리뷰 작성 응답 DTO")
public class ReviewResponse {

    @Schema(description = "생성된 리뷰의 ID", example = "1")
    private Long reviewId;

    @Schema(description = "리뷰 작성 성공 메시지", example = "리뷰가 성공적으로 등록되었습니다.")
    private String message;
}
