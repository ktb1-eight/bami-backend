package com.example.bami.short_travel.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TravelPlanDTO {
    private Long id;
    private List<RecommendationDTO> recommendations = new ArrayList<>();
    private String startDate;
    private String endDate;
}
