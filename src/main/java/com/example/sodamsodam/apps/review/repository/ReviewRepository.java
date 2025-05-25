package com.example.sodamsodam.apps.review.repository;


import com.example.sodamsodam.apps.review.entity.Review;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 유저가 작성한 리뷰 목록 조회
    List<Review> findByUser(UserPersonalInfo user);
}
