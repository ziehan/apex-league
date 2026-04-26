package com.apexleague.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 32f;
    private static final float WORLD_HEIGHT = 18f;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final World world;

    public PlayScreen() {
        Box2D.init();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = new SpriteBatch();
        world = new World(Vector2.Zero, true);
        camera.position.set(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, 0f);
        camera.update();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.06f, 0.08f, 0.12f, 1f);
        world.step(1f / 60f, 6, 2);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
    }
}

