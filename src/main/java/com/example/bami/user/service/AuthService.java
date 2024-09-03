package com.example.bami.user.service;

import com.example.bami.user.domain.BamiUser;
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

    @Value("${security.oauth2.client.registration.google.client-secret}")
    private String googleClientPw;

    @Value("${security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    
    @Value("${security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${security.oauth2.client.provider.google.token-uri}")
    private String googleTokenUri;

    @Value("${security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUri;

    @Value("${security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    @Value("${security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

    private static final String HTTPS = "https";
    private static final String GRANT_TYPE = "grant_type";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_ERROR_TEXT = "Invalid Parameter";
    private static final String SERVER_ERROR_TEXT = "Internal Server Error";
    private static final String BEARER = "Bearer ";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String IMAGE = "image";

    public AuthService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    public TokenResponseDto getKakaoToken(String code) {
        return WebClient.create(kakaoTokenUri)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .path("/oauth/token")
                        .queryParam(GRANT_TYPE, AUTHORIZATION_CODE)
                        .queryParam(CLIENT_ID, kakaoClientId)
                        .queryParam("redirect_uri", kakaoRedirectUri)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException(CLIENT_ERROR_TEXT)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException(SERVER_ERROR_TEXT)))
                .bodyToMono(TokenResponseDto.class)
                .block();
    }

    public KakaoUserInfoResponseDto getKakaoUserInfo(String accessToken) {
        return WebClient.create(kakaoUserInfoUri)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException(CLIENT_ERROR_TEXT)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException(SERVER_ERROR_TEXT)))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }

    public TokenResponseDto getGoogleToken(String code) {
        return WebClient.create(googleTokenUri)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .path("/token")
                        .queryParam(GRANT_TYPE, AUTHORIZATION_CODE)
                        .queryParam(CLIENT_ID, googleClientId)
                        .queryParam("client_secret", googleClientPw)
                        .queryParam("redirect_uri", googleRedirectUri)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException(CLIENT_ERROR_TEXT)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException(SERVER_ERROR_TEXT)))
                .bodyToMono(TokenResponseDto.class)
                .block();
    }

    public GoogleUserInfoResponseDto getGoogleUserInfo(String accessToken) {
        return WebClient.create(googleUserInfoUri)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .path("/userinfo/v2/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException(CLIENT_ERROR_TEXT)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException(SERVER_ERROR_TEXT)))
                .bodyToMono(GoogleUserInfoResponseDto.class)
                .block();
    }

    public TokenResponseDto getNaverToken(String code) {
        return WebClient.create(naverTokenUri)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .path("/oauth2.0/token")
                        .queryParam(GRANT_TYPE, AUTHORIZATION_CODE)
                        .queryParam(CLIENT_ID, naverClientId)
                        .queryParam("client_secret", naverClientSecret)
                        .queryParam("code", code)
                        .queryParam("state", "STATE_STRING")
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException(CLIENT_ERROR_TEXT)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException(SERVER_ERROR_TEXT)))
                .bodyToMono(TokenResponseDto.class)
                .block();
    }

    public NaverUserInfoResponseDto getNaverUserInfo(String accessToken) {
        return WebClient.create(naverUserInfoUri)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .path("/v1/nid/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException(CLIENT_ERROR_TEXT)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException(SERVER_ERROR_TEXT)))
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
            throw new IllegalArgumentException("Failed to retrieve user information from provider");
        }

        Map<String, String> responseMap = responseMapFunction.apply(userInfo);

        responseMap = new HashMap<>(responseMap);

        log.info("[ OAuth2 Service ] Name ---> {} ", responseMap.get(NAME));
        log.info("[ OAuth2 Service ] Image ---> {} ", responseMap.get(IMAGE));
        log.info("[ OAuth2 Service ] Email ---> {} ", responseMap.get(EMAIL));


        //Create JWT tokens
        String jwtAccessToken = jwtTokenProvider.generateToken(responseMap, 3600000); // 1 hour expiration
        String jwtRefreshToken = jwtTokenProvider.generateToken(responseMap, 604800000); // 1 week expiration

        // 사용자 정보를 DB에 저장하거나 업데이트
        BamiUser bamiUser = userRepository.findByEmail(responseMap.get(EMAIL));
        if (bamiUser == null) {
            bamiUser = new BamiUser();
            bamiUser.setEmail(responseMap.get(EMAIL));
            bamiUser.setName(responseMap.get(NAME));
            bamiUser.setOauthProvider(provider);
        }
        bamiUser.setProfileImageUrl(responseMap.get(IMAGE));
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
        userInfoMap.put(IMAGE, userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
        userInfoMap.put(NAME, userInfo.getKakaoAccount().getProfile().getNickname());
        userInfoMap.put(EMAIL, userInfo.getKakaoAccount().getEmail());

        return userInfoMap;
    }

    public Map<String, String> mapGoogleUserInfo(GoogleUserInfoResponseDto userInfo) {
        if (userInfo.getPicture() == null || userInfo.getName() == null) {
            throw new IllegalArgumentException("Failed to retrieve user information from Google");
        }
        Map<String, String> userInfoMap = new HashMap<>();
        userInfoMap.put(IMAGE, userInfo.getPicture());
        userInfoMap.put(NAME, userInfo.getName());
        userInfoMap.put(EMAIL, userInfo.getEmail());
        return userInfoMap;
    }

    public Map<String, String> mapNaverUserInfo(NaverUserInfoResponseDto userInfo) {
        if (userInfo.getResponse().getProfileImage() == null || userInfo.getResponse().getNickname() == null) {
            throw new IllegalArgumentException("Failed to retrieve user information from Naver");
        }
        Map<String, String> userInfoMap = new HashMap<>();
        userInfoMap.put(IMAGE, userInfo.getResponse().getProfileImage());
        userInfoMap.put(NAME, userInfo.getResponse().getName());
        userInfoMap.put(EMAIL, userInfo.getResponse().getEmail());
        return userInfoMap;
    }
}
