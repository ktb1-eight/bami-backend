package com.example.bami.city.service;

import com.example.bami.city.domain.TravelDestination;
import com.example.bami.city.repository.TravelDestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelDestinationService {

    @Autowired
    private TravelDestinationRepository repository;

    public TravelDestination saveDestination(TravelDestination travelDestination) {
        return repository.save(travelDestination);
    }
}
