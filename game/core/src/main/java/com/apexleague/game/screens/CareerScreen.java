package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.apexleague.game.state.GameManager;
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

    private final Label winRateLabel;
    private final Label matchPlayedLabel;
    private final Label totalGoalsLabel;
    private final Label totalBackwardGoalsLabel;
    private final Label totalSavesLabel;
    private final Label totalDemosLabel;

    public CareerScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = MenuFactory.createDefaultSkin();
        pitchTex = new Texture("images/football_pitch.png");
        tableBgDrawable = createTableBackground();
        tableBgTex = ((TextureRegionDrawable) tableBgDrawable).getRegion().getTexture();

        Label header = new Label("CAREER STATS", skin);
        header.setColor(Color.GOLD);
        header.setFontScale(1.5f);

        Table statsTable = new Table();
        statsTable.setBackground(tableBgDrawable);
        statsTable.pad(30f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);
        Label.LabelStyle highlightStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.CYAN);

        // WIN RATE
        statsTable.add(new Label("WIN RATE (%)", labelStyle)).padBottom(10f).align(Align.left);
        winRateLabel = new Label("0", highlightStyle);
        winRateLabel.setAlignment(Align.center);
        statsTable.add(winRateLabel).padBottom(10f).align(Align.center).row();

        // MATCH PLAYED
        statsTable.add(new Label("MATCH PLAYED", labelStyle)).padBottom(10f).align(Align.left);
        matchPlayedLabel = new Label("0", highlightStyle);
        matchPlayedLabel.setAlignment(Align.center);
        statsTable.add(matchPlayedLabel).padBottom(10f).align(Align.center).row();

        // GOALS
        statsTable.add(new Label("GOALS", labelStyle)).padBottom(10f).align(Align.left);
        totalGoalsLabel = new Label("0", highlightStyle);
        totalGoalsLabel.setAlignment(Align.center);
        statsTable.add(totalGoalsLabel).padBottom(10f).align(Align.center).row();

        // BACKWARD GOALS
        statsTable.add(new Label("BACKWARD GOALS", labelStyle)).padBottom(10f).align(Align.left);
        totalBackwardGoalsLabel = new Label("0", highlightStyle);
        totalBackwardGoalsLabel.setAlignment(Align.center);
        statsTable.add(totalBackwardGoalsLabel).padBottom(10f).align(Align.center).row();

        // SAVES
        statsTable.add(new Label("SAVES", labelStyle)).padBottom(10f).align(Align.left);
        totalSavesLabel = new Label("0", highlightStyle);
        totalSavesLabel.setAlignment(Align.center);
        statsTable.add(totalSavesLabel).padBottom(10f).align(Align.center).row();

        // DEMOS
        statsTable.add(new Label("DEMOS", labelStyle)).padBottom(10f).align(Align.left);
        totalDemosLabel = new Label("0", highlightStyle);
        totalDemosLabel.setAlignment(Align.center);
        statsTable.add(totalDemosLabel).padBottom(10f).align(Align.center).row();

        TextButton backButton = new TextButton("BACK", skin);

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
                game.setScreen(new ProfileScreen(game));
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
        matchPlayedLabel.setText(String.valueOf(gm.totalMatches));
        totalGoalsLabel.setText(String.valueOf(gm.totalGoals));
        totalBackwardGoalsLabel.setText(String.valueOf(gm.totalBackwardGoals));
        totalSavesLabel.setText(String.valueOf(gm.totalSaves));
        totalDemosLabel.setText(String.valueOf(gm.totalDemos));

        float winRate = gm.totalMatches > 0 ? (float) (gm.totalP1Wins + gm.totalP2Wins) / gm.totalMatches * 100f : 0f;
        winRateLabel.setText(String.format("%.1f", winRate));

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
