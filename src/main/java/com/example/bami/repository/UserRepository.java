package com.example.bami.repository;

import com.example.bami.domain.BamiUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//jpa
public interface UserRepository extends JpaRepository<BamiUser, Integer> {
    BamiUser findByEmail(String email); // null 포함 가능
}
