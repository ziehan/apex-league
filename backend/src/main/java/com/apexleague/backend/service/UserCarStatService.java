package com.apexleague.backend.service;

import java.util.List;
import com.apexleague.backend.model.UserCarStat;

public interface UserCarStatService {
    UserCarStat updateStats(UserCarStat stats);
    List<UserCarStat> getStatsByUserId(String userId);
}
