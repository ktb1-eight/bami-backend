package com.example.bami.city.domain;

import jakarta.persistence.*;
import lombok.Data;
import com.example.bami.user.domain.BamiUser;

import java.util.Date;

@Entity
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // BamiUser의 id를 참조하는 외래 키
    private BamiUser user;

    private String location;
    private Date startDate;
    private Date endDate;
    private boolean visited;
}
