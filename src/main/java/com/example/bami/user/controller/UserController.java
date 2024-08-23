package com.example.bami.user.controller;

import com.example.bami.user.domain.BamiUser;
import com.example.bami.user.repository.UserRepository;
import com.example.bami.user.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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
    public ResponseEntity<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Map<String, String> userInfo = jwtTokenProvider.getClaimsAsMap(token);
            String email = userInfo.get("email");
            BamiUser bamiUser = userRepository.findByEmail(email);

            if (bamiUser != null) {
                // 여행지 리스트를 함께 반환
                List<Map<String, Object>> destinations = bamiUser.getTravelDestinations().stream()
                        .map(destination -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", destination.getId());
                            map.put("location", destination.getLocation());
                            map.put("startDate", destination.getStartDate());
                            map.put("endDate", destination.getEndDate());
                            map.put("visited", destination.isVisited());
                            return map;
                        }).toList();

                return ResponseEntity.ok(Map.of(
                        "id", String.valueOf(bamiUser.getId()),
                        "name", bamiUser.getName(),
                        "email", bamiUser.getEmail(),
                        "image", bamiUser.getProfileImageUrl(),
                        "travelDestinations", destinations  // 여행지 리스트 포함
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
