package com.example.bami.city.service;

import com.example.bami.city.domain.Schedule;
import com.example.bami.city.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private ScheduleRepository repository;

    public ScheduleService(ScheduleRepository repository) {
        this.repository = repository;
    }

    public Schedule saveSchedule(Schedule schedule) {
        return repository.save(schedule);
    }
}
