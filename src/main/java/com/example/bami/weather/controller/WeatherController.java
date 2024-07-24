package com.example.bami.weather.controller;

import com.example.bami.weather.dto.WeatherDTO;
import com.example.bami.weather.dto.WeatherResultDTO;
import com.example.bami.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/weather")
@Slf4j
@Tag(name = "초단기예보조회", description = "공공데이터를 이용하여 초단기예보 조회")
public class WeatherController {
    private final WeatherService service;

    @GetMapping
    @Operation(summary = "예보 정보 Get", description = "좌표지점에 대한 예보정보조회기능")
    public WeatherResultDTO getTest(@ParameterObject @ModelAttribute WeatherDTO q) {
        double temperature = service.getTemparature(q);
        double[] LowHighTemperature = service.getHighLowTemperature(q);
        return WeatherResultDTO.builder()
                .status(HttpStatus.OK)
                .message(HttpStatus.OK.toString())
                .cur_temperature(temperature)
                .low_temperature(LowHighTemperature[0])
                .high_temperature(LowHighTemperature[1])
                .build();
    }
}