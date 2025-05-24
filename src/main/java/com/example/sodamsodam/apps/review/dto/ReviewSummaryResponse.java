package com.example.sodamsodam.apps.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
// 필드가 없는 생성자로 틀만 제공
@NoArgsConstructor
@Schema(description = "리뷰 전체 응답 DTO")
public class ReviewSummaryResponse {

    @Schema(description = "생성된 리뷰의 ID", example = "1")
    private Long reviewId;            // 리뷰 ID

    @Schema(description = "작성한 유저 이름",example = "소담이")
    private String userName;         // 작성 유저명

    @Schema(description = "작성한 리뷰 내용",example = "좋아요")
    private String content;          // 리뷰 내용

    @Schema(description = "리뷰 이미지 (최대 3장)",example = "[\"https://s3.amazonaws.com/bucket/image1.jpg\"]")
    private List<String> imageUrls;  // 최대 3장만 추려서 전달
}
