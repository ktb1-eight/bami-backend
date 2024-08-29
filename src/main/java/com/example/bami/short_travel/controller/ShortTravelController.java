package com.example.bami.short_travel.controller;

import com.example.bami.short_travel.dto.PlaceDTO;
import com.example.bami.short_travel.dto.RecommendationDTO;
import com.example.bami.short_travel.dto.ShortTravelDTO;
import com.example.bami.short_travel.service.TravelPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/shortTrip")
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

    private final TravelPlanService travelPlanService;

    public ShortTravelController(TravelPlanService travelPlanService) {
        this.travelPlanService = travelPlanService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveTravelPlan(@RequestBody List<RecommendationDTO> recommendations) {
        // TODO: 8/28/24 유저 정보 가져와서 연동하기, 로그인 안한 유저 예외 처리 하기
        try {
            travelPlanService.saveTravelPlan(recommendations);
            return ResponseEntity.ok("일정이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일정 저장 중 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/submit")
    public List<RecommendationDTO> submitTravelPlan(@RequestBody ShortTravelDTO shortTravelDTO) {
        log.info("Received travel plan: {}", shortTravelDTO);

        List<RecommendationDTO> mockRecommendations = createMockRecommendations();

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