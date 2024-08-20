package com.example.bami.city.controller;

import com.example.bami.city.domain.TravelDestination;
import com.example.bami.city.service.TravelDestinationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TravelDestinationController {

    private TravelDestinationService service;

    public TravelDestinationController(TravelDestinationService service) {
        this.service = service;
    }

    @PostMapping("/api/save-destination")
    public TravelDestination saveDestination(@RequestBody TravelDestination travelDestination) {
        System.out.println(travelDestination);
        return service.saveDestination(travelDestination);
    }

}
