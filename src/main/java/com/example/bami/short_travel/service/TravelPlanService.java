package com.example.bami.short_travel.service;

import com.example.bami.short_travel.dto.PlaceDTO;
import com.example.bami.short_travel.dto.RecommendationDTO;
import com.example.bami.short_travel.dto.SaveShortTravelDTO;
import com.example.bami.short_travel.entity.PlaceEntity;
import com.example.bami.short_travel.entity.RecommendationEntity;
import com.example.bami.short_travel.entity.TravelPlanEntity;
import com.example.bami.short_travel.repository.PlaceRepository;
import com.example.bami.short_travel.repository.RecommendationRepository;
import com.example.bami.short_travel.repository.TravelPlanRepository;
import com.example.bami.user.domain.BamiUser;
import com.example.bami.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TravelPlanService {

    private final UserRepository userRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final RecommendationRepository recommendationRepository;
    private final PlaceRepository placeRepository;


    public TravelPlanService(UserRepository userRepository, TravelPlanRepository travelPlanRepository,
                             RecommendationRepository recommendationRepository, PlaceRepository placeRepository) {
        this.userRepository = userRepository;
        this.travelPlanRepository = travelPlanRepository;
        this.recommendationRepository = recommendationRepository;
        this.placeRepository = placeRepository;
    }

    @Transactional
    public void saveTravelPlan(SaveShortTravelDTO saveShortTravelDTO, int userId) {
        String startDate = saveShortTravelDTO.getStartDate().substring(0, 10);
        String endDate = saveShortTravelDTO.getEndDate().substring(0, 10);

        TravelPlanEntity travelPlan = new TravelPlanEntity();
        BamiUser user = userRepository.findById(userId);

        if (user == null){
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        travelPlan.setUser(user);
        travelPlan.setDate(startDate, endDate);

        for (RecommendationDTO recommendation : saveShortTravelDTO.getRecommendations()) {
            RecommendationEntity recommendationDay = new RecommendationEntity(recommendation.getDay());
            for (PlaceDTO placeDTO : recommendation.getPlaces()) {
                PlaceEntity place = new PlaceEntity(placeDTO.getName(),
                        placeDTO.getRoadAddress(),
                        placeDTO.getLotnoAddress(),
                        placeDTO.getLatitude(),
                        placeDTO.getLongitude());
                recommendationDay.addPlace(place);

                placeRepository.save(place);
            }

            recommendationRepository.save(recommendationDay);
            travelPlan.addRecommendationDay(recommendationDay);
        }
        travelPlanRepository.save(travelPlan);
        user.addTravelPlan(travelPlan);
        userRepository.save(user);
    }
}
