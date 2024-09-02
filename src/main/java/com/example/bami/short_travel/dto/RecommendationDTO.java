package com.example.bami.short_travel.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationDTO {
    // "1일차", "2일차"
    private String day;
    // 해당 날짜의 추천 장소 리스트
    private List<PlaceDTO> places;
}