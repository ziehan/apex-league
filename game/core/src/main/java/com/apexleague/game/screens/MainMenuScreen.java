package com.apexleague.game.screens;

import com.apexleague.game.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout layout = new GlyphLayout();

    public MainMenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.03f, 0.05f, 0.08f, 1f);

        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        layout.setText(game.font, "APEX LEAGUE");
        float titleX = (viewport.getWorldWidth() - layout.width) * 0.5f;
        float titleY = viewport.getWorldHeight() * 0.7f;
        game.font.draw(game.batch, layout, titleX, titleY);

        String prompt = "Press ENTER to Play";
        layout.setText(game.font, prompt);
        float promptX = (viewport.getWorldWidth() - layout.width) * 0.5f;
        float promptY = viewport.getWorldHeight() * 0.55f;
        game.font.draw(game.batch, layout, promptX, promptY);

        String exitPrompt = "Press ESC to Exit Game";
        layout.setText(game.font, exitPrompt);
        float exitX = (viewport.getWorldWidth() - layout.width) * 0.5f;
        float exitY = viewport.getWorldHeight() * 0.48f;
        game.font.draw(game.batch, layout, exitX, exitY);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new PlayScreen(game));
            dispose();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
