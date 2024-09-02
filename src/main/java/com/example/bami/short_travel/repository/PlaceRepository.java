package com.example.bami.short_travel.repository;

import com.example.bami.short_travel.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> { }