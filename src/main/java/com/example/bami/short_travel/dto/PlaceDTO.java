package com.example.bami.short_travel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceDTO {
    private String name;         // 장소 이름
    private String roadAddress;  // 도로명 주소
    private String lotnoAddress; // 지번 주소
    private double latitude;     // 위도
    private double longitude;    // 경도
}