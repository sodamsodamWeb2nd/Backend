package com.example.sodamsodam.apps.place.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "places")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 카카오맵 API에서 제공하는 장소 고유 ID
    @Column(name = "kakao_place_id", unique = true)
    private String kakaoPlaceId;

    // 장소명 (필수)
    @Column(name = "place_name", nullable = false, length = 500)
    private String placeName;

    // 카테고리명 (예: 음식점 > 한식 > 삼겹살)
    @Column(name = "category_name", length = 500)
    private String categoryName;

    // 카테고리 그룹 코드 (예: FD6, CE7 등)
    @Column(name = "category_group_code", length = 10)
    private String categoryGroupCode;

    // 카테고리 그룹명 (예: 음식점, 카페 등)
    @Column(name = "category_group_name", length = 100)
    private String categoryGroupName;

    // 전화번호
    @Column(name = "phone", length = 20)
    private String phone;

    // 지번 주소
    @Column(name = "address_name", length = 500)
    private String addressName;

    // 도로명 주소
    @Column(name = "road_address_name", length = 500)
    private String roadAddressName;

    // 경도 (longitude)
    @Column(name = "longitude")
    private String longitude;

    // 위도 (latitude)
    @Column(name = "latitude")
    private String latitude;

    // 카카오맵 상세페이지 URL
    @Column(name = "place_url", length = 1000)
    private String placeUrl;

    // 중심좌표까지의 거리 (단위: meter) - 검색 시에만 사용
    @Column(name = "distance")
    private String distance;

    // 장소 이미지 URL
    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    // 추가 정보들 (우리 서비스에서 관리)
    @Column(name = "description", length = 1000)
    private String description; // 장소 설명

    @Column(name = "is_active")
    private Boolean isActive = true; // 활성화 여부

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 생성자에서 시간 자동 설정
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}