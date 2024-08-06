package com.example.bami.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConfigController {

    @Value("${security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @GetMapping("/config")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("googleClientId", googleClientId);
        config.put("googleRedirectUri", googleRedirectUri);
        config.put("naverClientId", naverClientId);
        config.put("naverRedirectUri", naverRedirectUri);
        config.put("kakaoClientId", kakaoClientId);
        config.put("kakaoRedirectUri", kakaoRedirectUri);
        return config;
    }
}
