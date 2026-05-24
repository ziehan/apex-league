package com.apexleague.backend.service.implementation;

import com.apexleague.backend.model.UserCarStat;
import com.apexleague.backend.repository.UserCarStatRepository;
import com.apexleague.backend.service.UserCarStatService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserCarStatServiceImpl implements UserCarStatService {

    private final UserCarStatRepository userCarStatRepository;

    public UserCarStatServiceImpl(UserCarStatRepository userCarStatRepository) {
        this.userCarStatRepository = userCarStatRepository;
    }

    @Override
    public UserCarStat updateStats(UserCarStat stats) {
        return userCarStatRepository.save(stats);
    }

    @Override
    public List<UserCarStat> getStatsByUserId(String userId) {
        UUID parsedId = UUID.fromString(userId);
        return userCarStatRepository.findByUserId(parsedId);
    }
}
