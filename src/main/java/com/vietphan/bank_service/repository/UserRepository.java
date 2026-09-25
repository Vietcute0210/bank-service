package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.User;
import com.vietphan.bank_service.entity.UserLevel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"account"})
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByLevel(UserLevel level);
}
