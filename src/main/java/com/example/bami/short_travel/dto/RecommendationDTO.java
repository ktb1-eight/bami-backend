package com.example.bami.short_travel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor

public class RecommendationDTO {
    // "1일차", "2일차"
    private String day;
    // 해당 날짜의 추천 장소 리스트
    private List<PlaceDTO> places;

    public RecommendationDTO(String day){
        this.day = day;
        this.places = new ArrayList<>();
    }

    public void addPlaces(PlaceDTO placeDTO){
        places.add(placeDTO);
    }
}