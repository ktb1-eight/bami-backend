package com.example.bami.short_travel.dto;

import lombok.Data;

@Data
public class ShortTravelDTO {
    private String companion;
    private String transport;
    private Preferences preferences;
    private Location location;
    private String travelPurpose;
    private String startDate;
    private String endDate;
    private int day_duration;
    private String gender;
    private String ageGroup;


    @Data
    public static class Preferences {
        private String nature;
        private String duration;
        private String newPlaces;
        private String relaxation;
        private String exploration;
        private String planning;
        private String photography;
    }

    @Data
    public static class Location {
        private float latitude;
        private float longitude;
    }
}