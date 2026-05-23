package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.state.GameManager;
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen implements Screen {
    private final Main game;
    private final GameManager gameManager;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;

    public GameOverScreen(Main game) {
        this.game = game;
        this.gameManager = GameManager.getInstance();
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();

        Label header = new Label(resolveWinnerText(), skin);
        header.setColor(Color.WHITE);

        Table statsTable = new Table();
        statsTable.add(new Label("", skin)).pad(6f);
        statsTable.add(new Label("TEAM RED", skin)).pad(6f);
        statsTable.add(new Label("TEAM BLUE", skin)).pad(6f);
        statsTable.row();
        statsTable.add(new Label("GOALS", skin)).pad(6f);
        statsTable.add(new Label(String.valueOf(gameManager.p1Goals), skin)).pad(6f);
        statsTable.add(new Label(String.valueOf(gameManager.p2Goals), skin)).pad(6f);
        statsTable.row();
        statsTable.add(new Label("SAVES", skin)).pad(6f);
        statsTable.add(new Label(String.valueOf(gameManager.p1Saves), skin)).pad(6f);
        statsTable.add(new Label(String.valueOf(gameManager.p2Saves), skin)).pad(6f);
        statsTable.row();
        statsTable.add(new Label("DEMOS", skin)).pad(6f);
        statsTable.add(new Label(String.valueOf(gameManager.p1Demos), skin)).pad(6f);
        statsTable.add(new Label(String.valueOf(gameManager.p2Demos), skin)).pad(6f);

        TextButton rematchButton = new TextButton("REMATCH", skin);
        TextButton menuButton = new TextButton("MAIN MENU", skin);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(header).padBottom(20f).row();
        table.add(statsTable).padBottom(20f).row();
        table.add(rematchButton).width(320f).height(56f).padBottom(12f).row();
        table.add(menuButton).width(320f).height(56f);
        stage.addActor(table);

        rematchButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                resetMatchState();
                gameManager.resetStats();
                game.setScreen(new PlayScreen(game));
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                resetMatchState();
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.03f, 0.05f, 0.08f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    private String resolveWinnerText() {
        if (gameManager.winnerText != null) {
            if (gameManager.winnerText.contains("KIRI")) {
                return "TEAM RED WINS!";
            }
            if (gameManager.winnerText.contains("KANAN")) {
                return "TEAM BLUE WINS!";
            }
        }
        return "MATCH OVER";
    }

    private void resetMatchState() {
        gameManager.leftScore = 0;
        gameManager.rightScore = 0;
        gameManager.matchTimer = 300f;
        gameManager.isOvertime = false;
        gameManager.isGameOver = false;
        gameManager.winnerText = "";
        gameManager.isResetting = false;
        gameManager.resetTimer = 0f;
        gameManager.isKickoff = true;
        gameManager.kickoffTimer = 3f;
        gameManager.isPaused = false;
    }
}
