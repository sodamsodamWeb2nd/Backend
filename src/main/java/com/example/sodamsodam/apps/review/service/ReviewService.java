package com.example.sodamsodam.apps.review.service;

import com.example.sodamsodam.apps.place.entity.PlaceEntity;
import com.example.sodamsodam.apps.place.repository.PlaceRepository;
import com.example.sodamsodam.apps.review.dto.ReviewCreateRequest;
import com.example.sodamsodam.apps.review.dto.ReviewCreateResponse;
import com.example.sodamsodam.apps.review.entity.Review;
import com.example.sodamsodam.apps.review.entity.ReviewImage;
import com.example.sodamsodam.apps.review.entity.ReviewTag;
import com.example.sodamsodam.apps.review.repository.ReviewRepository;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final S3Uploader s3Uploader;

    public ReviewCreateResponse createReview(Long placeId, ReviewCreateRequest request, List<MultipartFile> images) {
        PlaceEntity place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        // 로그인한 유저를 가져옴
        UserPersonalInfo user = getCurrentUser();

        Review review = request.toEntity(user,place,request.getContent());

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String url = s3Uploader.upload(image,"reviews");
                ReviewImage reviewImage = ReviewImage.builder()
                        .review(review)
                        .imageUrl(url)
                        .build();
                review.getImages().add(reviewImage);
            }
        }

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            for(String tag : request.getTags()) {
                ReviewTag reviewTag = ReviewTag.builder()
                        .review(review)
                        .tag(tag)
                        .build();
                review.getTags().add(reviewTag);
            }
        }

        log.info("요청 수신: user={}, placeId={}, tags={}, images={}", user.getEmail(), placeId, request.getTags(), images.size());

        reviewRepository.save(review);
        return new ReviewCreateResponse(review.getReviewId(), "리뷰가 성공적으로 등록되었습니다");
    }

    // 로그인 유저 반환 함수
    private UserPersonalInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPersonalInfo)) {
            throw new IllegalStateException("인증된 사용자를 찾을 수 없습니다");
        }
        return (UserPersonalInfo) authentication.getPrincipal();
    }
}
