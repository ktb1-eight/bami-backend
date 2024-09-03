package com.example.bami.city.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequestMapping("/api/longstay")
@RestController
@Tag(name = "장기 여행지 도시 추천", description = "장기 여행지로 추천된 도시의 정보를 조회 및 저장합니다.")
public class CityImageController {

    private final Map<String, String> imageCache = new ConcurrentHashMap<>();

    @Value("${security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @Operation(summary = "도시 이미지 조회", description = "도시 이름을 기반으로 이미지를 검색하여 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 검색 성공", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/city-image/{cityName}")
    public String getCityImage(
            @Parameter(description = "검색할 도시 이름", required = true) @PathVariable String cityName) {
        // 캐시에서 이미지 확인
        return imageCache.computeIfAbsent(cityName, this::fetchCityImageFromApi);
    }

    private String fetchCityImageFromApi(String cityName) {
        try {
            String url = buildImageUrl(cityName);
            HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return handleException(cityName, e, "Client error occurred while fetching image.");
        } catch (HttpServerErrorException e) {
            return handleException(cityName, e, "Server error occurred while fetching image.");
        } catch (RestClientException e) {
            return handleException(cityName, e, "An error occurred while fetching image.");
        } catch (Exception e) {
            return handleException(cityName, e, "Unexpected error occurred while fetching image.");
        }
    }

    private String buildImageUrl(String cityName) {
        return "https://openapi.naver.com/v1/search/image?query=" + cityName + "&display=5";
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        return headers;
    }

    private String handleException(String cityName, Exception e, String errorMessage) {
        log.error("{} for city {}: {}", errorMessage, cityName, e.getMessage());
        return errorMessage;
    }
}