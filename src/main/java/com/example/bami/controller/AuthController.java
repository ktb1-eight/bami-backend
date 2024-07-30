package com.example.bami.controller;

import com.example.bami.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/login/oauth2/code")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/kakao")
    public RedirectView loginKakao(@RequestParam("code") String code, HttpServletResponse response) {
        Map<String, String> tokens = authService.handleUserLogin(code, authService::getKakaoToken, authService::getKakaoUserInfo, authService::mapKakaoUserInfo);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS를 사용하는 경우
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(604800); // 1주일

        response.addCookie(refreshTokenCookie);

        return new RedirectView("http://localhost:3000/login?accessToken=" + tokens.get("accessToken"));
    }

    @GetMapping("/google")
    public RedirectView loginGoogle(@RequestParam("code") String code, HttpServletResponse response) {
        Map<String, String> tokens = authService.handleUserLogin(code, authService::getGoogleToken, authService::getGoogleUserInfo, authService::mapGoogleUserInfo);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS를 사용하는 경우
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(604800); // 1주일

        response.addCookie(refreshTokenCookie);

        return new RedirectView("/login?accessToken=" + tokens.get("accessToken"));
    }

    @GetMapping("/naver")
    public RedirectView loginNaver(@RequestParam("code") String code, HttpServletResponse response) {
        Map<String, String> tokens = authService.handleUserLogin(code, authService::getNaverToken, authService::getNaverUserInfo, authService::mapNaverUserInfo);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS를 사용하는 경우
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(604800); // 1주일

        response.addCookie(refreshTokenCookie);

        return new RedirectView("/login?accessToken=" + tokens.get("accessToken"));
    }
}
