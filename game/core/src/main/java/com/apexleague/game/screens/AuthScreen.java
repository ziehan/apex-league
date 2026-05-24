package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.managers.GameManager;
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AuthScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Texture pitchTex;

    public AuthScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/football_pitch.png");

        Label header = new Label("USER AUTHENTICATION", skin);
        header.setColor(Color.GOLD);
        header.setFontScale(1.2f);

        TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");

        TextField passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton loginButton = MenuFactory.createTextButton(skin, "LOGIN");
        TextButton registerButton = MenuFactory.createTextButton(skin, "REGISTER");
        TextButton guestButton = MenuFactory.createTextButton(skin, "PLAY AS GUEST (OFFLINE)");

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.setBackground(MenuFactory.createPanelDrawable(skin, new Color(0f, 0f, 0f, 0.8f)));
        root.pad(40f);
        root.add(header).padBottom(20f).row();
        root.add(usernameField).width(360f).height(48f).padBottom(12f).row();
        root.add(passwordField).width(360f).height(48f).padBottom(18f).row();
        root.add(loginButton).width(360f).height(52f).padBottom(10f).row();
        root.add(registerButton).width(360f).height(52f).padBottom(10f).row();
        root.add(guestButton).width(360f).height(52f).row();
        stage.addActor(root);

        guestButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                GameManager.getInstance().currentUserToken = "GUEST";
                GameManager.getInstance().currentUserId = "GUEST";
                game.goToMainMenu();
                dispose();
            }
        });

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                sendAuthRequest("LOGIN", usernameField.getText(), passwordField.getText());
                game.goToMainMenu();
                dispose();
            }
        });

        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                sendAuthRequest("REGISTER", usernameField.getText(), passwordField.getText());
                game.goToMainMenu();
                dispose();
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

    private void sendAuthRequest(String mode, String username, String password) {
        Gdx.app.log("Auth", "Stub " + mode + " for user: " + username);
    }
}

