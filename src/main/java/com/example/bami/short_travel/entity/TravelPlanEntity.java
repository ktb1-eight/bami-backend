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


    public void addRecommendationDay(RecommendationEntity recommendation) {
        recommendationDays.add(recommendation);
        recommendation.setTravelPlan(this);
    }

    public void setUser(BamiUser user) {
        this.user = user;
    }
}