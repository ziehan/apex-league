package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.state.GameManager;
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Label statsLabel;

    private enum MenuView {
        MAIN,
        PROFILE,
        CAREER,
        MATCH_HISTORY
    }

    private final Table mainTable;
    private final Table profileTable;
    private final Table careerTable;
    private final Table matchHistoryTable;
    private MenuView activeView = MenuView.MAIN;

    private final Label careerWinsRedLabel;
    private final Label careerGoalsRedLabel;
    private final Label careerGoalsBlueLabel;
    private final Label careerSavesRedLabel;
    private final Label careerSavesBlueLabel;
    private final Label careerDemosRedLabel;
    private final Label careerDemosBlueLabel;
    private final Label careerBackwardLabel;

    private final Label historyMatchesRedLabel;
    private final Label historyMatchesBlueLabel;
    private final Label historyWinsRedLabel;
    private final Label historyWinsBlueLabel;
    private final Label historyGoalsRedLabel;
    private final Label historyGoalsBlueLabel;

    public MainMenuScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();

        Label title = new Label("APEX LEAGUE", skin);
        TextButton playButton = new TextButton("PLAY", skin);
        TextButton profileButton = new TextButton("PROFILE", skin);
        TextButton quitButton = new TextButton("QUIT", skin);
        statsLabel = new Label("", skin);

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        mainTable.add(title).padBottom(24f).row();
        mainTable.add(playButton).width(320f).height(56f).padBottom(12f).row();
        mainTable.add(profileButton).width(320f).height(56f).padBottom(12f).row();
        mainTable.add(quitButton).width(320f).height(56f);
        stage.addActor(mainTable);

        profileTable = new Table();
        profileTable.setFillParent(true);
        profileTable.center();
        Label profileTitle = new Label("PROFILE", skin);
        TextButton careerButton = new TextButton("CAREER", skin);
        TextButton matchHistoryButton = new TextButton("MATCH HISTORY", skin);
        TextButton backButton = new TextButton("BACK", skin);
        profileTable.add(profileTitle).padBottom(24f).row();
        profileTable.add(careerButton).width(320f).height(56f).padBottom(12f).row();
        profileTable.add(matchHistoryButton).width(320f).height(56f).padBottom(12f).row();
        profileTable.add(backButton).width(320f).height(56f);
        stage.addActor(profileTable);

        careerTable = new Table();
        careerTable.setFillParent(true);
        careerTable.center();
        Label careerTitle = new Label("CAREER STATS", skin);
        TextButton careerBackButton = new TextButton("BACK", skin);
        Table careerStats = new Table();
        careerStats.add(new Label("STAT", skin)).pad(6f);
        careerStats.add(new Label("RED", skin)).pad(6f);
        careerStats.add(new Label("BLUE", skin)).pad(6f).row();
        careerStats.add(new Label("WINS", skin)).pad(6f);
        careerWinsRedLabel = new Label("0", skin);
        careerStats.add(careerWinsRedLabel).pad(6f);
        careerStats.add(new Label("-", skin)).pad(6f).row();
        careerStats.add(new Label("GOALS", skin)).pad(6f);
        careerGoalsRedLabel = new Label("0", skin);
        careerGoalsBlueLabel = new Label("0", skin);
        careerStats.add(careerGoalsRedLabel).pad(6f);
        careerStats.add(careerGoalsBlueLabel).pad(6f).row();
        careerStats.add(new Label("SAVES", skin)).pad(6f);
        careerSavesRedLabel = new Label("0", skin);
        careerSavesBlueLabel = new Label("0", skin);
        careerStats.add(careerSavesRedLabel).pad(6f);
        careerStats.add(careerSavesBlueLabel).pad(6f).row();
        careerStats.add(new Label("DEMOS", skin)).pad(6f);
        careerDemosRedLabel = new Label("0", skin);
        careerDemosBlueLabel = new Label("0", skin);
        careerStats.add(careerDemosRedLabel).pad(6f);
        careerStats.add(careerDemosBlueLabel).pad(6f).row();
        careerStats.add(new Label("BACKWARD GOALS", skin)).pad(6f);
        careerBackwardLabel = new Label("0", skin);
        careerStats.add(careerBackwardLabel).pad(6f);
        careerStats.add(new Label("-", skin)).pad(6f);
        careerTable.add(careerTitle).padBottom(16f).row();
        careerTable.add(careerStats).padBottom(16f).row();
        careerTable.add(careerBackButton).width(220f).height(50f);
        stage.addActor(careerTable);

        matchHistoryTable = new Table();
        matchHistoryTable.setFillParent(true);
        matchHistoryTable.center();
        Label historyTitle = new Label("LEADERBOARD", skin);
        TextButton historyBackButton = new TextButton("BACK", skin);
        Table historyStats = new Table();
        historyStats.add(new Label("STAT", skin)).pad(6f);
        historyStats.add(new Label("RED", skin)).pad(6f);
        historyStats.add(new Label("BLUE", skin)).pad(6f).row();
        historyStats.add(new Label("MATCHES", skin)).pad(6f);
        historyMatchesRedLabel = new Label("0", skin);
        historyMatchesBlueLabel = new Label("0", skin);
        historyStats.add(historyMatchesRedLabel).pad(6f);
        historyStats.add(historyMatchesBlueLabel).pad(6f).row();
        historyStats.add(new Label("WINS", skin)).pad(6f);
        historyWinsRedLabel = new Label("0", skin);
        historyWinsBlueLabel = new Label("0", skin);
        historyStats.add(historyWinsRedLabel).pad(6f);
        historyStats.add(historyWinsBlueLabel).pad(6f).row();
        historyStats.add(new Label("GOALS", skin)).pad(6f);
        historyGoalsRedLabel = new Label("0", skin);
        historyGoalsBlueLabel = new Label("0", skin);
        historyStats.add(historyGoalsRedLabel).pad(6f);
        historyStats.add(historyGoalsBlueLabel).pad(6f);
        matchHistoryTable.add(historyTitle).padBottom(16f).row();
        matchHistoryTable.add(historyStats).padBottom(16f).row();
        matchHistoryTable.add(historyBackButton).width(220f).height(50f);
        stage.addActor(matchHistoryTable);

        Table statsTable = new Table();
        statsTable.setFillParent(true);
        statsTable.top().left();
        statsTable.add(statsLabel).padLeft(12f).padTop(12f);
        stage.addActor(statsTable);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                GameManager.getInstance().resetStats();
                game.setScreen(new PlayScreen(game));
            }
        });

        profileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                setView(MenuView.PROFILE);
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.exit();
            }
        });

        careerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                setView(MenuView.CAREER);
            }
        });

        matchHistoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                setView(MenuView.MATCH_HISTORY);
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                setView(MenuView.MAIN);
            }
        });

        careerBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                setView(MenuView.PROFILE);
            }
        });

        historyBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                setView(MenuView.PROFILE);
            }
        });

        setView(MenuView.MAIN);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.03f, 0.05f, 0.08f, 1f);
        GameManager gm = GameManager.getInstance();
        statsLabel.setText(
            "Matches Played: " + gm.totalMatches +
                " | Red Wins: " + gm.totalP1Wins +
                " | Blue Wins: " + gm.totalP2Wins
        );
        refreshCareerStats();
        refreshMatchHistory();
        stage.act(delta);
        stage.draw();
    }

    private void refreshCareerStats() {
        if (activeView != MenuView.CAREER) {
            return;
        }
        GameManager gm = GameManager.getInstance();
        careerWinsRedLabel.setText(String.valueOf(gm.totalP1Wins));
        careerGoalsRedLabel.setText(String.valueOf(gm.p1Goals));
        careerGoalsBlueLabel.setText(String.valueOf(gm.p2Goals));
        careerSavesRedLabel.setText(String.valueOf(gm.p1Saves));
        careerSavesBlueLabel.setText(String.valueOf(gm.p2Saves));
        careerDemosRedLabel.setText(String.valueOf(gm.p1Demos));
        careerDemosBlueLabel.setText(String.valueOf(gm.p2Demos));
        careerBackwardLabel.setText(String.valueOf(gm.totalBackwardGoals));
    }

    private void refreshMatchHistory() {
        if (activeView != MenuView.MATCH_HISTORY) {
            return;
        }
        GameManager gm = GameManager.getInstance();
        historyMatchesRedLabel.setText(String.valueOf(gm.totalMatches));
        historyMatchesBlueLabel.setText(String.valueOf(gm.totalMatches));
        historyWinsRedLabel.setText(String.valueOf(gm.totalP1Wins));
        historyWinsBlueLabel.setText(String.valueOf(gm.totalP2Wins));
        historyGoalsRedLabel.setText(String.valueOf(gm.p1Goals));
        historyGoalsBlueLabel.setText(String.valueOf(gm.p2Goals));
    }

    private void setView(MenuView view) {
        activeView = view;
        mainTable.setVisible(view == MenuView.MAIN);
        profileTable.setVisible(view == MenuView.PROFILE);
        careerTable.setVisible(view == MenuView.CAREER);
        matchHistoryTable.setVisible(view == MenuView.MATCH_HISTORY);
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
}
