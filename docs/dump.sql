-- ============================================================
-- Apex League Database Dump
-- Database: PostgreSQL
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DROP TABLE IF EXISTS user_car_stats CASCADE;
DROP TABLE IF EXISTS match_history CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ============================================================
-- Table: users
-- ============================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    mmr INTEGER DEFAULT 0,
    total_match_played INTEGER DEFAULT 0,
    total_wins INTEGER DEFAULT 0,
    total_goals INTEGER DEFAULT 0,
    total_backward_goals INTEGER DEFAULT 0,
    total_saves INTEGER DEFAULT 0,
    total_demolitions INTEGER DEFAULT 0,
    total_hat_tricks INTEGER DEFAULT 0,

    last_used_p1_car VARCHAR(255) DEFAULT 'red_car',
    last_used_p2_car VARCHAR(255) DEFAULT 'blue_car',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Table: match_history
-- ============================================================

CREATE TABLE match_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    player1_id UUID NOT NULL,
    player1_name VARCHAR(255),
    player2_name VARCHAR(255) DEFAULT 'Guest',

    player1_score INTEGER DEFAULT 0,
    player2_score INTEGER DEFAULT 0,

    match_result VARCHAR(255) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_match_history_player1
        FOREIGN KEY (player1_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Table: user_car_stats
-- ============================================================

CREATE TABLE user_car_stats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,
    car_model_id VARCHAR(255) NOT NULL,

    wins INTEGER DEFAULT 0,
    goals_scored INTEGER DEFAULT 0,
    matches_played INTEGER DEFAULT 0,

    last_used TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_car_stats_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX idx_users_username
ON users(username);

CREATE INDEX idx_users_mmr
ON users(mmr DESC);

CREATE INDEX idx_users_total_wins
ON users(total_wins DESC);

CREATE INDEX idx_users_total_goals
ON users(total_goals DESC);

CREATE INDEX idx_users_total_saves
ON users(total_saves DESC);

CREATE INDEX idx_match_history_player1_id
ON match_history(player1_id);

CREATE INDEX idx_match_history_created_at
ON match_history(created_at DESC);

CREATE INDEX idx_user_car_stats_user_id
ON user_car_stats(user_id);

CREATE INDEX idx_user_car_stats_car_model_id
ON user_car_stats(car_model_id);

-- ============================================================
-- Optional Seed Data
-- Data contoh ini bisa dipakai agar database tidak kosong
-- Password hash di bawah hanya dummy.
-- Kalau backend kamu pakai BCrypt, user asli tetap sebaiknya dibuat lewat API register.
-- ============================================================

INSERT INTO users (
    id,
    username,
    password_hash,
    mmr,
    total_match_played,
    total_wins,
    total_goals,
    total_backward_goals,
    total_saves,
    total_demolitions,
    total_hat_tricks,
    last_used_p1_car,
    last_used_p2_car,
    created_at
) VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'Ziehan0001',
    '$2a$10$dummyhashedpassworddummyhashedpassworddummyhash',
    720,
    15,
    11,
    38,
    3,
    12,
    8,
    2,
    'red_car',
    'blue_car',
    CURRENT_TIMESTAMP
),
(
    '22222222-2222-2222-2222-222222222222',
    'Speedy4821',
    '$2a$10$dummyhashedpassworddummyhashedpassworddummyhash',
    610,
    12,
    8,
    31,
    2,
    9,
    6,
    1,
    'green_car',
    'yellow_car',
    CURRENT_TIMESTAMP
),
(
    '33333333-3333-3333-3333-333333333333',
    'RocketMaster9320',
    '$2a$10$dummyhashedpassworddummyhashedpassworddummyhash',
    540,
    10,
    6,
    24,
    1,
    7,
    4,
    1,
    'purple_car',
    'pink_car',
    CURRENT_TIMESTAMP
),
(
    '44444444-4444-4444-4444-444444444444',
    'Blaze1902',
    '$2a$10$dummyhashedpassworddummyhashedpassworddummyhash',
    430,
    8,
    4,
    18,
    0,
    5,
    3,
    0,
    'white_car',
    'blue_car',
    CURRENT_TIMESTAMP
),
(
    '55555555-5555-5555-5555-555555555555',
    'NightOwl7731',
    '$2a$10$dummyhashedpassworddummyhashedpassworddummyhash',
    350,
    7,
    3,
    14,
    0,
    4,
    2,
    0,
    'yellow_car',
    'red_car',
    CURRENT_TIMESTAMP
);

-- ============================================================
-- Sample Match History
-- ============================================================

INSERT INTO match_history (
    id,
    player1_id,
    player1_name,
    player2_name,
    player1_score,
    player2_score,
    match_result,
    created_at
) VALUES
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111111',
    'Ziehan0001',
    'Guest',
    5,
    2,
    'P1_WIN',
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    '22222222-2222-2222-2222-222222222222',
    'Speedy4821',
    'Guest',
    4,
    3,
    'P1_WIN',
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    '33333333-3333-3333-3333-333333333333',
    'RocketMaster9320',
    'Guest',
    2,
    5,
    'P2_WIN',
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    '44444444-4444-4444-4444-444444444444',
    'Blaze1902',
    'Guest',
    3,
    3,
    'DRAW',
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    '55555555-5555-5555-5555-555555555555',
    'NightOwl7731',
    'Guest',
    6,
    4,
    'P1_WIN',
    CURRENT_TIMESTAMP
);

-- ============================================================
-- Sample User Car Stats
-- ============================================================

INSERT INTO user_car_stats (
    id,
    user_id,
    car_model_id,
    wins,
    goals_scored,
    matches_played,
    last_used
) VALUES
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111111',
    'red_car',
    7,
    22,
    10,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111111',
    'blue_car',
    4,
    16,
    5,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    '22222222-2222-2222-2222-222222222222',
    'green_car',
    8,
    31,
    12,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    '33333333-3333-3333-3333-333333333333',
    'purple_car',
    6,
    24,
    10,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    '44444444-4444-4444-4444-444444444444',
    'white_car',
    4,
    18,
    8,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    '55555555-5555-5555-5555-555555555555',
    'yellow_car',
    3,
    14,
    7,
    CURRENT_TIMESTAMP
);

-- ============================================================
-- End of dump.sql
-- ============================================================