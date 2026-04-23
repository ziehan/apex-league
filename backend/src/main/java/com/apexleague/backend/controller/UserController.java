package com.apexleague.backend.controller;

import com.apexleague.backend.dto.UserRegistrationDto;
import com.apexleague.backend.dto.UserResponseDto;
import com.apexleague.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

    @DeleteMapping("/id/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}