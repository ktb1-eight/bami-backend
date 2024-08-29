package com.example.bami.city.controller;

import com.example.bami.city.domain.Schedule;
import com.example.bami.city.service.ScheduleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {

    private ScheduleService service;

    public ScheduleController(ScheduleService service) {
        this.service = service;
    }

    @PostMapping("/api/save-destination")
    public Schedule saveSchedule(@RequestBody Schedule schedule) {
        return service.saveSchedule(schedule);
    }

}
