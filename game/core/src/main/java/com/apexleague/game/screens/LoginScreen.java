package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.managers.GameManager;
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LoginScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Texture pitchTex;

    public LoginScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/football_pitch.png");

        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("title-font"), Color.GOLD);
        Label header = new Label("LOGIN", titleStyle);
        header.setAlignment(Align.center);

        TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");

        TextField passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton loginBtn = MenuFactory.createTextButton(skin, "LOGIN");
        TextButton registerLinkBtn = MenuFactory.createTextButton(skin, "Create Account");

        Table glassPanel = new Table();
        glassPanel.center();
        glassPanel.setBackground(MenuFactory.createPanelDrawable(skin, new Color(0f, 0f, 0f, 0.8f)));
        glassPanel.pad(40f);
        glassPanel.add(header).width(300f).padBottom(30f).row();
        glassPanel.add(usernameField).width(300f).height(50f).padBottom(15f).row();
        glassPanel.add(passwordField).width(300f).height(50f).padBottom(30f).row();
        glassPanel.add(loginBtn).width(300f).height(50f).padBottom(15f).row();
        glassPanel.add(registerLinkBtn).width(300f).height(40f).row();

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(glassPanel).width(400f);
        stage.addActor(root);

        registerLinkBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.goToRegister();
            }
        });

        loginBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                loginBtn.setText("LOADING...");
                loginBtn.setDisabled(true);

                String requestJson = "{\"username\":\"" + usernameField.getText() + "\",\"password\":\"" + passwordField.getText() + "\"}";
                HttpRequestBuilder builder = new HttpRequestBuilder();
                Net.HttpRequest request = builder.newRequest()
                    .method(Net.HttpMethods.POST)
                    .url(GameManager.API_BASE_URL + "/users/login")
                    .header("Content-Type", "application/json")
                    .content(requestJson)
                    .build();

                Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        int status = httpResponse.getStatus().getStatusCode();
                        if (status == 200) {
                            JsonValue root = new JsonReader().parse(httpResponse.getResultAsString());
                            GameManager gm = GameManager.getInstance();
                            gm.jwtToken = root.getString("token", "");
                            gm.currentUserId = root.getString("id", "");
                            gm.currentUsername = usernameField.getText();
                            Gdx.app.postRunnable(() -> {
                                game.goToMainMenu();
                                dispose();
                            });
                        } else {
                            Gdx.app.error("LOGIN", "Login failed: HTTP " + status + " - " + httpResponse.getResultAsString());
                            Gdx.app.postRunnable(() -> {
                                loginBtn.setText("LOGIN");
                                loginBtn.setDisabled(false);
                            });
                        }
                    }

                    @Override
                    public void failed(Throwable t) {
                        Gdx.app.error("LOGIN", "HTTP FAIL: " + t.getMessage(), t);
                        Gdx.app.postRunnable(() -> {
                            loginBtn.setText("LOGIN");
                            loginBtn.setDisabled(false);
                        });
                    }

                    @Override
                    public void cancelled() {
                        Gdx.app.error("LOGIN", "Login request cancelled");
                        Gdx.app.postRunnable(() -> {
                            loginBtn.setText("LOGIN");
                            loginBtn.setDisabled(false);
                        });
                    }
                });
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
    public void pause() {}
    @Override
    public void resume() {}
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
