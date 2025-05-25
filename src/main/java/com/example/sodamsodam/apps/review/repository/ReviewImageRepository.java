package com.example.sodamsodam.apps.review.repository;

import com.example.sodamsodam.apps.review.entity.Review;
import com.example.sodamsodam.apps.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    // 특정 리뷰에 연결된 이미지들
    List<ReviewImage> findByReview(Review review);
}
