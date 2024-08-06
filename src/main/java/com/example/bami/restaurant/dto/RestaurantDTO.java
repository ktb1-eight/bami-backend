package com.example.bami.restaurant.dto;

import lombok.Data;

@Data
public class RestaurantDTO {
    private String name;
    private String address;
    private String phone;
    private double distance;
    private String category;
    private String placeURL;
    private String imageURL;
    private float rating;
    private int userRatingsTotal;
}