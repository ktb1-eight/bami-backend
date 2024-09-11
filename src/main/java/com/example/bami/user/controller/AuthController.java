package com.example.bami.user.controller;

import com.example.bami.user.dto.TokenResponseDto;
import com.example.bami.user.service.AuthService;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/api/auth/login")
@Tag(name = "로그인", description = "OAuth2 소셜 로그인 API")
public class AuthController {

    private AuthService authService;
    private static final String REFRESH_TOKEN = "refreshToken";

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Value("${app.dns-addr}")
    private String endPoint;

    @Operation(summary = "카카오 로그인", description = "카카오 OAuth2 제공자를 이용한 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "액세스 토큰을 포함한 프론트엔드로 리디렉션",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/kakao")
    public RedirectView loginKakao(
            @Parameter(description = "OAuth2 인증 코드",
                    examples = @ExampleObject(value = "abc123"),
                    required = true) @RequestParam("code") String code,
            HttpServletResponse response) {
        return loginOAuth(code, response, authService::getKakaoToken, authService::getKakaoUserInfo, authService::mapKakaoUserInfo, "kakao");
    }

    @Operation(summary = "구글 로그인", description = "구글 OAuth2 제공자를 이용한 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "액세스 토큰을 포함한 프론트엔드로 리디렉션",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/google")
    public RedirectView loginGoogle(
            @Parameter(description = "OAuth2 인증 코드",
                    examples = @ExampleObject(value = "def456"),
                    required = true) @RequestParam("code") String code,
            HttpServletResponse response) {
        return loginOAuth(code, response, authService::getGoogleToken, authService::getGoogleUserInfo, authService::mapGoogleUserInfo, "google");
    }

    @Operation(summary = "네이버 로그인", description = "네이버 OAuth2 제공자를 이용한 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "액세스 토큰을 포함한 프론트엔드로 리디렉션",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/naver")
    public RedirectView loginNaver(
            @Parameter(description = "OAuth2 인증 코드",
                    examples = @ExampleObject(value = "ghi789"),
                    required = true) @RequestParam("code") String code,
            HttpServletResponse response) {
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

        return new RedirectView("http://" + endPoint + ":3000/login?accessToken=" + tokens.get("accessToken"));
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
