package com.example.bami.controller;

import com.example.bami.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private WebClient webClient;

    // TODO : login controller 구현   https://chatgpt.com/c/622a0d3a-9350-4e01-bfb0-f7adb5210226
    public ResponseEntity<?> login(@RequestParam String provider, @RequestParam String accessToken) {

        return (ResponseEntity<?>) ResponseEntity.ok();
    }
}
