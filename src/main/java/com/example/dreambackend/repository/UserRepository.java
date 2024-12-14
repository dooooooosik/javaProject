package com.example.dreambackend.repository;

import com.example.dreambackend.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByEmail(String email);

    boolean existsByEmail(String email);
}
