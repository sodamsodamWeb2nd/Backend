package com.example.sodamsodam.apps.review.dto;

import com.example.sodamsodam.apps.review.entity.Review;
import com.example.sodamsodam.apps.review.entity.ReviewImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 전체 응답 DTO")
public class ReviewSummaryResponse {

    @Schema(description = "조회할 리뷰의 ID", example = "1")
    private Long reviewId;            // 리뷰 ID

    @Schema(description = "작성한 유저 이름",example = "소담이")
    private String userName;         // 작성 유저명

    @Schema(description = "작성한 리뷰 내용",example = "좋아요")
    private String content;          // 리뷰 내용

    @Schema(description = "리뷰 이미지 (최대 3장)",example = "[\"https://s3.amazonaws.com/bucket/image1.jpg\"]")
    private List<String> imageUrls;  // 최대 3장만 추려서 전달

    public static ReviewSummaryResponse fromReview(Review review) {

        List<String> imageUrls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)  // ReviewImage에서 URL만 추출
                .collect(Collectors.toList());

        return new ReviewSummaryResponse(
                review.getReviewId(),
                review.getUser().getUserName(),
                review.getContent(),
                imageUrls
        );
    }
}
