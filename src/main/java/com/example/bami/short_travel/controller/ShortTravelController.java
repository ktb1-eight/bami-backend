package com.example.bami.short_travel.controller;

import com.example.bami.short_travel.dto.PlaceDTO;
import com.example.bami.short_travel.dto.RecommendationDTO;
import com.example.bami.short_travel.dto.ShortTravelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
성
@RestController
@RequestMapping("/api")
@Slf4j
public class ShortTravelController {

//    private final AIRecommendationService aiRecommendationService;

//    public ShortTravelController(AIRecommendationService aiRecommendationService) {
//        this.aiRecommendationService = aiRecommendationService;
//    }
//
//    @PostMapping("/submit")
//    public Mono<List<RecommendationDTO>> submitTravelPlan(@RequestBody ShortTravelDTO shortTravelDTO) {
//        log.info("Received travel plan: {}", shortTravelDTO);
//        return aiRecommendationService.getRecommendations(shortTravelDTO);
//    }

    @PostMapping("/submit")
    public List<RecommendationDTO> submitTravelPlan(@RequestBody ShortTravelDTO shortTravelDTO) {
        log.info("Received travel plan: {}", shortTravelDTO);

        // 가짜 데이터 생성
        List<RecommendationDTO> mockRecommendations = createMockRecommendations();

        // 프론트엔드에 가짜 데이터를 반환
        return mockRecommendations;
    }

    private List<RecommendationDTO> createMockRecommendations() {
        List<RecommendationDTO> recommendations = new ArrayList<>();

        RecommendationDTO day1 = new RecommendationDTO();
        day1.setDay("1일차");
        day1.setPlaces(List.of(
                new PlaceDTO("해운대 해수욕장", "부산광역시 해운대구", "부산광역시 해운대구 중동 1015", 35.158698, 129.160384),
                new PlaceDTO("부산 타워", "부산광역시 중구", "부산광역시 중구 광복동2가 1-2", 35.101975, 129.032213),
                new PlaceDTO("해운대 해수욕장", "부산광역시 해운대구", "부산광역시 해운대구 중동 1015", 35.158698, 129.160384),
                new PlaceDTO("부산 타워", "부산광역시 중구", "부산광역시 중구 광복동2가 1-2", 35.101975, 129.032213),
                new PlaceDTO("해운대 해수욕장", "부산광역시 해운대구", "부산광역시 해운대구 중동 1015", 35.158698, 129.160384),
                new PlaceDTO("부산 타워", "부산광역시 중구", "부산광역시 중구 광복동2가 1-2", 35.101975, 129.032213)
        ));

        RecommendationDTO day2 = new RecommendationDTO();
        day2.setDay("2일차");
        day2.setPlaces(List.of(
                new PlaceDTO("감천문화마을", "부산광역시 사하구", "부산광역시 사하구 감천동 10-13", 35.097317, 128.987374),
                new PlaceDTO("광안리 해수욕장", "부산광역시 수영구", "부산광역시 수영구 광안동 192-20", 35.153087, 129.118556),
                new PlaceDTO("해운대 해수욕장", "부산광역시 해운대구", "부산광역시 해운대구 중동 1015", 35.158698, 129.160384),
                new PlaceDTO("부산 타워", "부산광역시 중구", "부산광역시 중구 광복동2가 1-2", 35.101975, 129.032213),
                new PlaceDTO("해운대 해수욕장", "부산광역시 해운대구", "부산광역시 해운대구 중동 1015", 35.158698, 129.160384),
                new PlaceDTO("부산 타워", "부산광역시 중구", "부산광역시 중구 광복동2가 1-2", 35.101975, 129.032213)
        ));

        recommendations.add(day1);
        recommendations.add(day2);

        return recommendations;
    }
}