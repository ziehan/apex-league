package com.apexleague.game.screens;

import com.apexleague.game.Main;
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
import com.apexleague.game.ui.MenuFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
    private final List<PhysicsComponent> playerPhysicsList = new ArrayList<>();
    private final SpriteRenderSystem spriteRenderSystem;
    private final BoostManager boostManager;
    private final Texture pitchTex;
    private final Texture redCarTex;
    private final Texture blueCarTex;
    private Body ballBody;
    private PhysicsComponent leftPlayerPhysics;
    private PhysicsComponent rightPlayerPhysics;
    private float shakeTimer = 0f;
    private float shakeIntensity = 0f;
    private boolean isDebug = false;

    private final Main game;
    private final Stage pauseStage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin pauseSkin;

    public PlayScreen(Main game) {
        this.game = game;
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
        pitchTex = new Texture("images/football_pitch.png");
        redCarTex = new Texture("images/red_car.png");
        blueCarTex = new Texture("images/blue_car.png");

        pauseStage = new Stage(new ScreenViewport());
        pauseSkin = MenuFactory.createDefaultSkin();
        buildPauseMenu();

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

        Entity leftPlayerEntity = PlayerEntity.create(
            world,
            WORLD_WIDTH * 0.25f,
            SPAWN_Y,
            PLAYER_JUMP_FORCE,
            new TextureRegion(redCarTex),
            1
        );
        engine.addEntity(leftPlayerEntity);
        leftPlayerPhysics = leftPlayerEntity.getComponent(PhysicsComponent.class);
        if (leftPlayerPhysics != null) {
            playerPhysicsList.add(leftPlayerPhysics);
        }

        Entity rightPlayerEntity = PlayerEntity.create(
            world,
            WORLD_WIDTH * 0.75f,
            SPAWN_Y,
            PLAYER_JUMP_FORCE,
            new TextureRegion(blueCarTex),
            2
        );
        engine.addEntity(rightPlayerEntity);
        rightPlayerPhysics = rightPlayerEntity.getComponent(PhysicsComponent.class);
        if (rightPlayerPhysics != null) {
            playerPhysicsList.add(rightPlayerPhysics);
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            isDebug = !isDebug;
        }

        if (!gameManager.isGameOver && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameManager.isPaused = !gameManager.isPaused;
            if (gameManager.isPaused) {
                Gdx.input.setInputProcessor(pauseStage);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }

        if (gameManager.isGameOver) {
            gameManager.isPaused = false;
            Gdx.input.setInputProcessor(null);
            game.setScreen(new GameOverScreen(game));
            return;
        }

        if (!gameManager.isPaused) {
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
                handlePendingDemolitions();
            }
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
        if (isDebug) {
            b2dr.render(world, camera.combined);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(pitchTex, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        boostManager.renderPads(batch);
        spriteRenderSystem.update(delta);
        batch.end();

        if (hud != null) {
            hud.update(gameManager.leftScore, gameManager.rightScore);
            int leftBoost = leftPlayerPhysics != null ? MathUtils.round(leftPlayerPhysics.boostAmount) : 0;
            int rightBoost = rightPlayerPhysics != null ? MathUtils.round(rightPlayerPhysics.boostAmount) : 0;
            hud.updateBoosts(leftBoost, rightBoost);
            hud.updateTimer(formatTimer(gameManager.matchTimer, gameManager.isOvertime));
            hud.drawBoostIndicator();
            hud.stage.act(delta);
            hud.stage.draw();
        }

        if (gameManager.isPaused && !gameManager.isGameOver) {
            pauseStage.act(delta);
            pauseStage.draw();
            return;
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

        if (leftPlayerPhysics != null) {
            Body body = leftPlayerPhysics.body;
            body.setActive(true);
            body.setTransform(WORLD_WIDTH * 0.25f, WORLD_HEIGHT * 0.5f, -MathUtils.PI / 2f);
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
            leftPlayerPhysics.boostAmount = 33f;
            leftPlayerPhysics.pendingDemolition = false;
        }

        if (rightPlayerPhysics != null) {
            Body body = rightPlayerPhysics.body;
            body.setActive(true);
            body.setTransform(WORLD_WIDTH * 0.75f, WORLD_HEIGHT * 0.5f, MathUtils.PI / 2f);
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
            rightPlayerPhysics.boostAmount = 33f;
            rightPlayerPhysics.pendingDemolition = false;
        }

        gameManager.isResetting = false;
        gameManager.startKickoff();
        if (hud != null) {
            hud.setCenterText("");
        }
    }

    private void handlePendingDemolitions() {
        for (PhysicsComponent physics : playerPhysicsList) {
            if (physics == null || !physics.pendingDemolition) {
                continue;
            }

            Body body = physics.body;
            if (physics == leftPlayerPhysics) {
                body.setTransform(WORLD_WIDTH * 0.15f, WORLD_HEIGHT * 0.5f, -MathUtils.PI / 2f);
            } else if (physics == rightPlayerPhysics) {
                body.setTransform(WORLD_WIDTH * 0.85f, WORLD_HEIGHT * 0.5f, MathUtils.PI / 2f);
            }
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
            physics.pendingDemolition = false;
        }
    }

    private void buildPauseMenu() {
        Label title = new Label("PAUSED", pauseSkin);
        TextButton resumeButton = new TextButton("RESUME", pauseSkin);
        TextButton restartButton = new TextButton("RESTART MATCH", pauseSkin);
        TextButton menuButton = new TextButton("MAIN MENU", pauseSkin);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.setBackground(MenuFactory.createPanelDrawable(pauseSkin, new Color(0f, 0f, 0f, 0.6f)));
        table.add(title).padBottom(20f).row();
        table.add(resumeButton).width(320f).height(56f).padBottom(12f).row();
        table.add(restartButton).width(320f).height(56f).padBottom(12f).row();
        table.add(menuButton).width(320f).height(56f);
        pauseStage.addActor(table);

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                gameManager.isPaused = false;
                Gdx.input.setInputProcessor(null);
            }
        });

        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                gameManager.leftScore = 0;
                gameManager.rightScore = 0;
                gameManager.matchTimer = 300f;
                gameManager.isOvertime = false;
                gameManager.isGameOver = false;
                gameManager.winnerText = "";
                gameManager.isResetting = false;
                gameManager.resetTimer = 0f;
                gameManager.isPaused = false;
                resetArena();
                Gdx.input.setInputProcessor(null);
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                gameManager.isPaused = false;
                Gdx.input.setInputProcessor(null);
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    public float getWorldWidth() {
        return WORLD_WIDTH;
    }

    public int resolvePlayerId(Body body) {
        if (leftPlayerPhysics != null && leftPlayerPhysics.body == body) {
            return 1;
        }
        if (rightPlayerPhysics != null && rightPlayerPhysics.body == body) {
            return 2;
        }
        return 0;
    }

    @Override
    public void dispose() {
        b2dr.dispose();
        world.dispose();
        batch.dispose();
        boostManager.dispose();
        pitchTex.dispose();
        redCarTex.dispose();
        blueCarTex.dispose();
        pauseStage.dispose();
        pauseSkin.dispose();
        if (hud != null) {
            hud.dispose();
        }
    }

    public void triggerShake(float duration, float intensity) {
        shakeTimer = duration;
        shakeIntensity = intensity;
    }
}
