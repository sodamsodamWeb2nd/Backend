package com.example.sodamsodam.apps.place.repository;

import com.example.sodamsodam.apps.place.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

    // 카카오 장소 ID로 검색 (중복 저장 방지용)
    Optional<PlaceEntity> findByKakaoPlaceId(String kakaoPlaceId);

    // 장소명으로 검색 (부분 검색 지원)
    List<PlaceEntity> findByPlaceNameContainingIgnoreCase(String placeName);

    // 카테고리 그룹 코드로 검색
    List<PlaceEntity> findByCategoryGroupCode(String categoryGroupCode);

    // 카테고리 그룹명으로 검색
    List<PlaceEntity> findByCategoryGroupName(String categoryGroupName);

    // 활성화된 장소만 조회
    List<PlaceEntity> findByIsActiveTrue();

    // 특정 지역(주소) 내 장소 검색
    List<PlaceEntity> findByAddressNameContainingOrRoadAddressNameContaining(
            String addressName, String roadAddressName);

    // 카테고리별 활성화된 장소 조회
    List<PlaceEntity> findByCategoryGroupCodeAndIsActiveTrue(String categoryGroupCode);

    // 장소명과 카테고리로 복합 검색
    @Query("SELECT p FROM PlaceEntity p WHERE " +
            "(:placeName IS NULL OR p.placeName LIKE %:placeName%) AND " +
            "(:categoryGroupCode IS NULL OR p.categoryGroupCode = :categoryGroupCode) AND " +
            "p.isActive = true")
    List<PlaceEntity> findPlacesByNameAndCategory(
            @Param("placeName") String placeName,
            @Param("categoryGroupCode") String categoryGroupCode);

    // 좌표 기반 근처 장소 검색 (위도, 경도 범위로 검색)
    @Query("SELECT p FROM PlaceEntity p WHERE " +
            "p.latitude BETWEEN :minLat AND :maxLat AND " +
            "p.longitude BETWEEN :minLng AND :maxLng AND " +
            "p.isActive = true")
    List<PlaceEntity> findPlacesInArea(
            @Param("minLat") String minLatitude,
            @Param("maxLat") String maxLatitude,
            @Param("minLng") String minLongitude,
            @Param("maxLng") String maxLongitude);

    // 전화번호로 검색
    Optional<PlaceEntity> findByPhone(String phone);

    // 카테고리별 장소 수 조회
    @Query("SELECT p.categoryGroupName, COUNT(p) FROM PlaceEntity p " +
            "WHERE p.isActive = true " +
            "GROUP BY p.categoryGroupName")
    List<Object[]> countPlacesByCategory();
}