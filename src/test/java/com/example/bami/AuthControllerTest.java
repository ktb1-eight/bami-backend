package com.example.bami;

import com.example.bami.user.repository.UserRepository;
import com.example.bami.user.security.JwtTokenProvider;
import com.example.bami.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private static final String ACCESS_TOKEN = "testAccessToken";
    private static final String REFRESH_TOKEN = "testRefreshToken";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginKakao() throws Exception {
        String code = "testCode";
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", ACCESS_TOKEN);
        tokens.put("refreshToken", REFRESH_TOKEN);

        Mockito.when(authService.handleUserLogin(eq(code), any(), any(), any(), eq("kakao"))).thenReturn(tokens);

        mockMvc.perform(get("/api/login/oauth2/code/kakao")
                .param("code", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:3000/login?accessToken=" + ACCESS_TOKEN))
                .andExpect(cookie().value("refreshToken", REFRESH_TOKEN));
    }
}
