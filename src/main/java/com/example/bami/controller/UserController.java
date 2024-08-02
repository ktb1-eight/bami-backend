package com.example.bami.controller;

import com.example.bami.domain.BamiUser;
import com.example.bami.repository.UserRepository;
import com.example.bami.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;

    public UserController(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, String>> getUserInfo(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if(token != null && jwtTokenProvider.validateToken(token)) {
            Map<String, String> userInfo = jwtTokenProvider.getClaimsAsMap(token);
            String email = userInfo.get("email");
            BamiUser bamiUser = userRepository.findByEmail(email);

            if(bamiUser != null) {
                return ResponseEntity.ok(Map.of(
                    "name", bamiUser.getName(),
                    "email", bamiUser.getEmail(),
                    "image", bamiUser.getProfileImageUrl()
                ));
            }
        }
        return ResponseEntity.status(401).body(null);
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

    @PostMapping("/update-user-info")
    public ResponseEntity<String> updateUserInfo(@RequestBody Map<String, String> userInfo, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if(token != null && jwtTokenProvider.validateToken(token)) {
            Map<String, String> claims = jwtTokenProvider.getClaimsAsMap(token);
            String email = claims.get("email");
            BamiUser bamiUser = userRepository.findByEmail(email);

            if (bamiUser != null) {
                bamiUser.setName(userInfo.get("name"));
                userRepository.save(bamiUser);
                return ResponseEntity.ok().body("User info updated successfully");
            }
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

    @PostMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if(token != null && jwtTokenProvider.validateToken(token)) {
            Map<String, String> claims = jwtTokenProvider.getClaimsAsMap(token);
            String email = claims.get("email");
            BamiUser bamiUser = userRepository.findByEmail(email);

            if (bamiUser != null) {
                userRepository.delete(bamiUser);
                return ResponseEntity.ok().body("Account deleted successfully");
            }
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }
}
