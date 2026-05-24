package com.apexleague.backend.service;

import com.apexleague.backend.dto.CarSelectionDto;
import com.apexleague.backend.dto.UserRegistrationDto;
import com.apexleague.backend.dto.UserResponseDto;
import java.util.List;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationDto dto);
    UserResponseDto getUserByUsername(String username);
    List<UserResponseDto> getAllUsers();
    void deleteUserById(String userId);
    List<UserResponseDto> getTopPlayersByGoals();
    void updateUserCars(String username, CarSelectionDto dto);
}