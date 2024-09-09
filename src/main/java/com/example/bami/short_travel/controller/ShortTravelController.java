package com.example.bami.short_travel.controller;

import com.example.bami.short_travel.dto.*;
import com.example.bami.short_travel.service.TravelPlanService;
import com.example.bami.user.domain.BamiUser;
import com.example.bami.user.repository.UserRepository;
import com.example.bami.user.security.JwtTokenProvider;
import com.example.bami.weather.service.ReverseGeocodingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/shortTrip")
@AllArgsConstructor
@Slf4j
public class ShortTravelController {


//    public ShortTravelController(AIRecommendationService aiRecommendationService) {
//        this.aiRecommendationService = aiRecommendationService;
//    }
//
//    @PostMapping("/submit")
//    public Mono<List<RecommendationDTO>> submitTravelPlan(@RequestBody ShortTravelDTO shortTravelDTO) {
//        log.info("Received travel plan: {}", shortTravelDTO);
//        return aiRecommendationService.getRecommendations(shortTravelDTO);
//    }

//    private final AIRecommendationService aiRecommendationService;

    private final TravelPlanService travelPlanService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @PostMapping("/save")
    public ResponseEntity<String> saveTravelPlan(@RequestBody SaveShortTravelDTO saveShortTravelDTO, HttpServletRequest request) {
        log.info(saveShortTravelDTO.getStartDate());
        log.info(saveShortTravelDTO.getEndDate());

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        String email = jwtTokenProvider.getClaimsAsMap(token).get("email");
        BamiUser bamiUser = userRepository.findByEmail(email);
        if (bamiUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
        }

        int userId = bamiUser.getId();

        if (userId == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            travelPlanService.saveTravelPlan(saveShortTravelDTO, userId);
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