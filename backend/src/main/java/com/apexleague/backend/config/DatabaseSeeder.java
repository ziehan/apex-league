package com.apexleague.backend.config;

import com.apexleague.backend.dto.MatchSubmitRequestDto;
import com.apexleague.backend.dto.UserRegistrationDto;
import com.apexleague.backend.dto.UserResponseDto;
import com.apexleague.backend.repository.UserRepository;
import com.apexleague.backend.service.MatchHistoryService;
import com.apexleague.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserService userService;
    private final MatchHistoryService matchHistoryService;
    private final UserRepository userRepository;

    public DatabaseSeeder(
            UserService userService,
            MatchHistoryService matchHistoryService,
            UserRepository userRepository
    ) {
        this.userService = userService;
        this.matchHistoryService = matchHistoryService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            logger.info("DatabaseSeeder: users found, skipping seeding.");
            return;
        }

        List<String> cars = Arrays.asList(
                "red_car",
                "blue_car",
                "green_car",
                "yellow_car",
                "pink_car",
                "purple_car",
                "white_car"
        );

        List<String> names = Arrays.asList(
                "Ziehan",
                "ProPlayer",
                "RocketMaster",
                "Speedy",
                "Dreamer",
                "Glitch",
                "NightOwl",
                "Cosmos",
                "Blaze",
                "Thunder",
                "Octane",
                "Fennec",
                "GoalMaster",
                "Gusion",
                "StarBlezer",
                "PizzaGuy",
                "Diddy",
                "Rose",
                "Black"
        );

        Random rnd = new Random();

        int usersToCreate = 150;

        Set<String> usedUsernames = new HashSet<>();

        for (int i = 1; i <= usersToCreate; i++) {
            String username = generateUsernameFromNames(names, rnd, usedUsernames);

            try {
                UserRegistrationDto reg = new UserRegistrationDto();
                reg.setUsername(username);
                reg.setPassword("password123");

                UserResponseDto created = userService.registerUser(reg);

                int matches = 5 + rnd.nextInt(11);

                for (int m = 0; m < matches; m++) {
                    int p1Goals = rnd.nextInt(8);
                    int p2Goals = rnd.nextInt(8);
                    int p1Saves = rnd.nextInt(5);

                    int p1Demos;
                    if (rnd.nextDouble() < 0.7) {
                        p1Demos = 1 + rnd.nextInt(5);
                    } else {
                        p1Demos = 0;
                    }

                    String p1Car = cars.get(rnd.nextInt(cars.size()));
                    String p2Car = cars.get(rnd.nextInt(cars.size()));

                    String result;
                    if (p1Goals > p2Goals) {
                        result = "P1_WIN";
                    } else if (p2Goals > p1Goals) {
                        result = "P2_WIN";
                    } else {
                        result = "DRAW";
                    }

                    MatchSubmitRequestDto dto = MatchSubmitRequestDto.builder()
                            .player1Id(created.getId())
                            .player1Name(username)
                            .p1Car(p1Car)
                            .p2Car(p2Car)
                            .p1Goals(p1Goals)
                            .p2Goals(p2Goals)
                            .p1Saves(p1Saves)
                            .p1Demos(p1Demos)
                            .matchResult(result)
                            .build();

                    matchHistoryService.saveMatch(dto);
                }

                if (i % 20 == 0) {
                    logger.info("Seeded {}/{} users...", i, usersToCreate);
                }

            } catch (Exception e) {
                logger.warn("DatabaseSeeder: failed user {}: {}", username, e.getMessage());
            }
        }

        logger.info("DatabaseSeeder: Injection complete!");
    }

    private String generateUsernameFromNames(
            List<String> names,
            Random rnd,
            Set<String> usedUsernames
    ) {
        String username;

        do {
            String baseName = names.get(rnd.nextInt(names.size()));
            int fourDigitNumber = rnd.nextInt(10000);

            username = baseName + String.format("%04d", fourDigitNumber);
        } while (usedUsernames.contains(username));

        usedUsernames.add(username);

        return username;
    }
}