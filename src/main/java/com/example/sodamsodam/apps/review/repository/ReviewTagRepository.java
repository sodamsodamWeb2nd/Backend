package com.example.sodamsodam.apps.review.repository;

import com.example.sodamsodam.apps.review.entity.Review;
import com.example.sodamsodam.apps.review.entity.ReviewTag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {

    // 특정 리뷰에 연결된 태그들
    List<ReviewTag> findByReview(Review review);
}
