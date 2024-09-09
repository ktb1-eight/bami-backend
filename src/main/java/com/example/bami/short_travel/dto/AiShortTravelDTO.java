package com.example.bami.short_travel.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiShortTravelDTO {
    private String ADDRESS; // 주소
    private String MVMN_NM; // "자가용", "대중교통 등"
    private String GENDER; //"남성", "여성"
    private int AGE_GRP; // 20대면 20, 30대면 30 등등
    private int TRAVEL_STYL_1;
    private int TRAVEL_STYL_2;
    private int TRAVEL_STYL_3;
    private int TRAVEL_STYL_5;
    private int TRAVEL_STYL_6;
    private int TRAVEL_STYL_7;
    private int TRAVEL_STYL_8;
    private int TRAVEL_MOTIVE_1; // 여행 동기
    private String REL_CD_Categorized; // 동행인 정보

    public static AiShortTravelDTO toAiShortTravelDTO(ShortTravelDTO shortTravelDTO, String address){
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

        aiShortTravelDTO.ADDRESS = address;
        if (shortTravelDTO.getTransport().equals("차량 이용")){
            aiShortTravelDTO.MVMN_NM = "자가용";
        } else{
            aiShortTravelDTO.MVMN_NM = "대중교통 등";
        }
        aiShortTravelDTO.GENDER = shortTravelDTO.getGender().replace("성", "");
        aiShortTravelDTO.AGE_GRP = Integer.parseInt(shortTravelDTO.getAgeGroup().replace("대", "").replace(" 이상", "").replace(" 미만", ""));
        aiShortTravelDTO.TRAVEL_STYL_1 = Integer.parseInt(shortTravelDTO.getPreferences().getNature());
        aiShortTravelDTO.TRAVEL_STYL_2 = Integer.parseInt(shortTravelDTO.getPreferences().getDuration());
        aiShortTravelDTO.TRAVEL_STYL_3 = Integer.parseInt(shortTravelDTO.getPreferences().getNewPlaces());
        aiShortTravelDTO.TRAVEL_STYL_5 = Integer.parseInt(shortTravelDTO.getPreferences().getRelaxation());
        aiShortTravelDTO.TRAVEL_STYL_6 = Integer.parseInt(shortTravelDTO.getPreferences().getExploration());
        aiShortTravelDTO.TRAVEL_STYL_7 = Integer.parseInt(shortTravelDTO.getPreferences().getPlanning());
        aiShortTravelDTO.TRAVEL_STYL_8 = Integer.parseInt(shortTravelDTO.getPreferences().getPhotography());
        for (int i = 0; i < travelMotivations.size(); i++){
            if (travelMotivations.get(i).equals(shortTravelDTO.getTravelPurpose())){
                aiShortTravelDTO.TRAVEL_MOTIVE_1 = i + 1;
                break;
            }
        }
        aiShortTravelDTO.REL_CD_Categorized = shortTravelDTO.getCompanion().replace("와", "").replace("과", "");

        System.out.println(aiShortTravelDTO);
        return aiShortTravelDTO;
    }
}
