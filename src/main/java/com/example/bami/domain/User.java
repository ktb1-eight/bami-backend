package com.example.bami.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) //createdAt 값 넣어주기
@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 id 생성
    private int id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt; //생성일시

    @Setter @Column(nullable = false) private String name;
    @Setter @Column(nullable = false) private String email;
    @Setter @Column(nullable = false) private String oauthProvider;
    @Setter @Column(nullable = false) private int oauthProviderId;
}
