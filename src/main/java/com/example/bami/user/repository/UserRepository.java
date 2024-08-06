package com.example.bami.user.repository;

import com.example.bami.user.domain.BamiUser;
import org.springframework.data.jpa.repository.JpaRepository;

//jpa
public interface UserRepository extends JpaRepository<BamiUser, Integer> {
    BamiUser findByEmail(String email); // null 포함 가능
}
