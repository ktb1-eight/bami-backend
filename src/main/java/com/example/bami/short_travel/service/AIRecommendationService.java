package com.example.bami.short_travel.service;

import com.example.bami.short_travel.dto.RecommendationDTO;
import com.example.bami.short_travel.dto.ShortTravelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class AIRecommendationService {

    private final WebClient webClient;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public AIRecommendationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<List<RecommendationDTO>> getRecommendations(ShortTravelDTO shortTravelDTO) {
        return webClient.post()
                .uri(aiServerUrl + "/trip/short")
                .bodyValue(shortTravelDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RecommendationDTO>>() {})
                .doOnError(e -> log.error("AI 서버에서 추천 데이터를 가져오는 중 오류 발생", e));
    }
}