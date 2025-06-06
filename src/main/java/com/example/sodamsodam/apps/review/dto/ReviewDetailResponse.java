package com.example.sodamsodam.apps.review.dto;

import com.example.sodamsodam.apps.review.entity.Review;
import com.example.sodamsodam.apps.review.entity.ReviewImage;
import com.example.sodamsodam.apps.review.entity.ReviewTag;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 상세 응답 DTO")
public class ReviewDetailResponse {

    @Schema(description = "조회할 리뷰의 ID", example = "1")
    private Long reviewId;

    @Schema(description = "작성한 유저 이름",example = "소담이")
    private String userName;

    @Schema(description = "작성한 리뷰 내용",example = "좋아요")
    private String content;

    // 날짜 포맷 지정
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Schema(description = "리뷰 태그 리스트 (최대 3개)", example = "[\"청결\", \"조용함\"]")
    private List<String> tags;

    @Schema(description = "리뷰 이미지 (최대 3장)",example = "[\"https://s3.amazonaws.com/bucket/image1.jpg\"]")
    private List<String> imageUrls;

    public static ReviewDetailResponse fromReview(Review review) {

        List<String> tags = review.getTags().stream()
                .map(ReviewTag::getTag)
                .collect(Collectors.toList());

        List<String> imageUrls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)  // ReviewImage에서 URL만 추출
                .collect(Collectors.toList());

        return new ReviewDetailResponse(
                review.getReviewId(),
                review.getUser().getUserName(),
                review.getContent(),
                review.getCreatedAt(),
                tags,
                imageUrls
        );
    }
}
