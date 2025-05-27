package com.example.sodamsodam.apps.reservation.repository;

import com.example.sodamsodam.apps.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    //1. 유저 전체 예약 조회(빠른 날짜부터)
    List<Reservation> findByUser_UserIdOrderByReservedAtAsc(Long userId);

    // 2. 유저의 상태별 예약 조회 (빠른 날짜부터)
    List<Reservation> findByUser_UserIdAndStatusOrderByReservedAtAsc(Long userId, Reservation.ReservationStatus status);

    // 3. 유저의 리뷰 안한 예약만 조회 (리뷰 작성 가능 목록용)
    List<Reservation> findByUser_UserIdAndReviewedFalse(Long userId);
}
