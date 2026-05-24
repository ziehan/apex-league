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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class RegisterScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Texture pitchTex;

    public RegisterScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/bg.png");

        Label.LabelStyle titleStyle = new Label.LabelStyle(skin.getFont("title-font"), Color.GOLD);
        Label header = new Label("REGISTER", titleStyle);
        header.setAlignment(Align.center);

        TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");

        TextField passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextField confirmPasswordField = new TextField("", skin);
        confirmPasswordField.setMessageText("Confirm Password");
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');

        TextButton registerBtn = MenuFactory.createTextButton(skin, "REGISTER");
        TextButton loginLinkBtn = MenuFactory.createTextButton(skin, "Back to Login");

        Table glassPanel = new Table();
        glassPanel.center();
        glassPanel.setBackground(MenuFactory.createPanelDrawable(skin, new Color(0f, 0f, 0f, 0.8f)));
        glassPanel.pad(40f);
        glassPanel.add(header).width(300f).padBottom(30f).row();
        glassPanel.add(usernameField).width(300f).height(50f).padBottom(15f).row();
        glassPanel.add(passwordField).width(300f).height(50f).padBottom(15f).row();
        glassPanel.add(confirmPasswordField).width(300f).height(50f).padBottom(15f).row();
        glassPanel.add(registerBtn).width(300f).height(50f).padBottom(15f).row();
        glassPanel.add(loginLinkBtn).width(300f).height(40f).row();

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(pitchTex)));
        root.add(glassPanel).width(400f);
        stage.addActor(root);

        registerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (passwordField.getText().equals(confirmPasswordField.getText())) {
                    registerBtn.setText("LOADING...");
                    registerBtn.setDisabled(true);

                    String requestJson = "{\"username\":\"" + usernameField.getText() + "\",\"password\":\"" + passwordField.getText() + "\"}";
                    HttpRequestBuilder builder = new HttpRequestBuilder();
                    Net.HttpRequest request = builder.newRequest()
                        .method(Net.HttpMethods.POST)
                        .url(GameManager.API_BASE_URL + "/users/register")
                        .header("Content-Type", "application/json")
                        .content(requestJson)
                        .build();

                    Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                        @Override
                        public void handleHttpResponse(Net.HttpResponse httpResponse) {
                            int status = httpResponse.getStatus().getStatusCode();
                            if (status == 200 || status == 201) {
                                Gdx.app.postRunnable(() -> {
                                    game.goToLogin();
                                    dispose();
                                });
                            } else {
                                Gdx.app.error("Register", "Register failed: HTTP " + status + " - " + httpResponse.getResultAsString());
                                Gdx.app.postRunnable(() -> {
                                    registerBtn.setText("REGISTER");
                                    registerBtn.setDisabled(false);
                                });
                            }
                        }

                        @Override
                        public void failed(Throwable t) {
                            Gdx.app.error("Register", "Register request failed", t);
                            Gdx.app.postRunnable(() -> {
                                registerBtn.setText("REGISTER");
                                registerBtn.setDisabled(false);
                            });
                        }

                        @Override
                        public void cancelled() {
                            Gdx.app.error("Register", "Register request cancelled");
                            Gdx.app.postRunnable(() -> {
                                registerBtn.setText("REGISTER");
                                registerBtn.setDisabled(false);
                            });
                        }
                    });
                } else {
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                }
            }
        });

        loginLinkBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.goToLogin();
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
}

