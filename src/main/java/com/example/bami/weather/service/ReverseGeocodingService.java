package com.example.bami.weather.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReverseGeocodingService {

    private final GeoApiContext context;

    @Autowired
    public ReverseGeocodingService(@Value("${google.maps.key}") String apiKey) {
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public String getAddress(double latitude, double longitude) {
        try {
            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, new LatLng(latitude, longitude))
                    .language("ko") // 결과를 한글로 반환
                    .await();
            StringBuilder sb = new StringBuilder();
            if (results.length > 0) {
                String[] address = results[0].formattedAddress.split(" ");
                sb.append(address[1])
                        .append(" ")
                        .append(address[2])
                        .append(" ")
                        .append(address[3]);

                return sb.toString();
            } else {
                return "주소를 찾을 수 없습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }
}