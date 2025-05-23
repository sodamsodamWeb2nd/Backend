package com.example.sodamsodam.apps.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "리뷰 작성 요청 DTO")
public class ReviewRequest {

    @Schema(description = "리뷰 내용", example = "좋아요")
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @Schema(description = "리뷰 이미지 URL 리스트 (최대 3개)", example = "[\"https://s3.amazonaws.com/bucket/image1.jpg\"]")
    @Size(max = 3, message = "이미지는 최대 3장까지 업로드할 수 있습니다.")
    private List<String> images;

    @Schema(description = "리뷰 태그 리스트 (최대 3개)", example = "[\"청결\", \"조용함\"]")
    @Size(max = 3, message = "태그는 최대 3장까지 업로드할 수 있습니다.")
    private List<String> tags;
}

