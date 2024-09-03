package com.example.bami.user.domain;

import com.example.bami.city.domain.Schedule;
import com.example.bami.short_travel.entity.TravelPlanEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class) //createdAt 값 넣어주기
@Getter
@Entity
public class BamiUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 id 생성
    private int id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt; //생성일시

    @Setter @Column(nullable = false) private String name;
    @Setter @Column(nullable = false) private String email;
    @Setter @Column(nullable = false) private String profileImageUrl;
    @Setter @Column(nullable = false) private String oauthProvider;
    @Setter @Column() private String upcomingScheduleId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TravelPlanEntity> travelPlans = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    public void addTravelPlan(TravelPlanEntity travelPlan) {
        travelPlans.add(travelPlan);
        travelPlan.setUser(this);
    }
}
