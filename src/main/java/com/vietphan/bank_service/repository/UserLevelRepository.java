package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {

    boolean existsByLevelName(String levelName);

    Optional<UserLevel> findByLevelName(String levelName);

    boolean existsByLevelNameAndLevelIdNot(String levelName, Long levelId);
}
