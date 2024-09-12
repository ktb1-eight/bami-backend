package com.example.bami.city.service;

import com.example.bami.city.dto.AIRequestDTO;
import com.example.bami.weather.service.ReverseGeocodingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class AIResponseService {

    private final WebClient webClient;
    private ReverseGeocodingService reverseGeocodingService;

    @Value("${app.dns-addr}")
    private String endPoint;

    public AIResponseService(WebClient.Builder webClientBuilder, ReverseGeocodingService reverseGeocodingService) {
        this.webClient = webClientBuilder.build();
        this.reverseGeocodingService = reverseGeocodingService;
    }

    public List<String> getRecommendations(AIRequestDTO aiRequestDTO) {
//        AiShortTravelDTO aiShortTravelDTO = AiShortTravelDTO.toAiShortTravelDTO(shortTravelDTO);
//
//        System.out.println(aiShortTravelDTO);

        // List<Map<String, Object>>로 응답을 받기 위한 설정http://3.36.73.216:8000/
        System.out.println(aiRequestDTO);
        List<String> response = webClient.post()
                .uri("http://" + "3.36.73.216" + ":8000/api/predict")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(aiRequestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();

        System.out.println(response);       // Check the response body
        return response;
    }
}
