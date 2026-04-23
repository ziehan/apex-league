package com.apexleague.backend.controller;

import com.apexleague.backend.model.UserCarStat;
import com.apexleague.backend.service.UserCarStatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car-stats")
public class UserCarStatController {

    private final UserCarStatService userCarStatService;

    public UserCarStatController(UserCarStatService userCarStatService) {
        this.userCarStatService = userCarStatService;
    }

    @PutMapping
    public ResponseEntity<UserCarStat> updateCarStats(@RequestBody UserCarStat stats) {
        return ResponseEntity.ok(userCarStatService.updateStats(stats));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserCarStat>> getStats(@PathVariable String userId) {
        return ResponseEntity.ok(userCarStatService.getStatsByUserId(userId));
    }
}
