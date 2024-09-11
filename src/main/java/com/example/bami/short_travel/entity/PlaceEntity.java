package com.example.bami.short_travel.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String roadAddress;
    private String lotnoAddress;
    private float latitude;
    private float longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    private RecommendationEntity recommendation;

    public PlaceEntity(String name, String roadAddress, String lotnoAddress, float latitude, float longitude) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.lotnoAddress = lotnoAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected PlaceEntity() {}

    public void setRecommendation(RecommendationEntity recommendation) {
        this.recommendation= recommendation;
    }


}