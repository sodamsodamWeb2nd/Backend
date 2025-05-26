package com.example.sodamsodam.apps.review.repository;


import com.example.sodamsodam.apps.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 유저가 작성한 리뷰 목록 조회
    List<Review> findAllByPlace_IdAndUser_UserId(Long place_id,Long user_id);

    // 해당 장소에 작성된 리뷰 전체 조회
    List<Review> findAllByPlace_Id(Long place_id);

    // 해당 장소에 작성된 리뷰 단건 조회
    Optional<Review> findByReviewIdAndPlace_Id(Long reviewId, Long placeId);

    // 해당 장소에 작성된 리뷰 개수
    Long countByPlace_Id(Long place_id);

}
