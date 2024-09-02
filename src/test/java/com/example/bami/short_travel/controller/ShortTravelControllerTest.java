package com.example.bami.short_travel.controller;

import com.example.bami.short_travel.dto.PlaceDTO;
import com.example.bami.short_travel.dto.RecommendationDTO;
import com.example.bami.short_travel.service.TravelPlanService;
import com.example.bami.user.domain.BamiUser;
import com.example.bami.user.repository.UserRepository;
import com.example.bami.user.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Map;

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

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    //여행 일정을 저장 후 요청 결과 테스트
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testSaveTravelPlan() throws Exception {
        String email = "testuser@example.com";
        BamiUser mockUser = Mockito.mock(BamiUser.class);
        Mockito.when(mockUser.getId()).thenReturn(1);
        Mockito.when(mockUser.getEmail()).thenReturn(email);

        Mockito.when(jwtTokenProvider.resolveToken(Mockito.any(HttpServletRequest.class)))
                .thenReturn("valid-token");
        Mockito.when(jwtTokenProvider.validateToken("valid-token"))
                .thenReturn(true);
        Mockito.when(jwtTokenProvider.getClaimsAsMap("valid-token"))
                .thenReturn(Map.of("email", email));
        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(mockUser);

        Mockito.doNothing().when(travelPlanService).saveTravelPlan(Mockito.anyList(), Mockito.eq(1));

        String requestBody = "[{\"day\":\"1일차\",\"places\":[{\"name\":\"Place1\",\"roadAddress\":\"Road1\",\"lotnoAddress\":\"Lot1\",\"latitude\":1.1,\"longitude\":1.2}]}]";

        mockMvc.perform(post("/api/shortTrip/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("일정이 성공적으로 저장되었습니다.")); // 응답 메시지가 예상대로일 것

        // saveTravelPlan 메소드가 정확히 한 번 호출되었는지, 그리고 올바른 매개변수로 호출되었는지 검증
        verify(travelPlanService, times(1)).saveTravelPlan(Mockito.anyList(), Mockito.eq(1));
    }

    // 여러 날의 여행 일정을 저장 후 요청 테스트
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testSaveTravelPlan_withMultipleDays() throws Exception {
        String email = "testuser@example.com";
        BamiUser mockUser = Mockito.mock(BamiUser.class);
        Mockito.when(mockUser.getId()).thenReturn(1);
        Mockito.when(mockUser.getEmail()).thenReturn(email);

        Mockito.when(jwtTokenProvider.resolveToken(Mockito.any(HttpServletRequest.class)))
                .thenReturn("valid-token");
        Mockito.when(jwtTokenProvider.validateToken("valid-token"))
                .thenReturn(true);
        Mockito.when(jwtTokenProvider.getClaimsAsMap("valid-token"))
                .thenReturn(Map.of("email", email));
        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(mockUser);

        Mockito.doNothing().when(travelPlanService).saveTravelPlan(Mockito.anyList(), Mockito.eq(1));

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

        mockMvc.perform(post("/api/shortTrip/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("일정이 성공적으로 저장되었습니다."));

        verify(travelPlanService, times(1)).saveTravelPlan(Mockito.anyList(), Mockito.eq(1));
    }

    //빈 요청 본문을 준 후 요청 테스트
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testSaveTravelPlan_withEmptyRequestBody() throws Exception {
        String email = "testuser@example.com";
        BamiUser mockUser = Mockito.mock(BamiUser.class);
        Mockito.when(mockUser.getId()).thenReturn(1);
        Mockito.when(mockUser.getEmail()).thenReturn(email);

        Mockito.when(jwtTokenProvider.resolveToken(Mockito.any(HttpServletRequest.class)))
                .thenReturn("valid-token");
        Mockito.when(jwtTokenProvider.validateToken("valid-token"))
                .thenReturn(true);
        Mockito.when(jwtTokenProvider.getClaimsAsMap("valid-token"))
                .thenReturn(Map.of("email", email));
        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(mockUser);

        Mockito.doNothing().when(travelPlanService).saveTravelPlan(Mockito.anyList(), Mockito.eq(1));

        mockMvc.perform(post("/api/shortTrip/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().string("일정이 성공적으로 저장되었습니다."));

        verify(travelPlanService, times(1)).saveTravelPlan(Mockito.anyList(), Mockito.eq(1));
    }

    //잘못된 JSON 형식의 요청이 주어졌을 때, 400 Bad Request 에러를 반환하는지 확인
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testSaveTravelPlan_withInvalidRequestBody() throws Exception {
        mockMvc.perform(post("/api/shortTrip/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
}