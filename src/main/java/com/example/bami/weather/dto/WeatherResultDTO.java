package com.example.bami.weather.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class WeatherResultDTO {
    private HttpStatus status;
    private String message;
    private double cur_temperature;
}
