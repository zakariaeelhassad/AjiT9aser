package org.example.ajit9aser.repository;

import org.example.ajit9aser.models.entity.User;
import org.example.ajit9aser.models.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndActiveTrue(String login);

    Page<User> findByRole(Role role, Pageable pageable);

    Page<User> findByRoleAndSpecialite(Role role, Pageable pageable);
}