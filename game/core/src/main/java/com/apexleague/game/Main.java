package com.apexleague.game;

import com.apexleague.game.screens.CareerScreen;
import com.apexleague.game.screens.GameOverScreen;
import com.apexleague.game.screens.GarageScreen;
import com.apexleague.game.screens.LeaderboardScreen;
import com.apexleague.game.screens.LoginScreen;
import com.apexleague.game.screens.MainMenuScreen;
import com.apexleague.game.screens.MatchHistoryScreen;
import com.apexleague.game.screens.PlayScreen;
import com.apexleague.game.screens.ProfileScreen;
import com.apexleague.game.screens.RegisterScreen;
import com.apexleague.game.screens.TitleScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
        goToTitle();
    }

    public void goToTitle() {
        setScreen(new TitleScreen(this));
    }

    public void goToLogin() {
        setScreen(new LoginScreen(this));
    }

    public void goToRegister() {
        setScreen(new RegisterScreen(this));
    }

    public void goToMainMenu() {
        setScreen(new MainMenuScreen(this));
    }

    public void goToProfile() {
        setScreen(new ProfileScreen(this));
    }

    public void goToCareer() {
        setScreen(new CareerScreen(this));
    }

    public void goToMatchHistory() {
        setScreen(new MatchHistoryScreen(this));
    }

    public void goToLeaderboard() {
        setScreen(new LeaderboardScreen(this));
    }

    public void goToGarage() {
        setScreen(new GarageScreen(this));
    }

    public void goToPlay() {
        setScreen(new PlayScreen(this));
    }

    public void goToGameOver() {
        setScreen(new GameOverScreen(this));
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        super.dispose();
    }
}
