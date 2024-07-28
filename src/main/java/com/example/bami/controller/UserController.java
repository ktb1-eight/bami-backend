package com.example.bami.controller;

import com.example.bami.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private JwtTokenProvider jwtTokenProvider;

    public UserController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, String>> getUserInfo(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if(token != null && jwtTokenProvider.validateToken(token)) {
            Map<String, String> userInfo = jwtTokenProvider.getClaimsAsMap(token);
            return ResponseEntity.ok(userInfo);
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request) {
        String refreshToken = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Map<String, String> claims = jwtTokenProvider.getClaimsAsMap(refreshToken);
            String newAccessToken = jwtTokenProvider.generateToken(claims, 3600000); // 1 hour expiration
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }
}
