package com.example.bami.city.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class CityController {

    private final Map<String, String> imageCache = new ConcurrentHashMap<>();

    @Value("${security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @GetMapping("/api/city-image/{cityName}")
    public String getCityImage(@PathVariable String cityName) {
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
