package com.example.bami.restaurant.service;

import com.example.bami.restaurant.dto.RestaurantDTO;
import com.example.bami.restaurant.dto.RestaurantReqDTO;
import com.example.bami.restaurant.dto.RestaurantResultDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RestaurantService {

    private final WebClient webClient;
    private final GeoApiContext geoApiContext;
    private final String googleApiKey;

    private static final double SEOUL_CITY_LAT = 37.56570672735707;
    private static final double SEOUL_CITY_LON = 126.97736222453338;

    public RestaurantService(@Value("${kakao.api.key}") String kakaoApiKey,
                             @Value("${google.maps.key}") String googleApiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiKey)
                .build();
        this.geoApiContext = new GeoApiContext.Builder()
                .apiKey(googleApiKey)
                .build();
        this.googleApiKey = googleApiKey;
    }

    @PostConstruct
    @Cacheable(value = "restaurantCache", key = "'seoulCityRestaurants'")
    public RestaurantResultDTO getSeoulCityRestaurants() {
        log.info("Fetching Seoul City Restaurants...");
        RestaurantReqDTO seoulReqDTO = new RestaurantReqDTO();
        seoulReqDTO.setNx(SEOUL_CITY_LAT);
        seoulReqDTO.setNy(SEOUL_CITY_LON);

        List<RestaurantDTO> restaurants = getNearbyRestaurants(seoulReqDTO);

        RestaurantResultDTO result = RestaurantResultDTO.builder()
                .status(HttpStatus.OK)
                .message("서울 시청 주변 맛집 정보입니다.")
                .city("서울특별시")
                .restaurants(restaurants)
                .build();

        log.info("Seoul City Restaurants have been cached.");
        return result;
    }

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    @CachePut(value = "restaurantCache", key = "'seoulCityRestaurants'")
    public void updateSeoulCityRestaurants() {
        // This method returns void and takes no parameters
        log.info("Updating Seoul City Restaurants Cache...");
        getSeoulCityRestaurants(); // Fetch and cache the data
    }

    public List<RestaurantDTO> getNearbyRestaurants(RestaurantReqDTO q) {
        String uriString = String.format("/v2/local/search/keyword.json?y=%f&x=%f&radius=2000&query=%s",
                q.getNx(), q.getNy(), "맛집");

        String response = webClient.get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        List<RestaurantDTO> restaurants = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode documents = rootNode.path("documents");

            for (JsonNode node : documents) {
                RestaurantDTO restaurant = new RestaurantDTO();
                String placeName = node.path("place_name").asText();
                double lat = node.path("y").asDouble();
                double lon = node.path("x").asDouble();

                PlacesSearchResult placeDetails = getPlaceDetailsFromGoogle(placeName, lat, lon);

                restaurant.setImageURL(null);
                if (placeDetails != null) {
                    restaurant.setImageURL(getPlaceImageUrl(placeDetails));
                    restaurant.setRating(placeDetails.rating);
                    restaurant.setUserRatingsTotal(placeDetails.userRatingsTotal);
                }

                if (restaurant.getImageURL() == null) continue;
                restaurant.setName(placeName);
                restaurant.setAddress(node.path("road_address_name").asText());
                restaurant.setPhone(node.path("phone").asText());
                restaurant.setDistance(node.path("distance").asDouble());
                restaurant.setCategory(node.path("category_name").asText());
                restaurant.setPlaceURL(node.path("place_url").asText());

                restaurants.add(restaurant);
                if (restaurants.size() == 6) break; // 최대 6개의 맛집 정보만 사용
            }
        } catch (Exception e) {
            log.error("Error parsing JSON response", e);
        }

        return restaurants;
    }

    private PlacesSearchResult getPlaceDetailsFromGoogle(String placeName, double lat, double lon) {
        try {
            PlacesSearchResponse response = PlacesApi.nearbySearchQuery(geoApiContext, new com.google.maps.model.LatLng(lat, lon))
                    .radius(2000)
                    .keyword(placeName)
                    .type(PlaceType.RESTAURANT)
                    .await();

            if (response.results.length > 0) {
                return response.results[0];  // 첫 번째 결과를 반환
            }
        } catch (Exception e) {
            log.error("Error fetching place details from Google Places API", e);
        }
        return null;
    }

    private String getPlaceImageUrl(PlacesSearchResult placeDetails) {
        if (placeDetails.photos != null && placeDetails.photos.length > 0) {
            return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + placeDetails.photos[0].photoReference
                    + "&key="
                    + googleApiKey;
        }
        return null;
    }
}