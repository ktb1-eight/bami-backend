package com.example.bami.city.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class CityController {

    private Map<String, String> imageCache = new ConcurrentHashMap<>();

    @Value("${security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @GetMapping("/api/city-image/{cityName}")
    public String getCityImage(@PathVariable String cityName) {
        // 캐시에서 확인
        if(imageCache.containsKey(cityName)) {
            return imageCache.get(cityName);
        }

        try {
            // 네이버 이미지 검색 API 호출
            String url = "https://openapi.naver.com/v1/search/image?query=" + cityName + "&display=5";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            String responseBody = response.getBody();

            //응답 결과를 캐싱
            imageCache.put(cityName, responseBody);

            return responseBody;
        } catch (HttpClientErrorException e) {
            // 클라이언트 오류 처리 (4xx)
            log.error("Client error occurred while fetching image for city {}: {}", cityName, e.getMessage());
            return "Client error occurred while fetching image.";
        } catch (HttpServerErrorException e) {
            // 서버 오류 처리 (5xx)
            log.error("Server error occurred while fetching image for city {}: {}", cityName, e.getMessage());
            return "Server error occurred while fetching image.";
        } catch (RestClientException e) {
            // 기타 RestTemplate 관련 오류 처리
            log.error("Error occurred while fetching image for city {}: {}", cityName, e.getMessage());
            return "An error occurred while fetching image.";
        } catch (Exception e) {
            // 그 외의 모든 예외 처리
            log.error("Unexpected error occurred while fetching image for city {}: {}", cityName, e.getMessage());
            return "Unexpected error occurred while fetching image.";
        }
    }
}
