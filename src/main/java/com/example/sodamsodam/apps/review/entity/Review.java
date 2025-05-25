package com.example.sodamsodam.apps.review.entity;

import com.example.sodamsodam.apps.place.entity.PlaceEntity;
import com.example.sodamsodam.apps.user.entity.UserPersonalInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
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

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewTag> tags = new ArrayList<>();
}
