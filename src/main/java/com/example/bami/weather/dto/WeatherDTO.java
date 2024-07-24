package com.example.bami.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WeatherDTO {
    @Schema(defaultValue = "1")
    private int pageNo;       // 페이지 번호
    @Schema(defaultValue = "1000")
    private int numOfRows;    // 한 페이지 결과 수
    @Schema(defaultValue = "0730")
    private String baseTime;  // 기준 시간
    @Schema(defaultValue = "55")
    private double nx;           // 예보 지점 X 좌표
    @Schema(defaultValue = "127")
    private double ny;           // 예보 지점 Y 좌표
    @Schema(defaultValue = "20240722")
    private int baseDate;     // 기준 날짜
}