package com.apexleague.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.math.MathUtils;

public class HUD {
    public final Stage stage;
    private final Label scoreLabel;
    private final Label centerLabel;
    private final Label boostLabel;
    private final Label timerLabel;
    private final com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;
    private int boostValue = 0;

    public HUD() {
        stage = new Stage(new ScreenViewport());
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        scoreLabel = new Label("SCORE: 0 - 0", style);
        timerLabel = new Label("05:00", style);

        BitmapFont centerFont = new BitmapFont();
        centerFont.getData().setScale(2.5f);
        Label.LabelStyle centerStyle = new Label.LabelStyle(centerFont, Color.WHITE);
        centerLabel = new Label("", centerStyle);

        boostLabel = new Label("100", style);
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();

        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top();
        topTable.add(timerLabel).padTop(10f);
        topTable.row();
        topTable.add(scoreLabel).padTop(4f);
        topTable.row();
        topTable.add(centerLabel).padTop(6f);
        stage.addActor(topTable);

        Table boostTable = new Table();
        boostTable.setFillParent(true);
        boostTable.bottom().right();
        boostTable.add(boostLabel).padRight(16f).padBottom(12f);
        stage.addActor(boostTable);
    }

    public void update(int leftScore, int rightScore) {
        scoreLabel.setText("SCORE: " + leftScore + " - " + rightScore);
    }

    public void updateTimer(String text) {
        timerLabel.setText(text);
    }

    public void setCenterText(String text) {
        centerLabel.setText(text == null ? "" : text);
    }

    public void updateBoost(int boostValue) {
        this.boostValue = boostValue;
        boostLabel.setText(String.valueOf(boostValue));
    }

    public void drawBoostIndicator() {
        float width = stage.getViewport().getWorldWidth();
        float height = stage.getViewport().getWorldHeight();
        float centerX = width - 60f;
        float centerY = 40f;
        float radius = 26f;

        float t = MathUtils.clamp(boostValue / 100f, 0f, 1f);
        Color color = new Color(1f, 0.2f + 0.8f * t, 0f, 1f);
        shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(centerX, centerY, radius, 32);
        shapeRenderer.end();
    }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
