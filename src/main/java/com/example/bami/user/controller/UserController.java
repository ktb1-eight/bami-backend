package com.example.bami.user.controller;

import com.example.bami.short_travel.dto.TravelPlanDTO;
import com.example.bami.short_travel.service.TravelPlanService;
import com.example.bami.user.domain.BamiUser;
import com.example.bami.user.repository.UserRepository;
import com.example.bami.user.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "사용자 관리", description = "사용자 정보 조회, 수정, 삭제 등의 작업을 수행합니다.")
public class UserController {

    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;

    private TravelPlanService travelPlanService;

    public UserController(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, TravelPlanService travelPlanService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.travelPlanService = travelPlanService;
    }

    private static final String EMAIL_KEY = "email";

    @Operation(summary = "사용자 정보 조회", description = "JWT 토큰을 통해 인증된 사용자의 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 반환 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/retrieve-info")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @Parameter(description = "HTTP 요청", required = true) HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Map<String, String> userInfo = jwtTokenProvider.getClaimsAsMap(token);
            String email = userInfo.get(EMAIL_KEY);
            BamiUser bamiUser = userRepository.findByEmail(email);

            if (bamiUser != null) {
                List<Map<String, Object>> destinations = bamiUser.getSchedules().stream()
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
                        EMAIL_KEY, bamiUser.getEmail(),
                        "image", bamiUser.getProfileImageUrl(),
                        "travelDestinations", destinations
                ));
            }
        }
        return ResponseEntity.status(401).body(null);
    }

    @Operation(summary = "새로운 액세스 토큰 발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "새로운 액세스 토큰 발급 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰이 유효하지 않음", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @Parameter(description = "HTTP 요청", required = true) HttpServletRequest request) {
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

    @Operation(summary = "사용자 정보 업데이트", description = "JWT 토큰을 통해 인증된 사용자의 정보를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 업데이트 성공", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/update-info")
    public ResponseEntity<String> updateUserInfo(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "업데이트할 사용자 정보", required = true) Map<String, String> userInfo,
            @Parameter(description = "HTTP 요청", required = true) HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Map<String, String> claims = jwtTokenProvider.getClaimsAsMap(token);
            String email = claims.get(EMAIL_KEY);
            BamiUser bamiUser = userRepository.findByEmail(email);

            if (bamiUser != null) {
                bamiUser.setName(userInfo.get("name"));
                userRepository.save(bamiUser);
                return ResponseEntity.ok().body("User info updated successfully");
            }
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

    @Operation(summary = "계정 삭제", description = "JWT 토큰을 통해 인증된 사용자의 계정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 삭제 성공", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/delete-info")
    public ResponseEntity<String> deleteAccount(
            @Parameter(description = "HTTP 요청", required = true) HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Map<String, String> claims = jwtTokenProvider.getClaimsAsMap(token);
            String email = claims.get(EMAIL_KEY);
            BamiUser bamiUser = userRepository.findByEmail(email);

            if (bamiUser != null) {
                userRepository.delete(bamiUser);
                return ResponseEntity.ok().body("Account deleted successfully");
            }
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

    @GetMapping("/travel-plans")
    public ResponseEntity<?> getUserTravelPlans(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        String email = jwtTokenProvider.getClaimsAsMap(token).get("email");
        BamiUser bamiUser = userRepository.findByEmail(email);
        if (bamiUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
        }

        int userId = bamiUser.getId();

        if (userId == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            List<TravelPlanDTO> travelPlans = travelPlanService.getTravelPlansForUser(userId);
            return ResponseEntity.ok(travelPlans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("여행 계획 조회 중 오류 발생: " + e.getMessage());
        }
    }
}