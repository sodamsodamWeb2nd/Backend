package com.example.sodamsodam.apps.place.service;

import com.example.sodamsodam.apps.place.dto.KakaoPlaceSearchResponse;
import com.example.sodamsodam.apps.place.entity.PlaceEntity;
import com.example.sodamsodam.apps.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final WebClient webClient = WebClient.builder().build();

    // application.properties에서 카카오 REST API 키를 가져옴
    @Value("${kakao.client.id}")
    private String kakaoApiKey;

    /**
     * 특정 ID로 장소 조회
     * @param placeId 조회할 장소 ID
     * @return 장소 정보
     */
    public PlaceEntity getPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("장소를 찾을 수 없습니다 - ID: " + placeId));
    }

    /**
     * 카카오맵 API로 장소를 검색하는 메서드
     * @param query 검색할 키워드 (예: "스타벅스", "카페")
     * @param x 중심 좌표 경도 (선택사항)
     * @param y 중심 좌표 위도 (선택사항)
     * @param radius 검색 반경(미터, 선택사항)
     * @param page 페이지 번호 (기본값: 1)
     * @param size 한 페이지당 결과 수 (기본값: 15, 최대 15)
     * @return 검색된 장소 목록
     */
    public List<PlaceEntity> searchPlaces(String query, String x, String y,
                                          Integer radius, Integer page, Integer size) {

        // 기본값 설정
        if (page == null) page = 1;
        if (size == null) size = 15;

        log.info("카카오맵 장소 검색 - 키워드: {}, 좌표: ({}, {}), 반경: {}m", query, x, y, radius);

        try {
            // 카카오맵 API 호출
            String url = "https://dapi.kakao.com/v2/local/search/keyword.json";

            // WebClient로 API 호출 준비
            WebClient.RequestHeadersSpec<?> requestSpec = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.scheme("https")
                                .host("dapi.kakao.com")
                                .path("/v2/local/search/keyword.json")
                                .queryParam("query", query);
//                                .queryParam("page", page.toString())
//                                .queryParam("size", size.toString());

                        // 선택적 파라미터들 추가
                        if (x != null && y != null) {
                            uriBuilder.queryParam("x", x).queryParam("y", y);
                        }
                        if (radius != null) {
                            uriBuilder.queryParam("radius", radius);
                        }

                        return uriBuilder.build();
                    })
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            // API 호출 및 응답 받기
            KakaoPlaceSearchResponse response = requestSpec
                    .retrieve()
                    .bodyToMono(KakaoPlaceSearchResponse.class)
                    .block(); // 비동기를 동기로 변환

            if (response == null || response.getDocuments() == null) {
                log.warn("카카오맵 API 응답이 비어있습니다.");
                return new ArrayList<>();
            }

            // 카카오 API 응답을 우리 엔티티로 변환
            List<PlaceEntity> places = new ArrayList<>();
            for (KakaoPlaceSearchResponse.Document doc : response.getDocuments()) {
                PlaceEntity place = convertToEntity(doc);
                places.add(place);
            }

            log.info("검색 완료 - {}개 장소 발견", places.size());
            return places;

        } catch (WebClientResponseException e) {
            log.error("카카오맵 API 호출 실패 - 상태코드: {}, 응답: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("장소 검색에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("장소 검색 중 오류 발생", e);
            throw new RuntimeException("장소 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 검색된 장소를 데이터베이스에 저장
     * @param place 저장할 장소 정보
     * @return 저장된 장소 엔티티
     */
    public PlaceEntity savePlace(PlaceEntity place) {
        // 이미 같은 카카오 장소 ID가 있는지 확인
        Optional<PlaceEntity> existing = placeRepository.findByKakaoPlaceId(place.getKakaoPlaceId());

        if (existing.isPresent()) {
            log.info("이미 존재하는 장소입니다 - 카카오ID: {}", place.getKakaoPlaceId());
            return existing.get();
        }

        // 새로운 장소 저장
        PlaceEntity saved = placeRepository.save(place);
        log.info("새 장소 저장 완료 - ID: {}, 이름: {}", saved.getId(), saved.getPlaceName());
        return saved;
    }

    /**
     * 저장된 장소 목록 조회
     * @return 활성화된 모든 장소 목록
     */
    public List<PlaceEntity> getAllPlaces() {
        return placeRepository.findByIsActiveTrue();
    }

    /**
     * 카카오 API 응답을 우리 엔티티로 변환하는 private 메서드
     */
    private PlaceEntity convertToEntity(KakaoPlaceSearchResponse.Document doc) {
        return PlaceEntity.builder()
                .kakaoPlaceId(doc.getId())
                .placeName(doc.getPlaceName())
                .categoryName(doc.getCategoryName())
                .categoryGroupCode(doc.getCategoryGroupCode())
                .categoryGroupName(doc.getCategoryGroupName())
                .phone(doc.getPhone())
                .addressName(doc.getAddressName())
                .roadAddressName(doc.getRoadAddressName())
                .longitude(doc.getX()) // 카카오에서는 X가 경도
                .latitude(doc.getY())  // 카카오에서는 Y가 위도
                .placeUrl(doc.getPlaceUrl())
                .distance(doc.getDistance())
                .isActive(true) // 기본적으로 활성화
                .build();
    }

    /**
     * 장소를 검색하고 즉시 데이터베이스에 저장하는 메서드
     * @param query 검색할 키워드 (예: "스타벅스", "카페")
     * @param x 중심 좌표 경도 (선택사항)
     * @param y 중심 좌표 위도 (선택사항)
     * @param radius 검색 반경(미터, 선택사항)
     * @param page 페이지 번호 (기본값: 1)
     * @param size 한 페이지당 결과 수 (기본값: 15, 최대 15)
     * @return 검색 후 저장된 장소 목록
     */
    public List<PlaceEntity> searchAndSavePlaces(String query, String x, String y,
                                                 Integer radius, Integer page, Integer size) {
        // 장소 검색
        List<PlaceEntity> searchedPlaces = searchPlaces(query, x, y, radius, page, size);
        List<PlaceEntity> savedPlaces = new ArrayList<>();

        // 검색된 각 장소를 저장
        for (PlaceEntity place : searchedPlaces) {
            PlaceEntity savedPlace = savePlace(place);
            savedPlaces.add(savedPlace);
        }

        log.info("검색 및 저장 완료 - {}개 장소 저장됨", savedPlaces.size());
        return savedPlaces;
    }
}