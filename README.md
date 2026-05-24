# Apex League — Kinematic Kickoff

**A Top-Down 2D Rocket-Car Soccer** focused on vector physics, precise impulses, and fast-paced competitive play.

This repository contains the game client (libGDX) and a Spring Boot backend API used for user accounts, match history, leaderboards, and car statistics.

**Quick links**
- Game design summary: [docs/Game Design Document_ Apex League_ Kinematic Kickoff (1).md](c:\Users\nazie\Downloads\Game Design Document_ Apex League_ Kinematic Kickoff (1).md#L1)
- Backend application properties: [backend/src/main/resources/application.properties](backend/src/main/resources/application.properties#L1)
- Backend controllers: [backend/src/main/java/com/apexleague/backend/controller/UserController.java](backend/src/main/java/com/apexleague/backend/controller/UserController.java#L1)

**Contents**
- **Overview** — ringkasan permainan
- **Project structure** — modul dan file penting
- **API / Endpoints** — daftar endpoint backend dengan contoh payload
- **Setup Backend** — prasyarat, konfigurasi, build & run
- **Setup Client (Frontend / Game)** — menjalankan game lokal di desktop
- **Notes** — Redis, database, dan pengaturan keamanan ringan

**Overview**

Apex League adalah game sepak bola mobil 2D (top-down) yang menekankan pengendalian momentum dan manuver berbasis impulse pada bidang datar. Fitur utama berdasarkan dokumen desain:
- Mekanik "lompat" 2D berupa linear impulse pada X/Y
- Boost dan status Supersonic (jejak visual)
- Demolition saat menabrak dengan kecepatan tinggi
- Sistem ECS ringan, object pool, dan arsitektur modular (lihat dokumen desain terlampir)

**Project Structure (ringkasan)**

- **backend/** — Spring Boot API (Java 17, Gradle). Lihat [backend/build.gradle](backend/build.gradle#L1)
	- `src/main/java/com/apexleague/backend/controller/` — controller API (user, leaderboard, matches, car-stats)
	- `src/main/resources/application.properties` — konfigurasi datasource (Postgres) dan lainnya
- **core/** — game logic (shared libGDX core module)
- **lwjgl3/** — desktop launcher and assets (run jar or `:lwjgl3:run`)
- **game/** — higher-level game module and assets (may contain build scripts / tools)
- **docs/** — documentation and other supporting files

Refer to code for full details when needed.

**Backend API / Endpoints**

Base URL: `http://localhost:8080` (default Spring Boot port)

- POST `/api/users/register`
	- Deskripsi: Buat user baru
	- Body (JSON): `{"username":"player1", "password":"secret"}`
	- Response: 201 Created — UserResponseDto (id, username, stats)

- POST `/api/users/login`
	- Deskripsi: Simple login check (username/password). Mengembalikan 200 OK dengan data user atau 401 Unauthorized
	- Body: `{"username":"player1","password":"secret"}`

- GET `/api/users` — daftar semua user
- GET `/api/users/{username}` — detail user by username
- GET `/api/users/scoreboard` — top players (by goals)
- DELETE `/api/users/{id}` — hapus user (id = UUID)
- GET `/api/users/{id}/stats` — statistik lengkap user + match history
- GET `/api/users/{username}/full` — full stats including car stats and last used cars
- PUT `/api/users/{username}/cars`
	- Body: `{"p1Car":"carA","p2Car":"carB"}` — update pilihan mobil

- POST `/api/matches` and POST `/api/match`
	- Deskripsi: Simpan hasil pertandingan
	- Body (JSON) contoh (MatchSubmitRequestDto):
		{
			"player1Id":"<uuid>",
			"player1Name":"player1",
			"p1Car":"carA",
			"p2Car":"carB",
			"p1Goals":2,
			"p2Goals":1,
			"p1Saves":1,
			"p1Demos":0,
			"matchResult":"WIN"
		}
	- Response: 201 Created — saved MatchHistory object

- GET `/api/matches/player/{playerId}` — dapatkan riwayat pertandingan pemain
- DELETE `/api/matches/{matchId}` — hapus riwayat pertandingan

- PUT `/api/car-stats` — update statistik mobil pengguna
	- Model `UserCarStat` (lihat `backend/src/main/java/com/apexleague/backend/model/UserCarStat.java`): `user`, `carModelId`, `wins`, `goalsScored`, `matchesPlayed`

- GET `/api/leaderboard?category=mmr&limit=100`
	- Ambil top players berdasarkan strategi leaderboard (Redis-backed for beberapa kategori). Lihat [backend/src/main/java/com/apexleague/backend/service/implementation/WinsLeaderboardStrategy.java](backend/src/main/java/com/apexleague/backend/service/implementation/WinsLeaderboardStrategy.java#L1)
- GET `/api/leaderboard/keys` — daftar key leaderboard di Redis

**Setup Backend (lengkap)**

1) Prasyarat
	 - Java 17 (toolchain disetel di Gradle)
	 - Gradle (wrapper tersedia: `gradlew.bat` on Windows)
	 - PostgreSQL ( disarankan versi terbaru )
	 - (Opsional) Redis untuk leaderboard yang cepat

2) Konfigurasi database
	 - Default konfigurasi ada di [backend/src/main/resources/application.properties](backend/src/main/resources/application.properties#L1)
	 - Contoh (default repo): `spring.datasource.url=jdbc:postgresql://localhost:5432/apex_league` dan kredensial
	 - Buat database lokal:

```
psql -U postgres -c "CREATE DATABASE apex_league;"
```

	 - Jika ingin menggunakan environment variables, edit file `application.properties` atau gunakan Spring profile untuk memisahkan konfigurasi.

3) (Opsional) Setup Redis
	 - Jika ingin menggunakan leaderboard Redis-backed, install dan jalankan Redis di `localhost:6379`.
	 - Pada Windows gunakan WSL atau instalasi Redis yang sesuai.

4) Build & Run backend (development)

```
cd backend
..\gradlew.bat bootRun
```

atau buat jar dan jalankan:

```
cd backend
..\gradlew.bat clean build
java -jar build\libs\backend-0.0.1-SNAPSHOT.jar
```

5) Verifikasi
	 - API akan tersedia di `http://localhost:8080` (cek `http://localhost:8080/api/users`)

**Setup Client / Frontend (Game — libGDX desktop)**

1) Prasyarat
	 - Java 17
	 - Gradle wrapper (disertakan)

2) Jalankan langsung (development)

```
..\gradlew.bat :lwjgl3:run
```

3) Build jar executable

```
..\gradlew.bat :lwjgl3:jar
```

	 - Hasil jar berada di `lwjgl3\build\libs`.

4) Menjalankan game
	 - Jalankan jar yang dihasilkan atau gunakan `:lwjgl3:run` saat pengembangan.

5) Pengaturan koneksi ke backend
	 - Jika game memiliki integrasi online, titik akhir API backend diasumsikan `http://localhost:8080/api`. Sesuaikan alamat di konfigurasi client (cek modul `core`/`lwjgl3` untuk kode koneksi HTTP jika tersedia).
