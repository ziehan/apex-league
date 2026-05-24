package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.managers.GameManager;
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class GameOverScreen implements Screen {
    private final Main game;
    private final GameManager gameManager;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Texture pitchTex;
    private final Texture tableBgTex;
    private final Drawable tableBgDrawable;

    public GameOverScreen(Main game) {
        this.game = game;
        this.gameManager = GameManager.getInstance();
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/football_pitch.png");
        tableBgDrawable = createTableBackground();
        tableBgTex = ((TextureRegionDrawable) tableBgDrawable).getRegion().getTexture();

        Label header = new Label(resolveWinnerText(), skin);
        header.setColor(Color.GOLD);
        header.setFontScale(1.5f);

        Label.LabelStyle redStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.SCARLET);
        Label.LabelStyle blueStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.CYAN);
        Label.LabelStyle goldStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.GOLD);

        Table statsTable = new Table();
        statsTable.setBackground(tableBgDrawable);
        statsTable.pad(30f);
        statsTable.add(new Label("STAT", goldStyle)).padBottom(10f);
        statsTable.add(new Label("TEAM RED", redStyle)).padBottom(10f).align(Align.center);
        statsTable.add(new Label("TEAM BLUE", blueStyle)).padBottom(10f).align(Align.center).row();
        statsTable.add(new Label("GOALS", skin)).padBottom(10f);
        statsTable.add(createCenteredValueLabel(String.valueOf(gameManager.p1Goals))).padBottom(10f);
        statsTable.add(createCenteredValueLabel(String.valueOf(gameManager.p2Goals))).padBottom(10f).row();
        statsTable.add(new Label("SAVES", skin)).padBottom(10f);
        statsTable.add(createCenteredValueLabel(String.valueOf(gameManager.p1Saves))).padBottom(10f);
        statsTable.add(createCenteredValueLabel(String.valueOf(gameManager.p2Saves))).padBottom(10f).row();
        statsTable.add(new Label("DEMOS", skin)).padBottom(10f);
        statsTable.add(createCenteredValueLabel(String.valueOf(gameManager.p1Demos))).padBottom(10f);
        statsTable.add(createCenteredValueLabel(String.valueOf(gameManager.p2Demos))).padBottom(10f);

        TextButton rematchButton = MenuFactory.createTextButton(skin, "REMATCH");
        TextButton menuButton = MenuFactory.createTextButton(skin, "MAIN MENU");

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
                game.goToPlay();
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                resetMatchState();
                game.goToMainMenu();
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

    private Drawable createTableBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.7f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private Label createCenteredValueLabel(String text) {
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);
        return label;
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
        tableBgTex.dispose();
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
        gameManager.resetMatchRecordFlag();
        gameManager.winnerText = "";
        gameManager.isResetting = false;
        gameManager.resetTimer = 0f;
        gameManager.isKickoff = true;
        gameManager.kickoffTimer = 3f;
        gameManager.isPaused = false;
    }
}
