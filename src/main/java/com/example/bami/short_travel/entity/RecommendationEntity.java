package com.example.bami.short_travel.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class RecommendationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int day;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private TravelPlanEntity travelPlan;

    @OneToMany(mappedBy = "recommendation", cascade = CascadeType.ALL)
    private List<PlaceEntity> places = new ArrayList<>();

    public RecommendationEntity(String day) {
        this.day = Integer.parseInt(day.replace("일차", ""));
    }

    protected RecommendationEntity() {}

    public void setTravelPlan(TravelPlanEntity travelPlan) {
        this.travelPlan = travelPlan;
    }

    public void addPlace(PlaceEntity place) {
        places.add(place);
        place.setRecommendation(this);
    }

}
