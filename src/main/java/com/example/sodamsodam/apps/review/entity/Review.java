package com.example.sodamsodam.apps.review.entity;

import com.example.sodamsodam.apps.place.entity.PlaceEntity;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    // 하나의 유저는 여러개의 리뷰 가짐
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserPersonalInfo user;

    // 하나의 장소는 여러개의 리뷰를 가짐
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    // 대용량 텍스트
    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    // 생성 시간 자동 설정
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 빌더로 엔티티 생성시 해당 필드는 초기화 방지
    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> images = new ArrayList<>();

    // 빌더로 엔티티 생성시 해당 필드는 초기화 방지
    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewTag> tags = new ArrayList<>();
}
