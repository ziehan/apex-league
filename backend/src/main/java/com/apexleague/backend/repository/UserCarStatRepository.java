package com.apexleague.backend.repository;

import com.apexleague.backend.model.UserCarStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCarStatRepository extends JpaRepository<UserCarStat, String> {
    List<UserCarStat> findByUserId(String userId);
    Optional<UserCarStat> findByUserIdAndCarModelId(String userId, String carModelId);
}