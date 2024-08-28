package com.example.bami.city.service;

import com.example.bami.city.domain.TravelDestination;
import com.example.bami.city.repository.TravelDestinationRepository;
import org.springframework.stereotype.Service;

@Service
public class TravelDestinationService {

    private TravelDestinationRepository repository;

    public TravelDestinationService(TravelDestinationRepository repository) {
        this.repository = repository;
    }

    public TravelDestination saveDestination(TravelDestination travelDestination) {
        return repository.save(travelDestination);
    }
}
