package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.managers.GameManager;
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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class MainMenuScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Label statsLabel;
    private final Texture pitchTex;

    private final Table mainTable;

    public MainMenuScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/football_pitch.png");

        Label title = new Label("APEX LEAGUE", skin);
        TextButton playButton = MenuFactory.createTextButton(skin, "PLAY");
        TextButton garageButton = MenuFactory.createTextButton(skin, "GARAGE");
        TextButton profileButton = MenuFactory.createTextButton(skin, "PROFILE");
        TextButton quitButton = MenuFactory.createTextButton(skin, "QUIT");
        statsLabel = new Label("", skin);

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        mainTable.add(title).padBottom(24f).row();
        mainTable.add(playButton).width(320f).height(56f).padBottom(12f).row();
        mainTable.add(garageButton).width(320f).height(56f).padBottom(12f).row();
        mainTable.add(profileButton).width(320f).height(56f).padBottom(12f).row();
        mainTable.add(quitButton).width(320f).height(56f);
        stage.addActor(mainTable);

        Table statsTable = new Table();
        statsTable.setFillParent(true);
        statsTable.top().left();
        statsTable.add(statsLabel).padLeft(12f).padTop(12f);
        stage.addActor(statsTable);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                GameManager.getInstance().resetStats();
                GameManager.getInstance().resetMatchRecordFlag();
                game.goToPlay();
            }
        });

        garageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.goToGarage();
            }
        });

        profileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.goToProfile();
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.exit();
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
        stage.getViewport().apply();
        game.batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        game.batch.setColor(0.15f, 0.15f, 0.15f, 1f);
        game.batch.begin();
        game.batch.draw(pitchTex, 0f, 0f, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        game.batch.end();
        game.batch.setColor(Color.WHITE);
        GameManager gm = GameManager.getInstance();
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
        pitchTex.dispose();
    }
}
