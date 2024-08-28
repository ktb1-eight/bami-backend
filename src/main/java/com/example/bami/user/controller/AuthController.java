package com.example.bami.user.controller;

import com.example.bami.user.dto.TokenResponseDto;
import com.example.bami.user.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/api/login/oauth2/code")
public class AuthController {

    private AuthService authService;
    private static final String REFRESH_TOKEN = "refreshToken";

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/kakao")
    public RedirectView loginKakao(@RequestParam("code") String code, HttpServletResponse response) {
        return loginOAuth(code, response, authService::getKakaoToken, authService::getKakaoUserInfo, authService::mapKakaoUserInfo, "kakao");
    }

    @GetMapping("/google")
    public RedirectView loginGoogle(@RequestParam("code") String code, HttpServletResponse response) {
        return loginOAuth(code, response, authService::getGoogleToken, authService::getGoogleUserInfo, authService::mapGoogleUserInfo, "google");
    }

    @GetMapping("/naver")
    public RedirectView loginNaver(@RequestParam("code") String code, HttpServletResponse response) {
        return loginOAuth(code, response, authService::getNaverToken, authService::getNaverUserInfo, authService::mapNaverUserInfo, "naver");
    }

    private <T> RedirectView loginOAuth(String code, HttpServletResponse response,
                                        Function<String, TokenResponseDto> getToken,
                                        Function<String, T> getUserInfo,
                                        Function<T, Map<String, String>> mapUserInfo,
                                        String provider) {

        Map<String, String> tokens = authService.handleUserLogin(code, getToken, getUserInfo, mapUserInfo, provider);

        Cookie refreshTokenCookie = createRefreshTokenCookie(tokens.get(REFRESH_TOKEN));
        response.addCookie(refreshTokenCookie);

        return new RedirectView("http://localhost:3000/login?accessToken=" + tokens.get("accessToken"));
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS를 사용하는 경우
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(604800); // 1주일
        return refreshTokenCookie;
    }
}

