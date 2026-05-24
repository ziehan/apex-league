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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CareerScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Texture pitchTex;
    private final Texture tableBgTex;
    private final Drawable tableBgDrawable;

    private final Label p1WinsLabel;
    private final Label p2WinsLabel;
    private final Label p1GoalsLabel;
    private final Label p2GoalsLabel;
    private final Label p1SavesLabel;
    private final Label p2SavesLabel;
    private final Label p1MmrLabel;
    private final Label p2MmrLabel;

    public CareerScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/bg.png");
        tableBgDrawable = createTableBackground();
        tableBgTex = ((TextureRegionDrawable) tableBgDrawable).getRegion().getTexture();

        Label header = new Label("CAREER STATS", skin);
        header.setColor(Color.GOLD);
        header.setFontScale(1.5f);

        Table statsTable = new Table();
        statsTable.setBackground(tableBgDrawable);
        statsTable.pad(30f);

        Label.LabelStyle headerStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.LIGHT_GRAY);
        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);
        Label.LabelStyle redStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.SCARLET);
        Label.LabelStyle blueStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.CYAN);

        Label headerStat = new Label("STAT", headerStyle);
        Label headerRed = new Label("RED", headerStyle);
        Label headerBlue = new Label("BLUE", headerStyle);
        headerStat.setAlignment(Align.left);
        headerRed.setAlignment(Align.center);
        headerBlue.setAlignment(Align.center);
        statsTable.add(headerStat).width(220f).padBottom(12f).align(Align.left);
        statsTable.add(headerRed).width(120f).padBottom(12f).align(Align.center);
        statsTable.add(headerBlue).width(120f).padBottom(12f).align(Align.center).row();

        statsTable.add(new Label("MMR", labelStyle)).padBottom(10f).align(Align.left);
        p1MmrLabel = new Label("0", redStyle);
        p1MmrLabel.setAlignment(Align.center);
        statsTable.add(p1MmrLabel).padBottom(10f).align(Align.center);
        p2MmrLabel = new Label("-", blueStyle);
        p2MmrLabel.setAlignment(Align.center);
        statsTable.add(p2MmrLabel).padBottom(10f).align(Align.center).row();

        statsTable.add(new Label("WINS", labelStyle)).padBottom(10f).align(Align.left);
        p1WinsLabel = new Label("0", redStyle);
        p1WinsLabel.setAlignment(Align.center);
        statsTable.add(p1WinsLabel).padBottom(10f).align(Align.center);
        p2WinsLabel = new Label("0", blueStyle);
        p2WinsLabel.setAlignment(Align.center);
        statsTable.add(p2WinsLabel).padBottom(10f).align(Align.center).row();

        statsTable.add(new Label("GOALS", labelStyle)).padBottom(10f).align(Align.left);
        p1GoalsLabel = new Label("0", redStyle);
        p1GoalsLabel.setAlignment(Align.center);
        statsTable.add(p1GoalsLabel).padBottom(10f).align(Align.center);
        p2GoalsLabel = new Label("0", blueStyle);
        p2GoalsLabel.setAlignment(Align.center);
        statsTable.add(p2GoalsLabel).padBottom(10f).align(Align.center).row();

        statsTable.add(new Label("SAVES", labelStyle)).padBottom(10f).align(Align.left);
        p1SavesLabel = new Label("0", redStyle);
        p1SavesLabel.setAlignment(Align.center);
        statsTable.add(p1SavesLabel).padBottom(10f).align(Align.center);
        p2SavesLabel = new Label("0", blueStyle);
        p2SavesLabel.setAlignment(Align.center);
        statsTable.add(p2SavesLabel).padBottom(10f).align(Align.center).row();

        TextButton backButton = MenuFactory.createTextButton(skin, "BACK");

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.setBackground(createGlassPanel());
        root.pad(30f);
        root.add(header).padBottom(20f).row();
        root.add(statsTable).padBottom(20f).row();
        root.add(backButton).width(220f).height(50f).row();
        stage.addActor(root);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.goToProfile();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        GameManager.getInstance().fetchUserStats(() -> {
            GameManager gm = GameManager.getInstance();
            p1MmrLabel.setText(String.valueOf(gm.mmr));
            p2MmrLabel.setText("-");
            p1WinsLabel.setText(String.valueOf(gm.totalWins));
            p2WinsLabel.setText("0");
            p1GoalsLabel.setText(String.valueOf(gm.totalGoals));
            p2GoalsLabel.setText("0");
            p1SavesLabel.setText(String.valueOf(gm.totalSaves));
            p2SavesLabel.setText("0");
        });
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
        p1MmrLabel.setText(String.valueOf(gm.mmr));
        p2MmrLabel.setText("-");
        p1WinsLabel.setText(String.valueOf(gm.totalWins));
        p2WinsLabel.setText("0");
        p1GoalsLabel.setText(String.valueOf(gm.totalGoals));
        p2GoalsLabel.setText("0");
        p1SavesLabel.setText(String.valueOf(gm.totalSaves));
        p2SavesLabel.setText("0");

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
        tableBgTex.dispose();
    }

    private Drawable createTableBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.7f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private Drawable createGlassPanel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.8f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
