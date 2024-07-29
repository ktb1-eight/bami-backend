package com.example.bami.restaurant.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class RestaurantResultDTO {
    private HttpStatus status;
    private String message;
    private String city;
    private List<RestaurantDTO> restaurants;
}