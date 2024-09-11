package com.example.bami.short_travel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceDTO {
    private String name;         // 장소 이름
    private String city;  // 도로명 주소
    private String address; // 지번 주소
    private float longitude;    // 경도
    private float latitude;     // 위도
}