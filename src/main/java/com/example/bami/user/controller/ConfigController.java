package com.example.bami.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "설정 관리", description = "OAuth2 클라이언트 설정 정보를 제공합니다.")
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

    @Operation(summary = "OAuth2 클라이언트 설정 조회", description = "Google, Naver, Kakao의 OAuth2 클라이언트 설정 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "설정 정보 반환 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(hidden = true)))
    })
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