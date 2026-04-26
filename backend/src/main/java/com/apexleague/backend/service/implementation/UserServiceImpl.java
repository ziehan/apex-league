package com.apexleague.backend.service.implementation;

import com.apexleague.backend.dto.UserRegistrationDto;
import com.apexleague.backend.dto.UserResponseDto;
import com.apexleague.backend.model.User;
import com.apexleague.backend.repository.UserRepository;
import com.apexleague.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto registerUser(UserRegistrationDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username sudah digunakan");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        return mapToDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User tidak ditemukan");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserResponseDto> getTopPlayersByGoals() {
        return userRepository.findTop10ByOrderByTotalGoalsDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private UserResponseDto mapToDto(User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setUsername(user.getUsername());
        responseDto.setCreatedAt(user.getCreatedAt());
        responseDto.setTotalMatchPlayed(user.getTotalMatchPlayed());
        responseDto.setTotalWins(user.getTotalWins());
        responseDto.setTotalGoals(user.getTotalGoals());
        responseDto.setTotalBackwardGoals(user.getTotalBackwardGoals());
        responseDto.setTotalAssists(user.getTotalAssists());
        responseDto.setTotalSaves(user.getTotalSaves());
        responseDto.setTotalDemolitions(user.getTotalDemolitions());
        responseDto.setTotalHatTricks(user.getTotalHatTricks());

        if (user.getTotalMatchPlayed() > 0) {
            double goalsPerMatch = (double) user.getTotalGoals() / user.getTotalMatchPlayed();
            double assistsPerMatch = (double) user.getTotalAssists() / user.getTotalMatchPlayed();
            double savesPerMatch = (double) user.getTotalSaves() / user.getTotalMatchPlayed();

            responseDto.setGoalPerMatch(roundToTwoDecimals(goalsPerMatch));
            responseDto.setAssistPerMatch(roundToTwoDecimals(assistsPerMatch));
            responseDto.setSavesPerMatch(roundToTwoDecimals(savesPerMatch));
        } else {
            responseDto.setGoalPerMatch(0.0);
            responseDto.setAssistPerMatch(0.0);
            responseDto.setSavesPerMatch(0.0);
        }

        return responseDto;
    }

    private double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}