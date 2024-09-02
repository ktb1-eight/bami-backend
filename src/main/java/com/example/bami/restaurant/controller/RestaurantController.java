package com.example.bami.restaurant.controller;

import com.example.bami.restaurant.dto.RestaurantDTO;
import com.example.bami.restaurant.dto.RestaurantReqDTO;
import com.example.bami.restaurant.dto.RestaurantResultDTO;
import com.example.bami.restaurant.service.RestaurantService;
import com.example.bami.weather.service.ReverseGeocodingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/restaurant")
@Slf4j
@Tag(name = "주변 맛집 조회", description = "현재 위치를 이용하여 주변 맛집 조회")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final ReverseGeocodingService reverseGeocodingService;

    @GetMapping
    @Operation(summary = "맛집 정보 Get", description = "좌표지점에 대한 맛집정보조회기능")
    public RestaurantResultDTO getRestaurants(@ParameterObject @ModelAttribute RestaurantReqDTO q) {
        if (q.getNx() == null || q.getNy() == null) {
            log.info("위치 정보가 없어 서울 시청 맛집 데이터를 반환합니다.");
            return restaurantService.getSeoulCityRestaurants();
        } else {
            String city = reverseGeocodingService.getAddress(q.getNx(), q.getNy());
            List<RestaurantDTO> restaurants = restaurantService.getNearbyRestaurants(q);

            return RestaurantResultDTO.builder()
                    .status(HttpStatus.OK)
                    .message(HttpStatus.OK.toString())
                    .city(city)
                    .restaurants(restaurants)
                    .build();
        }
    }
}