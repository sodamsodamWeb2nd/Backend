package com.example.sodamsodam.apps.place.dto;

import com.example.sodamsodam.apps.place.entity.PlaceEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// 장소 검색 요청 DTO
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장소 검색 요청 DTO")
class PlaceSearchRequest {

    @Schema(description = "검색 키워드", example = "카카오프렌즈")
    private String query;

    @Schema(description = "카테고리 그룹 코드", example = "FD6")
    private String categoryGroupCode;

    @Schema(description = "중심 좌표 X (경도)", example = "127.027621")
    private String x;

    @Schema(description = "중심 좌표 Y (위도)", example = "37.498095")
    private String y;

    @Schema(description = "검색 반경(미터)", example = "20000")
    private Integer radius;

    @Schema(description = "페이지 번호", example = "1")
    private Integer page = 1;

    @Schema(description = "한 페이지에 보여질 문서의 개수", example = "15")
    private Integer size = 15;

    @Schema(description = "결과 정렬 순서", example = "distance")
    private String sort = "distance"; // distance(거리순) 또는 accuracy(정확도순)
}

// 장소 저장 요청 DTO
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장소 저장 요청 DTO")
class PlaceSaveRequest {

    @Schema(description = "카카오 장소 ID", required = true)
    private String kakaoPlaceId;

    @Schema(description = "장소명", required = true)
    private String placeName;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "카테고리 그룹 코드")
    private String categoryGroupCode;

    @Schema(description = "카테고리 그룹명")
    private String categoryGroupName;

    @Schema(description = "전화번호")
    private String phone;

    @Schema(description = "지번 주소")
    private String addressName;

    @Schema(description = "도로명 주소")
    private String roadAddressName;

    @Schema(description = "경도")
    private String longitude;

    @Schema(description = "위도")
    private String latitude;

    @Schema(description = "카카오맵 URL")
    private String placeUrl;

    @Schema(description = "장소 설명")
    private String description;
}

// 장소 응답 DTO
@Getter @Setter
@NoArgsConstructor
@Schema(description = "장소 정보 응답 DTO")
class PlaceResponse {

    @Schema(description = "장소 ID")
    private Long id;

    @Schema(description = "카카오 장소 ID")
    private String kakaoPlaceId;

    @Schema(description = "장소명")
    private String placeName;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "카테고리 그룹 코드")
    private String categoryGroupCode;

    @Schema(description = "카테고리 그룹명")
    private String categoryGroupName;

    @Schema(description = "전화번호")
    private String phone;

    @Schema(description = "지번 주소")
    private String addressName;

    @Schema(description = "도로명 주소")
    private String roadAddressName;

    @Schema(description = "경도")
    private String longitude;

    @Schema(description = "위도")
    private String latitude;

    @Schema(description = "카카오맵 URL")
    private String placeUrl;

    @Schema(description = "장소 설명")
    private String description;

    @Schema(description = "활성화 여부")
    private Boolean isActive;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환 생성자
    public PlaceResponse(PlaceEntity place) {
        this.id = place.getId();
        this.kakaoPlaceId = place.getKakaoPlaceId();
        this.placeName = place.getPlaceName();
        this.categoryName = place.getCategoryName();
        this.categoryGroupCode = place.getCategoryGroupCode();
        this.categoryGroupName = place.getCategoryGroupName();
        this.phone = place.getPhone();
        this.addressName = place.getAddressName();
        this.roadAddressName = place.getRoadAddressName();
        this.longitude = place.getLongitude();
        this.latitude = place.getLatitude();
        this.placeUrl = place.getPlaceUrl();
        this.description = place.getDescription();
        this.isActive = place.getIsActive();
        this.createdAt = place.getCreatedAt();
        this.updatedAt = place.getUpdatedAt();
    }
}

// 장소 검색 결과 응답 DTO
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장소 검색 결과 응답 DTO")
class PlaceSearchResponse {

    @Schema(description = "검색된 장소 목록")
    private List<PlaceResponse> places;

    @Schema(description = "총 검색 결과 수")
    private Integer totalCount;

    @Schema(description = "현재 페이지")
    private Integer currentPage;

    @Schema(description = "총 페이지 수")
    private Integer totalPages;

    @Schema(description = "마지막 페이지 여부")
    private Boolean isEnd;
}