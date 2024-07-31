package com.example.bami.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

@Getter
@NoArgsConstructor // 역직렬화를 위한 기본 생성자
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // 회원 번호
    @JsonProperty("id")
    private Long id;

    // 카카오 계정 정보
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount implements Serializable {

        private static final long serialVersionUID = 1L;

        // 사용자 프로필 정보
        @JsonProperty("profile")
        private Profile profile;

        @JsonProperty("email")
        private String email;

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Profile implements Serializable {

            private static final long serialVersionUID = 1L;

            // 닉네임
            @JsonProperty("nickname")
            private String nickname;

            // 프로필 사진 URL
            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }
}
