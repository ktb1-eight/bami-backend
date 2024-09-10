package com.example.bami.short_travel.service;

import com.example.bami.short_travel.dto.PlaceDTO;
import com.example.bami.short_travel.dto.RecommendationDTO;
import com.example.bami.short_travel.dto.SaveShortTravelDTO;
import com.example.bami.short_travel.dto.TravelPlanDTO;
import com.example.bami.short_travel.entity.PlaceEntity;
import com.example.bami.short_travel.entity.RecommendationEntity;
import com.example.bami.short_travel.entity.TravelPlanEntity;
import com.example.bami.short_travel.repository.PlaceRepository;
import com.example.bami.short_travel.repository.RecommendationRepository;
import com.example.bami.short_travel.repository.TravelPlanRepository;
import com.example.bami.user.domain.BamiUser;
import com.example.bami.user.repository.UserRepository;
import com.example.bami.weather.service.ReverseGeocodingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TravelPlanService {

    private final UserRepository userRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final RecommendationRepository recommendationRepository;
    private final PlaceRepository placeRepository;
    private final ReverseGeocodingService reverseGeocodingService;


    @Transactional
    public void saveTravelPlan(SaveShortTravelDTO saveShortTravelDTO, int userId) {
        String startDate = saveShortTravelDTO.getStartDate().substring(0, 10);
        String endDate = saveShortTravelDTO.getEndDate().substring(0, 10);
        float latitude = saveShortTravelDTO.getLatitude();
        float longitude = saveShortTravelDTO.getLongitude();

        TravelPlanEntity travelPlan = new TravelPlanEntity();
        BamiUser user = userRepository.findById(userId);

        if (user == null){
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        travelPlan.setUser(user);
        travelPlan.setDate(startDate, endDate);
        travelPlan.setLocation(latitude, longitude, reverseGeocodingService.getAddress(latitude, longitude));


        for (RecommendationDTO recommendation : saveShortTravelDTO.getRecommendations()) {
            RecommendationEntity recommendationDay = new RecommendationEntity(recommendation.getDay());
            for (PlaceDTO placeDTO : recommendation.getPlaces()) {
                PlaceEntity place = new PlaceEntity(placeDTO.getName(),
                        placeDTO.getCity(),
                        placeDTO.getAddress(),
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

    public List<TravelPlanDTO> getTravelPlansForUser(int userId) {
        BamiUser user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return user.getTravelPlans().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TravelPlanDTO convertToDTO(TravelPlanEntity travelPlan) {
        TravelPlanDTO dto = new TravelPlanDTO();
        dto.setId(travelPlan.getId());
        dto.setStartDate(travelPlan.getStartDate());
        dto.setEndDate(travelPlan.getEndDate());
        dto.setAddress(travelPlan.getAddress());
        dto.setRecommendations(travelPlan.getRecommendationDays().stream()
                .map(this::convertRecommendationToDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private RecommendationDTO convertRecommendationToDTO(RecommendationEntity recommendation) {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setDay(recommendation.getDay() + "일차");
        dto.setPlaces(recommendation.getPlaces().stream()
                .map(this::convertPlaceToDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private PlaceDTO convertPlaceToDTO(PlaceEntity place) {
        return new PlaceDTO(
                place.getName(),
                place.getRoadAddress(),
                place.getLotnoAddress(),
                place.getLatitude(),
                place.getLongitude()
        );
    }
}
