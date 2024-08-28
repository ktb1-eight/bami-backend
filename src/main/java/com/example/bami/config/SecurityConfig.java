package com.example.bami.config;

import com.example.bami.user.security.JwtAuthenticationFilter;
import com.example.bami.user.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Spring Security를 사용하여 애플리케이션의 보안을 구성하는 Java 클래스
@Configuration // 스프링 설정 클래스
@EnableWebSecurity // 스프링 시큐리티 설정을 활성화
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;

    public SecurityConfig(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // HTTP 보안 설정을 구성
    @Bean // 이 메소드를 빈으로 등록하여 스프링 컨텍스트가 이를 인식하고, 보안 필터 체인을 구성할 수 있게 합니다.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers(HttpMethod.GET, "/api/config").permitAll() // /api/config 경로에 대한 GET 요청 허용
                    .requestMatchers("/api/**").permitAll() // /api/** 경로는 누구나 접근할 수 있도록 허용
                    .requestMatchers("/").permitAll()
                    .anyRequest().authenticated() // 그 외의 모든 요청은 인증된 사용자만 접근
            )
                //JWT 인증 필터가 모든 요청에 대해 적용됩니다. 이는 애플리케이션의 모든 요청이 JwtAuthenticationFilter를 통해 JWT 토큰의 유효성을 검증받도록 보장합니다.
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // UsernamePasswordAuthenticationFilter 필터 앞에 jwtAuthenticationFilter를 추가
        return http.build();
    }

    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    } // JWT 토큰을 검증하는 필터
}
