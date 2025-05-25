package com.example.sodamsodam.apps.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

// 카카오맵 API 전체 응답
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPlaceSearchResponse {

    private Meta meta;
    private List<Document> documents;

    // 메타 정보
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        @JsonProperty("total_count")
        private Integer totalCount;      // 검색어에 검색된 문서 수

        @JsonProperty("pageable_count")
        private Integer pageableCount;   // total_count 중 노출 가능 문서 수 (최대 45)

        @JsonProperty("is_end")
        private Boolean isEnd;           // 현재 페이지가 마지막 페이지인지 여부

        @JsonProperty("same_name")
        private SameName sameName;       // 질의어의 지역 및 키워드 분석 정보
    }

    // 동일한 이름의 지역 정보
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SameName {
        private List<String> region;          // 질의어에서 인식된 지역의 리스트
        private String keyword;               // 질의어에서 지역 정보를 제외한 키워드
        @JsonProperty("selected_region")
        private String selectedRegion;        // 인식된 지역 리스트 중, 현재 검색에 사용된 지역 정보
    }

    // 장소 문서 정보
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private String id;                    // 장소 ID

        @JsonProperty("place_name")
        private String placeName;             // 장소명, 업체명

        @JsonProperty("category_name")
        private String categoryName;          // 카테고리 이름

        @JsonProperty("category_group_code")
        private String categoryGroupCode;     // 중요 카테고리만 그룹핑한 카테고리 그룹 코드

        @JsonProperty("category_group_name")
        private String categoryGroupName;     // 중요 카테고리만 그룹핑한 카테고리 그룹명

        private String phone;                 // 전화번호

        @JsonProperty("address_name")
        private String addressName;           // 전체 지번 주소

        @JsonProperty("road_address_name")
        private String roadAddressName;       // 전체 도로명 주소

        private String x;                     // X 좌표값, 경도(longitude)
        private String y;                     // Y 좌표값, 위도(latitude)

        @JsonProperty("place_url")
        private String placeUrl;              // 장소 상세페이지 URL

        private String distance;              // 중심좌표까지의 거리 (단위 meter)
    }
}