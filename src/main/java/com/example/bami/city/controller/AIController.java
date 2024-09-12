package com.example.bami.city.controller;

import com.example.bami.city.dto.AIRequestDTO;
import com.example.bami.city.service.AIResponseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@Slf4j
public class AIController {

    private final AIResponseService aiResponseService;


    @PostMapping(value = "/api/longTrip/submit", consumes = "application/json")
    public List<String> submitTravelPlan(@RequestBody AIRequestDTO aiRequestDTO) {
        log.info("Received requested body: {}", aiRequestDTO);
        return aiResponseService.getRecommendations(aiRequestDTO);
    }
}
