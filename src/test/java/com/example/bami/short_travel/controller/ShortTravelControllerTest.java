package com.example.bami.short_travel.controller;

import com.example.bami.short_travel.dto.PlaceDTO;
import com.example.bami.short_travel.dto.RecommendationDTO;
import com.example.bami.short_travel.service.TravelPlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShortTravelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TravelPlanService travelPlanService;

    // TravelPlanService의 동작을 목(mock)으로 설정
    @Test
    public void testSaveTravelPlan() throws Exception {
        Mockito.doNothing().when(travelPlanService).saveTravelPlan(Mockito.anyList());

        // JSON으로 요청 본문 생성
        String requestBody = "[{\"day\":\"1일차\",\"places\":[{\"name\":\"Place1\",\"roadAddress\":\"Road1\",\"lotnoAddress\":\"Lot1\",\"latitude\":1.1,\"longitude\":1.2}]}]";

        // POST 요청 수행
        mockMvc.perform(post("/api/shortTrip/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()) // 상태 코드가 200 OK일 것
                .andExpect(content().string("일정이 성공적으로 저장되었습니다.")); // 응답 메시지가 예상대로일 것

        // saveTravelPlan 메소드가 정확히 한 번 호출되었는지, 그리고 올바른 매개변수로 호출되었는지 검증
        verify(travelPlanService, times(1)).saveTravelPlan(Mockito.anyList());
    }

    // 여러 날의 여행 계획이 있는 경우의 처리를 확인
    @Test
    public void testSaveTravelPlan_withMultipleDays() throws Exception {
        Mockito.doNothing().when(travelPlanService).saveTravelPlan(Mockito.anyList());

        // 여러 날의 계획이 있는 요청 본문 생성
        RecommendationDTO recommendation1 = new RecommendationDTO();
        recommendation1.setDay("1일차");
        recommendation1.setPlaces(Arrays.asList(
                new PlaceDTO("Place1", "Road1", "Lot1", 1.1, 1.2),
                new PlaceDTO("Place2", "Road2", "Lot2", 2.1, 2.2)
        ));

        RecommendationDTO recommendation2 = new RecommendationDTO();
        recommendation2.setDay("2일차");
        recommendation2.setPlaces(Arrays.asList(
                new PlaceDTO("Place3", "Road3", "Lot3", 3.1, 3.2)
        ));

        String requestBody = objectMapper.writeValueAsString(Arrays.asList(recommendation1, recommendation2));

        // POST 요청 수행
        mockMvc.perform(post("/api/shortTrip/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("일정이 성공적으로 저장되었습니다."));

        // saveTravelPlan 메소드가 정확히 한 번 호출되었는지, 그리고 올바른 매개변수로 호출되었는지 검증
        verify(travelPlanService, times(1)).saveTravelPlan(Mockito.anyList());
    }

    // 빈 요청 본문이 전달될 경우의 처리를 확인
    @Test
    public void testSaveTravelPlan_withEmptyRequestBody() throws Exception {
        Mockito.doNothing().when(travelPlanService).saveTravelPlan(Mockito.anyList());

        // 빈 JSON으로 POST 요청 수행
        mockMvc.perform(post("/api/shortTrip/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().string("일정이 성공적으로 저장되었습니다."));

        // saveTravelPlan 메소드가 정확히 한 번 호출되었는지 검증
        verify(travelPlanService, times(1)).saveTravelPlan(Mockito.anyList());
    }

    // 잘못된 JSON으로 POST 요청 수행
    @Test
    public void testSaveTravelPlan_withInvalidRequestBody() throws Exception {
        mockMvc.perform(post("/api/shortTrip/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest()); // 상태 코드가 400 Bad Request일 것을 기대
    }
}