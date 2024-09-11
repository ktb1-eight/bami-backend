package com.example.bami.short_travel.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaveShortTravelDTO {
    private List<RecommendationDTO> recommendations;
    private String startDate;
    private String endDate;
    private float latitude;
    private float longitude;
}
