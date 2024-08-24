package com.example.bami.weather.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@Builder
public class WeatherResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private HttpStatus status;
    private String message;
    private double cur_temperature;
    private double low_temperature;
    private double high_temperature;
    private String city;

}
