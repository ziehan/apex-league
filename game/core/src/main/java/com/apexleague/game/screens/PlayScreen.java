package com.apexleague.game.screens;

import com.apexleague.game.entities.BallEntity;
import com.apexleague.game.entities.PlayerEntity;
import com.apexleague.game.systems.InputSystem;
import com.apexleague.game.systems.JumpSystem;
import com.apexleague.game.systems.MovementSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 60f;
    private static final float WORLD_HEIGHT = 40f;
    private static final float PLAYER_JUMP_FORCE = 18f;
    private static final float BALL_LINEAR_DAMPING = 0.3f;
    private static final float WALL_RESTITUTION = 0.5f;
    private static final float WALL_CORNER_RADIUS = 4f;
    private static final float GOAL_GAP_HEIGHT = 8f;
    private static final float GOAL_HALF_GAP = GOAL_GAP_HEIGHT * 0.5f;
    private static final float PLAYER_SPAWN_X = WORLD_WIDTH * 0.25f;
    private static final float BALL_SPAWN_X = WORLD_WIDTH * 0.5f;
    private static final float SPAWN_Y = WORLD_HEIGHT * 0.5f;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final World world;
    private final Engine engine;
    private final Box2DDebugRenderer b2dr;

    public PlayScreen() {
        Box2D.init();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = new SpriteBatch();
        world = new World(Vector2.Zero, true);
        engine = new Engine();
        b2dr = new Box2DDebugRenderer();

        createWalls();
        setupContactListener();

        engine.addSystem(new InputSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new JumpSystem());
        engine.addEntity(PlayerEntity.create(world, PLAYER_SPAWN_X, SPAWN_Y, PLAYER_JUMP_FORCE));
        engine.addEntity(BallEntity.create(world, BALL_SPAWN_X, SPAWN_Y, BALL_LINEAR_DAMPING));

        camera.position.set(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, 0f);
        camera.update();
    }

    private void createWalls() {
        float width = 60f;
        float height = 40f;
        float corner = 4f;
        float goalHalfHeight = 4f;
        float centerY = height / 2f;

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        Body wallBody = world.createBody(bdef);

        addEdge(wallBody, corner, height, width - corner, height);
        addEdge(wallBody, corner, 0, width - corner, 0);
        addEdge(wallBody, 0, corner, 0, centerY - goalHalfHeight);
        addEdge(wallBody, 0, centerY + goalHalfHeight, 0, height - corner);
        addEdge(wallBody, width, corner, width, centerY - goalHalfHeight);
        addEdge(wallBody, width, centerY + goalHalfHeight, width, height - corner);

        addCorner(wallBody, corner, corner, corner, 180, 270);
        addCorner(wallBody, width - corner, corner, corner, 270, 360);
        addCorner(wallBody, width - corner, height - corner, corner, 0, 90);
        addCorner(wallBody, corner, height - corner, corner, 90, 180);

        createGoalSensors(width, centerY, goalHalfHeight);
    }

    private void addEdge(Body body, float x1, float y1, float x2, float y2) {
        EdgeShape edge = new EdgeShape();
        edge.set(x1, y1, x2, y2);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = edge;
        fdef.restitution = 0.5f;
        body.createFixture(fdef);
        edge.dispose();
    }

    private void addCorner(Body body, float cx, float cy, float radius, float startAngle, float endAngle) {
        int segments = 10;
        float startRad = startAngle * com.badlogic.gdx.math.MathUtils.degreesToRadians;
        float endRad = endAngle * com.badlogic.gdx.math.MathUtils.degreesToRadians;
        float step = (endRad - startRad) / segments;

        for (int i = 0; i < segments; i++) {
            float theta1 = startRad + step * i;
            float theta2 = startRad + step * (i + 1);

            float x1 = cx + radius * com.badlogic.gdx.math.MathUtils.cos(theta1);
            float y1 = cy + radius * com.badlogic.gdx.math.MathUtils.sin(theta1);
            float x2 = cx + radius * com.badlogic.gdx.math.MathUtils.cos(theta2);
            float y2 = cy + radius * com.badlogic.gdx.math.MathUtils.sin(theta2);

            addEdge(body, x1, y1, x2, y2);
        }
    }

    private void createGoalSensors(float width, float centerY, float goalHalfHeight) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, goalHalfHeight);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        bdef.position.set(0, centerY);
        Body leftGoal = world.createBody(bdef);
        Fixture leftFixture = leftGoal.createFixture(fdef);
        leftFixture.setUserData("GOAL_LEFT");

        bdef.position.set(width, centerY);
        Body rightGoal = world.createBody(bdef);
        Fixture rightFixture = rightGoal.createFixture(fdef);
        rightFixture.setUserData("GOAL_RIGHT");

        shape.dispose();
    }

    private void setupContactListener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixA = contact.getFixtureA();
                Fixture fixB = contact.getFixtureB();

                Object dataA = fixA.getUserData();
                Object dataB = fixB.getUserData();

                if (dataA == null || dataB == null) return;

                checkGoal(dataA, dataB);
            }

            private void checkGoal(Object a, Object b) {
                if ((a.equals("BALL") && b.equals("GOAL_LEFT")) || (b.equals("BALL") && a.equals("GOAL_LEFT"))) {
                    System.out.println("========== GOL!!! ==========");
                    System.out.println("PLAYER KANAN MENCETAK ANGKA!");
                    System.out.println("============================");
                } else if ((a.equals("BALL") && b.equals("GOAL_RIGHT")) || (b.equals("BALL") && a.equals("GOAL_RIGHT"))) {
                    System.out.println("========== GOL!!! ==========");
                    System.out.println("PLAYER KIRI MENCETAK ANGKA!");
                    System.out.println("============================");
                }
            }

            @Override
            public void endContact(Contact contact) {}
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.06f, 0.08f, 0.12f, 1f);
        engine.update(delta);
        world.step(1f / 60f, 6, 2);

        camera.update();
        b2dr.render(world, camera.combined);
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
        b2dr.dispose();
        world.dispose();
        batch.dispose();
    }
}
