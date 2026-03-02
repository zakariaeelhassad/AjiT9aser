package com.example.backend.repository;

import com.example.backend.model.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {

    Optional<UserTeam> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
