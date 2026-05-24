package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.managers.GameManager;
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class GarageScreen implements Screen {
    private static final String[] CAR_TYPES = {
        "red_car", "blue_car", "green_car", "yellow_car", "pink_car", "purple_car", "white_car"
    };

    private final Main game;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Texture pitchTex;
    private final GameManager gameManager;
    private final List<Texture> carTextures = new ArrayList<>();
    private Label p1StatLabel;
    private Label p2StatLabel;

    public GarageScreen(Main game) {
        this.game = game;
        this.gameManager = GameManager.getInstance();

        gameManager.p1CarType = (gameManager.lastUsedP1Car != null && !gameManager.lastUsedP1Car.isEmpty())
            ? gameManager.lastUsedP1Car : "red_car";
        gameManager.p2CarType = (gameManager.lastUsedP2Car != null && !gameManager.lastUsedP2Car.isEmpty())
            ? gameManager.lastUsedP2Car : "blue_car";

        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/bg.png");

        Label header = new Label("GARAGE", skin);
        header.setColor(Color.GOLD);
        header.setFontScale(1.5f);

        p1StatLabel = new Label("WINS: 0\nGOALS: 0", skin);
        p1StatLabel.setAlignment(Align.center);
        p1StatLabel.setColor(Color.SCARLET);

        p2StatLabel = new Label("WINS: 0\nGOALS: 0", skin);
        p2StatLabel.setAlignment(Align.center);
        p2StatLabel.setColor(Color.CYAN);

        Table columns = new Table();
        columns.add(buildSelectionColumn("P1 SELECTION", Color.SCARLET, true)).padRight(30f);
        columns.add(buildSelectionColumn("P2 SELECTION", Color.CYAN, false));

        TextButton backButton = MenuFactory.createTextButton(skin, "BACK");
        TextButton saveButton = MenuFactory.createTextButton(skin, "SIMPAN");

        Table bottomTable = new Table();
        bottomTable.add(p1StatLabel).padRight(60f);
        bottomTable.add(backButton).width(150f).height(50f).padRight(20f);
        bottomTable.add(saveButton).width(150f).height(50f);
        bottomTable.add(p2StatLabel).padLeft(60f);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.setBackground(MenuFactory.createPanelDrawable(skin, new Color(0f, 0f, 0f, 0.8f)));
        root.pad(30f);
        root.add(header).padBottom(20f).row();
        root.add(columns).padBottom(20f).row();
        root.add(bottomTable).padTop(30f).row();
        stage.addActor(root);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.goToMainMenu();
            }
        });

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                saveButton.setText("SAVING...");
                saveButton.setDisabled(true);

                gameManager.lastUsedP1Car = gameManager.p1CarType;
                gameManager.lastUsedP2Car = gameManager.p2CarType;

                if (gameManager.currentUsername != null && !gameManager.currentUsername.isEmpty() && !gameManager.currentUsername.equalsIgnoreCase("GUEST")) {
                    String payload = "{\"p1Car\":\"" + gameManager.p1CarType + "\",\"p2Car\":\"" + gameManager.p2CarType + "\"}";
                    HttpRequestBuilder builder = new HttpRequestBuilder();
                    Net.HttpRequest request = builder.newRequest()
                        .method(Net.HttpMethods.PUT)
                        .url(GameManager.API_BASE_URL + "/users/" + gameManager.currentUsername + "/cars")
                        .header("Content-Type", "application/json")
                        .content(payload)
                        .build();

                    if (gameManager.jwtToken != null && !gameManager.jwtToken.isEmpty()) {
                        request.setHeader("Authorization", "Bearer " + gameManager.jwtToken);
                    }

                    Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                        @Override
                        public void handleHttpResponse(Net.HttpResponse httpResponse) {
                            Gdx.app.log("GARAGE", "Menyimpan P1: " + gameManager.p1CarType + " P2: " + gameManager.p2CarType);
                            Gdx.app.postRunnable(() -> game.goToMainMenu());
                        }

                        @Override
                        public void failed(Throwable t) {
                            Gdx.app.error("GARAGE", "Gagal menyimpan", t);
                            Gdx.app.postRunnable(() -> game.goToMainMenu());
                        }

                        @Override
                        public void cancelled() {
                            Gdx.app.postRunnable(() -> game.goToMainMenu());
                        }
                    });
                } else {
                    game.goToMainMenu();
                }
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
        GameManager.CarStat p1Stat = gm.getCarStat(gm.p1CarType);
        GameManager.CarStat p2Stat = gm.getCarStat(gm.p2CarType);
        p1StatLabel.setText("WINS: " + p1Stat.wins + "\nGOALS: " + p1Stat.goals);
        p2StatLabel.setText("WINS: " + p2Stat.wins + "\nGOALS: " + p2Stat.goals);

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
        for (Texture texture : carTextures) { texture.dispose(); }
    }

    private Table buildSelectionColumn(String title, Color titleColor, boolean isPlayerOne) {
        Label label = new Label(title, skin);
        label.setColor(titleColor);

        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(1);
        group.setMaxCheckCount(1);

        Table grid = new Table();
        int columns = 4;
        for (int i = 0; i < CAR_TYPES.length; i++) {
            String carType = CAR_TYPES[i];
            ImageButton button = createCarButton(carType, group, isPlayerOne);
            grid.add(button).size(96f).pad(6f);
            if ((i + 1) % columns == 0) {
                grid.row();
            }
        }

        Table column = new Table();
        column.add(label).padBottom(12f).row();
        column.add(grid);
        return column;
    }

    private ImageButton createCarButton(String carType, ButtonGroup<ImageButton> group, boolean isPlayerOne) {
        Texture texture = new Texture(resolveCarTexturePath(carType));
        carTextures.add(texture);

        Drawable background = MenuFactory.createPanelDrawable(skin, new Color(0f, 0f, 0f, 0.45f));
        Drawable backgroundChecked = MenuFactory.createPanelDrawable(skin, new Color(0.9f, 0.75f, 0.2f, 0.6f));
        Drawable backgroundOver = MenuFactory.createPanelDrawable(skin, new Color(0.2f, 0.26f, 0.34f, 0.6f));

        Drawable iconUp = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.85f, 0.85f, 0.85f, 1f));
        Drawable iconChecked = new TextureRegionDrawable(new TextureRegion(texture)).tint(Color.WHITE);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = background;
        style.over = backgroundOver;
        style.checked = backgroundChecked;
        style.imageUp = iconUp;
        style.imageChecked = iconChecked;
        style.imageOver = iconChecked;

        ImageButton button = new ImageButton(style);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (isPlayerOne) {
                    gameManager.p1CarType = carType;
                } else {
                    gameManager.p2CarType = carType;
                }
            }
        });

        if (isPlayerOne && carType.equals(gameManager.p1CarType)) {
            button.setChecked(true);
        } else if (!isPlayerOne && carType.equals(gameManager.p2CarType)) {
            button.setChecked(true);
        }

        group.add(button);
        return button;
    }

    private String resolveCarTexturePath(String carType) {
        String safeType = carType == null || carType.isEmpty() ? "red_car" : carType;
        String basePath = "images/" + safeType + ".png";
        if (Gdx.files.internal(basePath).exists()) { return basePath; }
        String dashedPath = "images/" + safeType.replace('_', '-') + ".png";
        if (Gdx.files.internal(dashedPath).exists()) { return dashedPath; }
        return "images/red_car.png";
    }
}
