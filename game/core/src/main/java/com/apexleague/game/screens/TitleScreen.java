package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TitleScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Texture pitchTex;
    private final Label promptLabel;
    private float stateTime = 0f;

    public TitleScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/bg.png");

        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("title-font"), Color.GOLD);
        Label titleLabel = new Label("APEX LEAGUE", titleStyle);

        Label.LabelStyle promptStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);
        promptLabel = new Label("PRESS ANY KEY TO START", promptStyle);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(titleLabel).padBottom(24f).row();
        root.add(promptLabel);
        stage.addActor(root);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.03f, 0.05f, 0.08f, 1f);
        stateTime += delta;

        stage.getViewport().apply();
        game.batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        game.batch.setColor(0.15f, 0.15f, 0.15f, 1f);
        game.batch.begin();
        game.batch.draw(pitchTex, 0f, 0f, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        game.batch.end();
        game.batch.setColor(Color.WHITE);

        float alpha = 0.5f + 0.5f * MathUtils.sin(stateTime * 2f);
        Color promptColor = promptLabel.getColor();
        promptLabel.setColor(promptColor.r, promptColor.g, promptColor.b, alpha);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
            game.goToLogin();
            dispose();
        }
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

