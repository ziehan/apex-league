package com.apexleague.backend.repository;

import com.apexleague.backend.model.UserCarStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCarStatRepository extends JpaRepository<UserCarStat, UUID> {
    List<UserCarStat> findByUserId(UUID userId);
    Optional<UserCarStat> findByUserIdAndCarModelId(UUID userId, String carModelId);
}