package com.example.sodamsodam.apps.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 개수 응답 DTO")
public class ReviewCountResponse {

    @Schema(description = "리뷰 개수를 조회한 장소 ID", example = "1")
    Long placeId;

    @Schema(description = "작성된 리뷰의 개수", example = "1")
    Long reviewCount;
}
