package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = {"account"})
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
