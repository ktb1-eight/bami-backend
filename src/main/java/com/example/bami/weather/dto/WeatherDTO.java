package com.example.bami.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WeatherDTO {
    @Schema(defaultValue = "1")
    private int pageNo;       // 페이지 번호
    @Schema(defaultValue = "1000")
    private int numOfRows;    // 한 페이지 결과 수
    private String baseTime;
    private Double nx;
    private Double ny;
    private int baseDate;
}