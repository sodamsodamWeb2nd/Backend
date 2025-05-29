package com.example.sodamsodam.apps.review.service;

import com.example.sodamsodam.apps.common.exception.PlaceNotFoundException;
import com.example.sodamsodam.apps.common.exception.ReviewNotFoundException;
import com.example.sodamsodam.apps.common.exception.UserNotAuthenticatedException;
import com.example.sodamsodam.apps.place.entity.PlaceEntity;
import com.example.sodamsodam.apps.place.repository.PlaceRepository;
import com.example.sodamsodam.apps.review.dto.*;
import com.example.sodamsodam.apps.review.entity.Review;
import com.example.sodamsodam.apps.review.entity.ReviewImage;
import com.example.sodamsodam.apps.review.entity.ReviewTag;
import com.example.sodamsodam.apps.review.repository.ReviewRepository;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public ReviewCreateResponse createReview(Long placeId, ReviewCreateRequest request, List<MultipartFile> images) {
        PlaceEntity place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException("해당 ID의 장소를 찾을 수 없습니다."));

        // 로그인한 유저를 가져옴
        UserPersonalInfo user = getCurrentUser();

        Review review = request.toEntity(user,place);

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

    // 리뷰 전체 조회
    public List<ReviewSummaryResponse> getAllReviews(Long place_id) {

        PlaceEntity place = placeRepository.findById(place_id)
                .orElseThrow(() -> new PlaceNotFoundException("해당 ID의 장소를 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findAllByPlace_Id(place.getId());

        return reviews.stream() // 리뷰 엔티티 리스트를 스트림 형태로 변환
                .map(ReviewSummaryResponse::fromReview) // 각 Review 객체를 ReviewSummaryResponse DTO로 매핑 (정적 메서드 사용)
                .collect(Collectors.toList());          // 스트림을 List<ReviewSummaryResponse> 형태로 수집 및 반환
    }

    // 유저 리뷰 전체 조회
    public List<ReviewSummaryResponse> getMyAllReviews(Long place_id) {

        PlaceEntity place = placeRepository.findById(place_id)
                .orElseThrow(() -> new PlaceNotFoundException("해당 ID의 장소를 찾을 수 없습니다."));

        UserPersonalInfo user = getCurrentUser();

        List<Review> review = reviewRepository.findAllByPlace_IdAndUser_UserId(place.getId(),user.getUserId());

        return review.stream()
                .map(ReviewSummaryResponse::fromReview)
                .collect(Collectors.toList());
    }

    // 리뷰 단건 조회
    public ReviewDetailResponse getReviewDetail(Long place_id,Long review_id) {

        PlaceEntity place = placeRepository.findById(place_id)
                .orElseThrow(() -> new PlaceNotFoundException("해당 ID의 장소를 찾을 수 없습니다."));

        Review review = reviewRepository.findByReviewIdAndPlace_Id(review_id,place.getId())
                .orElseThrow(() -> new ReviewNotFoundException("해당 ID의 리뷰를 찾을 수 없습니다"));

        return ReviewDetailResponse.fromReview(review);
    }

    // 리뷰 개수 조회
    public ReviewCountResponse getReviewCount(Long place_id) {

        PlaceEntity place = placeRepository.findById(place_id)
                .orElseThrow(() -> new PlaceNotFoundException("해당 ID의 장소를 찾을 수 없습니다."));

        Long reviewCount = reviewRepository.countByPlace_Id(place.getId());

        return ReviewCountResponse.builder()
                        .placeId(place_id)
                        .reviewCount(reviewCount).build();
    }

    // 로그인 유저 반환 함수
    private UserPersonalInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPersonalInfo)) {
            throw new UserNotAuthenticatedException("로그인이 필요합니다");
        }
        return (UserPersonalInfo) authentication.getPrincipal();
    }
}
