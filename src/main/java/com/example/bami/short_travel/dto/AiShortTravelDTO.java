package com.example.bami.short_travel.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiShortTravelDTO {
    private float latitude;     // 위도
    private float longitude;    // 경도
    private String mvmn_nm; // "자가용", "대중교통 등"
    private String gender; //"남성", "여성"
    private int age_grp; // 20대면 20, 30대면 30 등등
    private int day;
    private int travel_styl_1;
    private int travel_styl_2;
    private int travel_styl_3;
    private int travel_styl_5;
    private int travel_styl_6;
    private int travel_styl_7;
    private int travel_styl_8;
    private int travel_motive_1; // 여행 동기
    private String rel_cd_categorized; // 동행인 정보


    public static AiShortTravelDTO toAiShortTravelDTO(ShortTravelDTO shortTravelDTO){
        AiShortTravelDTO aiShortTravelDTO = new AiShortTravelDTO();
        List<String> travelMotivations = List.of(
                "일상적인 환경 및 역할에서의 탈출, 지루함 탈피",
                "쉴 수 있는 기회, 육체 피로 해결 및 정신적인 휴식",
                "여행 동반자와의 친밀감 및 유대감 증진",
                "진정한 자아 찾기 또는 자신을 되돌아볼 기회 찾기",
                "SNS 사진 등록 등 과시",
                "운동, 건강 증진 및 충전",
                "새로운 경험 추구",
                "역사 탐방, 문화적 경험 등 교육적 동기",
                "특별한 목적(칠순여행, 신혼여행, 수학여행, 인센티브여행)",
                "기타"
        );

        aiShortTravelDTO.latitude = shortTravelDTO.getLocation().getLatitude();
        aiShortTravelDTO.longitude = shortTravelDTO.getLocation().getLongitude();
        aiShortTravelDTO.day = shortTravelDTO.getDay_duration();
        if (shortTravelDTO.getTransport().equals("차량 이용")){
            aiShortTravelDTO.mvmn_nm = "자가용";
        } else{
            aiShortTravelDTO.mvmn_nm = "대중교통 등";
        }
        aiShortTravelDTO.gender = shortTravelDTO.getGender();
        aiShortTravelDTO.age_grp = Integer.parseInt(shortTravelDTO.getAgeGroup().replace("대", "").replace(" 이상", "").replace(" 미만", ""));
        aiShortTravelDTO.travel_styl_1 = Integer.parseInt(shortTravelDTO.getPreferences().getNature());
        aiShortTravelDTO.travel_styl_2 = Integer.parseInt(shortTravelDTO.getPreferences().getDuration());
        aiShortTravelDTO.travel_styl_3 = Integer.parseInt(shortTravelDTO.getPreferences().getNewPlaces());
        aiShortTravelDTO.travel_styl_5 = Integer.parseInt(shortTravelDTO.getPreferences().getRelaxation());
        aiShortTravelDTO.travel_styl_6 = Integer.parseInt(shortTravelDTO.getPreferences().getExploration());
        aiShortTravelDTO.travel_styl_7 = Integer.parseInt(shortTravelDTO.getPreferences().getPlanning());
        aiShortTravelDTO.travel_styl_8 = Integer.parseInt(shortTravelDTO.getPreferences().getPhotography());
        for (int i = 0; i < travelMotivations.size(); i++){
            if (travelMotivations.get(i).equals(shortTravelDTO.getTravelPurpose())){
                aiShortTravelDTO.travel_motive_1 = i + 1;
                break;
            }
        }
        aiShortTravelDTO.rel_cd_categorized = shortTravelDTO.getCompanion().replace("와", "").replace("과", "");

        return aiShortTravelDTO;
    }
}