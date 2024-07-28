package com.example.bami;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class) // 기본 로그인 화면 안보이게
public class BamiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BamiApplication.class, args);
	}

}
