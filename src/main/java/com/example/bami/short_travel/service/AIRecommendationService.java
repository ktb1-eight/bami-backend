package com.example.bami.short_travel.service;

import com.example.bami.short_travel.dto.*;
import com.example.bami.weather.service.ReverseGeocodingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AIRecommendationService {

    private final WebClient webClient;
    private ReverseGeocodingService reverseGeocodingService;

    @Value("${app.dns-addr}")
    private String endPoint;

    public AIRecommendationService(WebClient.Builder webClientBuilder, ReverseGeocodingService reverseGeocodingService) {
        this.webClient = webClientBuilder.build();
        this.reverseGeocodingService = reverseGeocodingService;
    }

    public List<RecommendationDTO> getRecommendations(ShortTravelDTO shortTravelDTO) {
        AiShortTravelDTO aiShortTravelDTO = AiShortTravelDTO.toAiShortTravelDTO(shortTravelDTO);

        System.out.println(aiShortTravelDTO);

        // List<Map<String, Object>>로 응답을 받기 위한 설정
        List<Map<String, Object>> recommendations = webClient.post()
                .uri("http://" + endPoint + ":8000/ai/trip/short")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(aiShortTravelDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();  // 동기적으로 결과를 받을 때 block() 사용

        List<RecommendationDTO> recommendationDTOS = new ArrayList<>();
//        // 받아온 응답을 출력
        for (Map<String, Object> recommendation : recommendations) {
            List<Map<String, Object>> places = (List<Map<String, Object>>) recommendation.get("places");

            RecommendationDTO recommendationDTO = new RecommendationDTO((String) recommendation.get("day"));

            for (Map<String, Object> place : places) {
                String name = null;
                String city = null;
                String address = null;
                System.out.println(place.get("city"));
                if (!place.get("name").equals("0.0")){
                    name = (String)place.get("name");
                }
                if (!place.get("city").equals("0.0")){
                    city = (String)place.get("city");
                } else{
                    city = reverseGeocodingService.getAddress(((Number) place.get("latitude")).doubleValue(), ((Number) place.get("longitude")).doubleValue());
                }
                if (!place.get("address").equals("0.0")){
                    address = (String)place.get("address");
                }
                float latitude = ((Number) place.get("latitude")).floatValue();
                float longitude = ((Number) place.get("longitude")).floatValue();

                PlaceDTO placeDTO = new PlaceDTO(name, city, address, latitude, longitude);
                recommendationDTO.addPlaces(placeDTO);
            }

            recommendationDTOS.add(recommendationDTO);
        }
        for (RecommendationDTO recommendationDTO: recommendationDTOS){
            System.out.println(recommendationDTO);
        }
        return recommendationDTOS;
    }

}