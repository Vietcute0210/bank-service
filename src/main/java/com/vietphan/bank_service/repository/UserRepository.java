package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.User;
import com.vietphan.bank_service.entity.UserLevel;
import com.vietphan.bank_service.enums.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"account", "level"})
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"account", "level"})
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"account", "level"})
    List<User> findAll();

    @EntityGraph(attributePaths = {"account", "level"})
    List<User> findByLevel_LevelName(String levelName);

    @EntityGraph(attributePaths = {"account", "level"})
    List<User> findByRole(Role role);

    @EntityGraph(attributePaths = {"account", "level"})
    List<User> findByLevel_LevelNameAndRole(String levelName, Role role);

    boolean existsByUsername(String username);
    boolean existsByLevel(UserLevel level);
}
