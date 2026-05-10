package com.apexleague.game.screens;

import com.apexleague.game.components.PhysicsComponent;
import com.apexleague.game.entities.BallEntity;
import com.apexleague.game.entities.PlayerEntity;
import com.apexleague.game.factories.ArenaFactory;
import com.apexleague.game.managers.BoostManager;
import com.apexleague.game.physics.GameContactListener;
import com.apexleague.game.state.GameManager;
import com.apexleague.game.systems.InputSystem;
import com.apexleague.game.systems.JumpSystem;
import com.apexleague.game.systems.MovementSystem;
import com.apexleague.game.systems.SpriteRenderSystem;
import com.apexleague.game.ui.HUD;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class PlayScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 60f;
    private static final float WORLD_HEIGHT = 40f;
    private static final float PLAYER_JUMP_FORCE = 18f;
    private static final float BALL_LINEAR_DAMPING = 0.3f;
    private static final float WALL_RESTITUTION = 0.5f;
    private static final float WALL_CORNER_RADIUS = 4f;
    private static final int WALL_CORNER_SEGMENTS = 10;
    private static final float GOAL_GAP_HEIGHT = 8f;
    private static final float GOAL_HALF_GAP = GOAL_GAP_HEIGHT * 0.5f;
    private static final float GOAL_DEPTH = 4f;
    private static final float CENTER_CIRCLE_RADIUS = 5f;
    private static final float PLAYER_SPAWN_X = WORLD_WIDTH * 0.25f;
    private static final float BALL_SPAWN_X = WORLD_WIDTH * 0.5f;
    private static final float SPAWN_Y = WORLD_HEIGHT * 0.5f;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final World world;
    private final Engine engine;
    private final Box2DDebugRenderer b2dr;
    private final HUD hud;
    private final GameManager gameManager;
    private final List<Body> playerBodies = new ArrayList<>();
    private final ShapeRenderer padRenderer;
    private final SpriteRenderSystem spriteRenderSystem;
    private final BoostManager boostManager;
    private Body ballBody;
    private PhysicsComponent playerPhysics;
    private float shakeTimer = 0f;
    private float shakeIntensity = 0f;

    public PlayScreen() {
        Box2D.init();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batch = new SpriteBatch();
        world = new World(Vector2.Zero, true);
        engine = new Engine();
        b2dr = new Box2DDebugRenderer();
        hud = new HUD();
        gameManager = GameManager.getInstance();
        padRenderer = new ShapeRenderer();

        spriteRenderSystem = new SpriteRenderSystem(batch);
        spriteRenderSystem.setProcessing(false);
        engine.addSystem(spriteRenderSystem);

        boostManager = new BoostManager();

        ArenaFactory.buildArena(world);
        boostManager.createBoostPads(world, WORLD_WIDTH, WORLD_HEIGHT);
        world.setContactListener(new GameContactListener(gameManager, this, boostManager, hud));

        engine.addSystem(new InputSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new JumpSystem());

        Entity playerEntity = PlayerEntity.create(world, PLAYER_SPAWN_X, SPAWN_Y, PLAYER_JUMP_FORCE);
        engine.addEntity(playerEntity);
        playerPhysics = playerEntity.getComponent(PhysicsComponent.class);
        if (playerPhysics != null) {
            playerBodies.add(playerPhysics.body);
        }

        Entity ballEntity = BallEntity.create(world, BALL_SPAWN_X, SPAWN_Y, BALL_LINEAR_DAMPING);
        engine.addEntity(ballEntity);
        PhysicsComponent ballPhysics = ballEntity.getComponent(PhysicsComponent.class);
        if (ballPhysics != null) {
            ballBody = ballPhysics.body;
        }

        camera.position.set(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, 0f);
        camera.update();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.06f, 0.08f, 0.12f, 1f);

        if (!gameManager.isGameOver) {
            if (!gameManager.isOvertime && !gameManager.isResetting && !gameManager.isKickoff) {
                gameManager.matchTimer -= delta;
                if (gameManager.matchTimer <= 0f) {
                    gameManager.matchTimer = 0f;
                    if (gameManager.leftScore == gameManager.rightScore) {
                        gameManager.isOvertime = true;
                        if (!gameManager.isResetting) {
                            gameManager.startReset();
                        }
                    } else {
                        gameManager.isGameOver = true;
                        gameManager.winnerText = gameManager.leftScore > gameManager.rightScore ? "KIRI MENANG!" : "KANAN MENANG!";
                        if (hud != null) {
                            hud.setCenterText(gameManager.winnerText);
                        }
                    }
                }
            } else if (gameManager.isOvertime && !gameManager.isResetting && !gameManager.isKickoff) {
                gameManager.matchTimer += delta;
            }
        }

        if (gameManager.isResetting) {
            gameManager.resetTimer -= delta;
            if (gameManager.resetTimer <= 0f) {
                resetArena();
            }
        }

        if (gameManager.isKickoff) {
            gameManager.kickoffTimer -= delta;
            if (hud != null) {
                if (gameManager.kickoffTimer > 2f) {
                    hud.setCenterText("3");
                } else if (gameManager.kickoffTimer > 1f) {
                    hud.setCenterText("2");
                } else if (gameManager.kickoffTimer > 0f) {
                    hud.setCenterText("1");
                } else if (gameManager.kickoffTimer > -1f) {
                    hud.setCenterText("GO!");
                } else {
                    gameManager.isKickoff = false;
                    hud.setCenterText("");
                }
            }
        } else if (!gameManager.isResetting && !gameManager.isGameOver && hud != null) {
            hud.setCenterText("");
        }

        boostManager.update(delta);

        if (!gameManager.isGameOver) {
            engine.update(delta);
            world.step(1f / 60f, 6, 2);
        }

        viewport.apply();
        if (shakeTimer > 0f) {
            shakeTimer -= delta;
            camera.position.set(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, 0f);
            camera.position.add(
                MathUtils.random(-shakeIntensity, shakeIntensity),
                MathUtils.random(-shakeIntensity, shakeIntensity),
                0f
            );
            if (shakeTimer <= 0f) {
                camera.position.set(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, 0f);
            }
        } else {
            camera.position.set(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, 0f);
        }
        camera.update();
        b2dr.render(world, camera.combined);

        batch.setProjectionMatrix(camera.combined);
        spriteRenderSystem.update(delta);

        padRenderer.setProjectionMatrix(camera.combined);
        boostManager.renderPads(padRenderer, camera.combined);

        if (hud != null) {
            hud.update(gameManager.leftScore, gameManager.rightScore);
            if (playerPhysics != null) {
                hud.updateBoost(MathUtils.round(playerPhysics.boostAmount));
            }
            hud.updateTimer(formatTimer(gameManager.matchTimer, gameManager.isOvertime));
            hud.drawBoostIndicator();
            hud.stage.act(delta);
            hud.stage.draw();
        }
    }

    private String formatTimer(float time, boolean overtime) {
        int totalSeconds = Math.max(0, (int) Math.floor(time));
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String formatted = String.format("%02d:%02d", minutes, seconds);
        return overtime ? "OT " + formatted : formatted;
    }

    private void resetArena() {
        if (ballBody != null) {
            ballBody.setActive(true);
            ballBody.setTransform(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, 0f);
            ballBody.setLinearVelocity(0f, 0f);
            ballBody.setAngularVelocity(0f);
        }

        for (Body body : playerBodies) {
            body.setActive(true);
            float angle = 0f;
            if (ballBody != null) {
                angle = MathUtils.atan2(ballBody.getPosition().y - body.getPosition().y, ballBody.getPosition().x - body.getPosition().x);
            }
            body.setTransform(PLAYER_SPAWN_X, SPAWN_Y, angle);
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
        }

        gameManager.isResetting = false;
        gameManager.startKickoff();
        if (hud != null) {
            hud.setCenterText("");
        }
    }

    @Override
    public void dispose() {
        b2dr.dispose();
        world.dispose();
        batch.dispose();
        padRenderer.dispose();
        if (hud != null) {
            hud.dispose();
        }
    }

    public void triggerShake(float duration, float intensity) {
        shakeTimer = duration;
        shakeIntensity = intensity;
    }
}
