package com.apexleague.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
        name = "user_car_stats",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "car_model_id"})
        }
)
public class UserCarStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "car_model_id", nullable = false)
    private String carModelId;

    @Column(name = "goals_scored")
    private Integer goalsScored = 0;

    @Column(name = "matches_played")
    private Integer matchesPlayed = 0;

    @UpdateTimestamp
    @Column(name = "last_used")
    private LocalDateTime lastUsed;

}
