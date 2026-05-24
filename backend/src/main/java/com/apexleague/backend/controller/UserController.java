package com.apexleague.backend.controller;

import com.apexleague.backend.dto.UserFullStatsDto;
import com.apexleague.backend.dto.UserRegistrationDto;
import com.apexleague.backend.dto.UserResponseDto;
import com.apexleague.backend.dto.CarSelectionDto;
import com.apexleague.backend.model.MatchHistory;
import com.apexleague.backend.service.UserService;
import com.apexleague.backend.service.MatchHistoryService;
import com.apexleague.backend.repository.UserRepository;
import com.apexleague.backend.repository.UserCarStatRepository;
import com.apexleague.backend.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final MatchHistoryService matchHistoryService;
    private final UserRepository userRepository;
    private final UserCarStatRepository userCarStatRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
                          MatchHistoryService matchHistoryService,
                          UserRepository userRepository,
                          UserCarStatRepository userCarStatRepository,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.matchHistoryService = matchHistoryService;
        this.userRepository = userRepository;
        this.userCarStatRepository = userCarStatRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
        return new ResponseEntity<>(userService.registerUser(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/scoreboard")
    public ResponseEntity<List<UserResponseDto>> getScoreboard() {
        return ResponseEntity.ok(userService.getTopPlayersByGoals());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        userService.deleteUserById(id);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "User berhasil dihapus");
        response.put("userId", id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable String id) {
        try {
            java.util.UUID parsed = java.util.UUID.fromString(id);
            User user = userRepository.findById(parsed).orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            UserResponseDto userDto = new UserResponseDto();
            userDto.setId(user.getId().toString());
            userDto.setUsername(user.getUsername());
            userDto.setCreatedAt(user.getCreatedAt());
            userDto.setTotalMatchPlayed(user.getTotalMatchPlayed());
            userDto.setTotalWins(user.getTotalWins());
            userDto.setTotalGoals(user.getTotalGoals());
            userDto.setTotalBackwardGoals(user.getTotalBackwardGoals());
            userDto.setTotalAssists(user.getTotalAssists());
            userDto.setTotalSaves(user.getTotalSaves());
            userDto.setTotalDemolitions(user.getTotalDemolitions());
            userDto.setTotalHatTricks(user.getTotalHatTricks());

            if (user.getTotalMatchPlayed() > 0) {
                userDto.setGoalPerMatch(Math.round(((double) user.getTotalGoals() / user.getTotalMatchPlayed()) * 100.0) / 100.0);
                userDto.setAssistPerMatch(Math.round(((double) user.getTotalAssists() / user.getTotalMatchPlayed()) * 100.0) / 100.0);
                userDto.setSavesPerMatch(Math.round(((double) user.getTotalSaves() / user.getTotalMatchPlayed()) * 100.0) / 100.0);
            } else {
                userDto.setGoalPerMatch(0.0);
                userDto.setAssistPerMatch(0.0);
                userDto.setSavesPerMatch(0.0);
            }

            java.util.Map<String, Object> response = new java.util.LinkedHashMap<>();
            response.put("user", userDto);
            java.util.List<MatchHistory> history = matchHistoryService.getPlayerMatchHistory(id);
            response.put("matchHistory", history);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid UUID"));
        }
    }

    @GetMapping("/{username}/full")
    public ResponseEntity<UserFullStatsDto> getUserFullStats(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        UserFullStatsDto response = UserFullStatsDto.builder()
                .user(user)
                .matchHistory(matchHistoryService.getPlayerMatchHistory(user.getId().toString()))
                .carStats(userCarStatRepository.findByUserId(user.getId()))
                .lastUsedP1Car(user.getLastUsedP1Car())
                .lastUsedP2Car(user.getLastUsedP2Car())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserRegistrationDto loginDto) {
        return userRepository.findByUsername(loginDto.getUsername())
                .filter(user -> passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash()))
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(userService.getUserByUsername(user.getUsername())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
    }

    @PutMapping("/{username}/cars")
    public ResponseEntity<java.util.Map<String, String>> updateUserCars(@PathVariable String username,
                                                                        @RequestBody CarSelectionDto dto) {
        userService.updateUserCars(username, dto);
        return ResponseEntity.ok(java.util.Map.of(
                "status", "success",
                "username", username,
                "p1Car", dto.getP1Car(),
                "p2Car", dto.getP2Car()
        ));
    }
}