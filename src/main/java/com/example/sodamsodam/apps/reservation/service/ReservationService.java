package com.example.sodamsodam.apps.reservation.service;


import com.example.sodamsodam.apps.reservation.dto.ReservationCreateRequest;
import com.example.sodamsodam.apps.reservation.entity.Reservation;
import com.example.sodamsodam.apps.reservation.repository.ReservationRepository;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import com.example.sodamsodam.apps.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository; //DB접근
    private final UserRepository userRepository;

    public Long createReservation(ReservationCreateRequest request) {
        UserPersonalInfo user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        Reservation reservation = Reservation.builder()
                .user(user)
                .placeName(request.getPlaceName())
                .kakaoPlaceId(request.getKakaoPlaceId())
                .address(request.getAddress())
                .reservedAt(request.getReservedAt())
                .status(Reservation.ReservationStatus.UPCOMING)
                .reviewed(false)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        return saved.getReservationId();
    }



    public List<Reservation> getReservations(Long userId) {
        return reservationRepository.findByUser_UserIdOrderByReservedAtAsc(userId);
    }// 예약 일자 오름차순(빠른 날자부터)가져옴

    public List<Reservation> getReservationsByStatus(Long userId, Reservation.ReservationStatus status) {
        return reservationRepository.findByUser_UserIdAndStatusOrderByReservedAtAsc(userId, status);
    }//상태에 따라 분류 가능

    public Reservation getReservationDetail(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));
    }

    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        reservation.cancel(); // 상태 변경
        return reservationRepository.save(reservation); // 변경사항 저장
    }


    public List<Reservation> getNotReviewedReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUser_UserIdAndReviewedFalse(userId);

        // 리뷰 가능 조건 추가
        return reservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.COMPLETED)
                .filter(r -> r.getReservedAt().toLocalDate().plusDays(3).isAfter(LocalDate.now()))
                .toList();
    }

}
