package com.example.bami.short_travel.controller;

import com.example.bami.short_travel.dto.ShortTravelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class ShortTravelController {

    @PostMapping("/submit")
    public void submitTravelPlan(@RequestBody ShortTravelDTO shortTravelDTO) {
        log.info("Received travel plan:");
        log.info("Travel Purpose: {}", shortTravelDTO.getTravelPurpose());
        log.info("Companion: {}", shortTravelDTO.getCompanion());
        log.info("Transport: {}", shortTravelDTO.getTransport());
        log.info("Preferences: {}", shortTravelDTO.getPreferences());
        log.info("Location: {}", shortTravelDTO.getLocation());
    }
}
