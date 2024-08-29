package com.example.bami.short_travel.repository;

import com.example.bami.short_travel.entity.TravelPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TravelPlanRepository extends JpaRepository<TravelPlanEntity, Long> {
}
