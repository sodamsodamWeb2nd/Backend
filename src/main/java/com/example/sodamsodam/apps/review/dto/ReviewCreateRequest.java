package com.example.sodamsodam.apps.review.dto;

import com.example.sodamsodam.apps.place.entity.PlaceEntity;
import com.example.sodamsodam.apps.review.entity.Review;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "리뷰 작성 요청 DTO")
public class ReviewCreateRequest {

    @Schema(description = "리뷰 내용", example = "좋아요")
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @Schema(description = "리뷰 태그 리스트 (최대 3개)", example = "[\"청결\", \"조용함\"]")
    @Size(max = 3, message = "태그는 최대 3장까지 업로드할 수 있습니다.")
    private List<String> tags;

    public Review toEntity(UserPersonalInfo user, PlaceEntity place,String content) {
        return Review.builder()
                .user(user)
                .place(place)
                .content(content)
                .build();
    }
}

