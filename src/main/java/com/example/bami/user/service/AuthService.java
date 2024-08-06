package com.example.bami.user.service;

import com.example.bami.user.domain.BamiUser;
import com.example.bami.dto.*;
import com.example.bami.user.dto.GoogleUserInfoResponseDto;
import com.example.bami.user.dto.KakaoUserInfoResponseDto;
import com.example.bami.user.dto.NaverUserInfoResponseDto;
import com.example.bami.user.dto.TokenResponseDto;
import com.example.bami.user.repository.UserRepository;
import com.example.bami.user.security.JwtTokenProvider;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${security.oauth2.client.registration.google.client-pw}")
    private String googleClientPw;

    @Value("${security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

//    @Value("${security.oauth2.client.registration.naver.redirect-uri}")
//    private String naverRedirectUri;

    private final static String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final static String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    private final static String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com";
    private final static String GOOGLE_USER_URL = "https://www.googleapis.com";
    private final static String NAVER_TOKEN_URL = "https://nid.naver.com";
    private final static String NAVER_USER_URL = "https://openapi.naver.com";

    public AuthService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    public TokenResponseDto getKakaoToken(String code) {
        return WebClient.create(KAUTH_TOKEN_URL_HOST)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoClientId)
                        .queryParam("redirect_uri", kakaoRedirectUri)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(TokenResponseDto.class)
                .block();
    }

    public KakaoUserInfoResponseDto getKakaoUserInfo(String accessToken) {
        return WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }

    public TokenResponseDto getGoogleToken(String code) {
        return WebClient.create(GOOGLE_TOKEN_URL)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", googleClientId)
                        .queryParam("client_secret", googleClientPw)
                        .queryParam("redirect_uri", googleRedirectUri)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(TokenResponseDto.class)
                .block();
    }

    public GoogleUserInfoResponseDto getGoogleUserInfo(String accessToken) {
        return WebClient.create(GOOGLE_USER_URL)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/userinfo/v2/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(GoogleUserInfoResponseDto.class)
                .block();
    }

    public TokenResponseDto getNaverToken(String code) {
        return WebClient.create(NAVER_TOKEN_URL)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth2.0/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", naverClientId)
                        .queryParam("client_secret", naverClientSecret)
                        .queryParam("code", code)
                        .queryParam("state", "STATE_STRING")
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(TokenResponseDto.class)
                .block();
    }

    public NaverUserInfoResponseDto getNaverUserInfo(String accessToken) {
        return WebClient.create(NAVER_USER_URL)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v1/nid/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(NaverUserInfoResponseDto.class)
                .block();
    }

    public <T> Map<String, String> handleUserLogin(String code,
                                      Function<String, TokenResponseDto> tokenFunction,
                                      Function<String, T> userInfoFunction,
                                      Function<T, Map<String, String>> responseMapFunction,
                                      String provider) {
        TokenResponseDto tokenResponse = tokenFunction.apply(code);
        String accessToken = tokenResponse.getAccessToken();
        T userInfo = userInfoFunction.apply(accessToken);

        if (userInfo == null) {
            throw new RuntimeException("Failed to retrieve user information from provider");
        }

        Map<String, String> responseMap = responseMapFunction.apply(userInfo);

        responseMap = new HashMap<>(responseMap);

        log.info("[ OAuth2 Service ] Name ---> {} ", responseMap.get("name"));
        log.info("[ OAuth2 Service ] Image ---> {} ", responseMap.get("image"));
        log.info("[ OAuth2 Service ] Email ---> {} ", responseMap.get("email"));


        //Create JWT tokens
        String jwtAccessToken = jwtTokenProvider.generateToken(responseMap, 3600000); // 1 hour expiration
        String jwtRefreshToken = jwtTokenProvider.generateToken(responseMap, 604800000); // 1 week expiration

        // 사용자 정보를 DB에 저장하거나 업데이트
        BamiUser bamiUser = userRepository.findByEmail(responseMap.get("email"));
        if (bamiUser == null) {
            bamiUser = new BamiUser();
            bamiUser.setEmail(responseMap.get("email"));
            bamiUser.setName(responseMap.get("name"));
            bamiUser.setOauthProvider(provider);
        }
        bamiUser.setProfileImageUrl(responseMap.get("image"));
        userRepository.save(bamiUser);

        responseMap.put("accessToken", jwtAccessToken);
        responseMap.put("refreshToken", jwtRefreshToken);

        return responseMap;
    }

    public Map<String, String> mapKakaoUserInfo(KakaoUserInfoResponseDto userInfo) {
        if (userInfo.getKakaoAccount() == null || userInfo.getKakaoAccount().getProfile() == null) {
            throw new IllegalArgumentException("Failed to retrieve user information from Kakao");
        }
        Map<String, String> userInfoMap = new HashMap<>();
        userInfoMap.put("image", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
        userInfoMap.put("name", userInfo.getKakaoAccount().getProfile().getNickname());
        userInfoMap.put("email", userInfo.getKakaoAccount().getEmail());

        return userInfoMap;
    }

    public Map<String, String> mapGoogleUserInfo(GoogleUserInfoResponseDto userInfo) {
        if (userInfo.getPicture() == null || userInfo.getName() == null) {
            throw new IllegalArgumentException("Failed to retrieve user information from Google");
        }
        Map<String, String> userInfoMap = new HashMap<>();
        userInfoMap.put("image", userInfo.getPicture());
        userInfoMap.put("name", userInfo.getName());
        userInfoMap.put("email", userInfo.getEmail());
        return userInfoMap;
    }

    public Map<String, String> mapNaverUserInfo(NaverUserInfoResponseDto userInfo) {
        if (userInfo.getResponse().getProfile_image() == null || userInfo.getResponse().getNickname() == null) {
            throw new IllegalArgumentException("Failed to retrieve user information from Naver");
        }
        Map<String, String> userInfoMap = new HashMap<>();
        userInfoMap.put("image", userInfo.getResponse().getProfile_image());
        userInfoMap.put("name", userInfo.getResponse().getName());
        userInfoMap.put("email", userInfo.getResponse().getEmail());
        return userInfoMap;
    }
}
