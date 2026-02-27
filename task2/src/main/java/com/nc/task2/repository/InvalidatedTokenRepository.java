package com.nc.task2.repository;

import com.nc.task2.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Long> {
    Optional<InvalidatedToken> findByToken(String token);
}