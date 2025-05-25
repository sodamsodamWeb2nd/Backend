package com.example.sodamsodam.apps.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review_tag")
@Getter
@Setter
@NoArgsConstructor
public class ReviewTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_tag_id")
    private Long reviewTagId;

    @Column(nullable = false, length = 100)
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
}
