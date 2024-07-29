package com.example.bami.restaurant.service;

import com.example.bami.restaurant.dto.RestaurantDTO;
import com.example.bami.restaurant.dto.RestaurantReqDTO;
import com.google.maps.model.PlaceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RestaurantService {
    private final WebClient webClient;
    private final GeoApiContext geoApiContext;
    private final String googleApiKey;

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
                double lat = node.path("y").asDouble();
                double lon = node.path("x").asDouble();

                // Google Places API를 사용하여 이미지 URL과 이름 가져오기
                PlacesSearchResult placeDetails = getPlaceDetailsFromGoogle(node.path("place_name").asText(), lat, lon);

                restaurant.setImageURL(null);
                if (placeDetails != null) {
                    restaurant.setImageURL(getPlaceImageUrl(placeDetails));
                    restaurant.setRating(placeDetails.rating);
                    restaurant.setUserRatingsTotal(placeDetails.userRatingsTotal);
                }
                restaurant.setName(node.path("place_name").asText());
                restaurant.setAddress(node.path("road_address_name").asText());
                restaurant.setPhone(node.path("phone").asText());
                restaurant.setDistance(node.path("distance").asDouble());
                restaurant.setCategory(node.path("category_name").asText());
                restaurant.setPlaceURL(node.path("place_url").asText());


                restaurants.add(restaurant);
                if (restaurants.size() == 6) break;
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