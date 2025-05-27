package com.example.sodamsodam.apps.place.controller;

import com.example.sodamsodam.apps.place.entity.PlaceEntity;
import com.example.sodamsodam.apps.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@Tag(name = "Place", description = "장소 검색 및 관리 API")
public class PlaceController {

    private final PlaceService placeService;

    /**
     * 카카오맵 API로 장소 검색
     * 예시: GET /api/places/search?query=스타벅스&x=127.027621&y=37.498095&radius=1000
     */
    @Operation(summary = "장소 검색",
            description = "카카오맵 API를 통해 장소를 검색합니다. 키워드만 입력하거나 좌표와 반경을 함께 설정할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "카카오맵 API 호출 실패")
    })
    @GetMapping("/search")
    public ResponseEntity<List<PlaceEntity>> searchPlaces(
            @Parameter(description = "검색 키워드", example = "스타벅스", required = true)
            @RequestParam String query,

            @Parameter(description = "중심 좌표 X (경도)", example = "127.027621")
            @RequestParam(required = false) String x,

            @Parameter(description = "중심 좌표 Y (위도)", example = "37.498095")
            @RequestParam(required = false) String y,

            @Parameter(description = "검색 반경(미터)", example = "1000")
            @RequestParam(required = false) Integer radius,

            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(required = false, defaultValue = "1") Integer page,

            @Parameter(description = "페이지당 결과 수", example = "15")
            @RequestParam(required = false, defaultValue = "15") Integer size
    ) {
        log.info("장소 검색 요청 - 키워드: {}", query);

        List<PlaceEntity> places = placeService.searchPlaces(query, x, y, radius, page, size);
        return ResponseEntity.ok(places);
    }

    /**
     * 검색한 장소를 데이터베이스에 저장
     * 예시: POST /api/places
     */
    @Operation(summary = "장소 저장",
            description = "검색된 장소를 데이터베이스에 저장합니다. 이미 존재하는 장소면 기존 데이터를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<PlaceEntity> savePlace(@RequestBody PlaceEntity place) {
        log.info("장소 저장 요청 - 이름: {}", place.getPlaceName());

        PlaceEntity savedPlace = placeService.savePlace(place);
        return ResponseEntity.ok(savedPlace);
    }

    /**
     * 저장된 모든 장소 목록 조회
     * 예시: GET /api/places
     */
    @Operation(summary = "저장된 장소 목록 조회",
            description = "데이터베이스에 저장된 모든 활성화된 장소를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PlaceEntity>> getAllPlaces() {
        log.info("저장된 장소 목록 조회 요청");

        List<PlaceEntity> places = placeService.getAllPlaces();
        return ResponseEntity.ok(places);
    }

    /**
     * 특정 장소 상세 정보 조회
     * 예시: GET /api/places/1
     */
    @Operation(summary = "장소 상세 조회",
            description = "특정 ID의 장소 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "장소를 찾을 수 없음")
    })
    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceEntity> getPlace(
            @Parameter(description = "조회할 장소 ID", example = "1")
            @PathVariable Long placeId
    ) {
        log.info("장소 상세 조회 요청 - ID: {}", placeId);

        PlaceEntity place = placeService.getPlaceById(placeId);
        return ResponseEntity.ok(place);
    }

    /**
     * 카테고리별 장소 검색 (간편 검색)
     * 예시: GET /api/places/category/FD6?x=127.027621&y=37.498095
     */
    @Operation(summary = "카테고리별 장소 검색",
            description = "특정 카테고리의 장소를 검색합니다. FD6(음식점), CE7(카페), CT1(문화시설) 등")
    @GetMapping("/category/{categoryCode}")
    public ResponseEntity<List<PlaceEntity>> searchByCategory(
            @Parameter(description = "카테고리 코드", example = "FD6")
            @PathVariable String categoryCode,

            @Parameter(description = "중심 좌표 X (경도)", example = "127.027621", required = true)
            @RequestParam String x,

            @Parameter(description = "중심 좌표 Y (위도)", example = "37.498095", required = true)
            @RequestParam String y,

            @Parameter(description = "검색 반경(미터)", example = "1000")
            @RequestParam(required = false, defaultValue = "1000") Integer radius
    ) {
        log.info("카테고리별 검색 요청 - 카테고리: {}, 좌표: ({}, {})", categoryCode, x, y);

        // 카테고리 코드를 키워드로 사용해서 검색
        String query = getCategoryQuery(categoryCode);
        List<PlaceEntity> places = placeService.searchPlaces(query, x, y, radius, 1, 15);

        return ResponseEntity.ok(places);
    }

    /**
     * 카테고리 코드를 검색 키워드로 변환하는 헬퍼 메서드
     */
    private String getCategoryQuery(String categoryCode) {
        switch (categoryCode.toUpperCase()) {
            case "FD6": return "음식점";
            case "CE7": return "카페";
            case "CT1": return "문화시설";
            case "AT4": return "관광명소";
            case "CS2": return "편의점";
            case "MT1": return "대형마트";
            case "SW8": return "지하철역";
            case "PK6": return "주차장";
            case "OL7": return "주유소";
            case "HP8": return "병원";
            case "PM9": return "약국";
            default: return categoryCode;
        }
    }
}