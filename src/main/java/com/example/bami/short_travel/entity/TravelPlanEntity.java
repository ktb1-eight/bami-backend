package com.example.bami.short_travel.entity;

import com.example.bami.user.domain.BamiUser;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class TravelPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL)
    private List<RecommendationEntity> recommendationDays = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private BamiUser user;

    @Column(nullable = false)
    private String startDate;  // 여행 시작 날짜

    @Column(nullable = false)
    private String endDate;  // 여행 종료 날짜

    @Column(nullable = false)
    private float latitude;

    @Column(nullable = false)
    private float longitude;

    private String address;


    public void addRecommendationDay(RecommendationEntity recommendation) {
        recommendationDays.add(recommendation);
        recommendation.setTravelPlan(this);
    }

    public void setUser(BamiUser user) {
        this.user = user;
    }

    public void setDate(String startDate, String endDate){
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setLocation(float latitude, float longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

}