package com.example.bami.repository;

import com.example.bami.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//jpa
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email); // null 포함 가능
}
