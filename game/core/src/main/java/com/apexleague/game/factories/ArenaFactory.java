package com.apexleague.game.factories;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

public final class ArenaFactory {
    private static final float WORLD_WIDTH = 60f;
    private static final float WORLD_HEIGHT = 40f;
    private static final float WALL_RESTITUTION = 0.5f;
    private static final float WALL_CORNER_RADIUS = 4f;
    private static final int WALL_CORNER_SEGMENTS = 10;
    private static final float GOAL_GAP_HEIGHT = 8f;
    private static final float GOAL_HALF_GAP = GOAL_GAP_HEIGHT * 0.5f;
    private static final float GOAL_DEPTH = 4f;
    private static final float CENTER_CIRCLE_RADIUS = 5f;

    private ArenaFactory() {
    }

    public static void buildArena(World world) {
        createWalls(world);
        createGoalSensors(world);
        createCenterLineAndCircle(world);
    }

    private static void createWalls(World world) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        Body wallBody = world.createBody(bdef);

        Vector2[] vertices = buildArenaVertices();
        ChainShape shape = new ChainShape();
        shape.createLoop(vertices);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.restitution = WALL_RESTITUTION;
        wallBody.createFixture(fdef);
        shape.dispose();
    }

    private static Vector2[] buildArenaVertices() {
        List<Vector2> vertices = new ArrayList<>();
        float w = WORLD_WIDTH;
        float h = WORLD_HEIGHT;
        float r = WALL_CORNER_RADIUS;
        float midY = h * 0.5f;

        addPoint(vertices, r, 0f);
        addPoint(vertices, w - r, 0f);
        addArc(vertices, w - r, r, r, -90f, 0f, WALL_CORNER_SEGMENTS);
        addPoint(vertices, w, r);
        addPoint(vertices, w, midY - GOAL_HALF_GAP);
        addPoint(vertices, w + GOAL_DEPTH, midY - GOAL_HALF_GAP);
        addPoint(vertices, w + GOAL_DEPTH, midY + GOAL_HALF_GAP);
        addPoint(vertices, w, midY + GOAL_HALF_GAP);
        addPoint(vertices, w, h - r);
        addArc(vertices, w - r, h - r, r, 0f, 90f, WALL_CORNER_SEGMENTS);
        addPoint(vertices, w - r, h);
        addPoint(vertices, r, h);
        addArc(vertices, r, h - r, r, 90f, 180f, WALL_CORNER_SEGMENTS);
        addPoint(vertices, 0f, h - r);
        addPoint(vertices, 0f, midY + GOAL_HALF_GAP);
        addPoint(vertices, -GOAL_DEPTH, midY + GOAL_HALF_GAP);
        addPoint(vertices, -GOAL_DEPTH, midY - GOAL_HALF_GAP);
        addPoint(vertices, 0f, midY - GOAL_HALF_GAP);
        addPoint(vertices, 0f, r);
        addArc(vertices, r, r, r, 180f, 270f, WALL_CORNER_SEGMENTS);

        if (vertices.size() > 2 && isTooClose(vertices.get(0), vertices.get(vertices.size() - 1))) {
            vertices.remove(vertices.size() - 1);
        }

        return vertices.toArray(new Vector2[0]);
    }

    private static void addPoint(List<Vector2> vertices, float x, float y) {
        Vector2 next = new Vector2(x, y);
        int size = vertices.size();
        if (size > 0) {
            Vector2 last = vertices.get(size - 1);
            if (isTooClose(last, next)) {
                return;
            }
        }
        vertices.add(next);
    }

    private static boolean isTooClose(Vector2 a, Vector2 b) {
        return a.dst2(b) <= 0.005f * 0.005f;
    }

    private static void addArc(List<Vector2> vertices, float cx, float cy, float radius, float startDeg, float endDeg, int segments) {
        float step = (endDeg - startDeg) / segments;
        for (int i = 1; i <= segments; i++) {
            float angle = (startDeg + step * i) * MathUtils.degreesToRadians;
            addPoint(vertices, cx + MathUtils.cos(angle) * radius, cy + MathUtils.sin(angle) * radius);
        }
    }

    private static void createGoalSensors(World world) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(GOAL_DEPTH * 0.5f, GOAL_HALF_GAP);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        bdef.position.set(-GOAL_DEPTH, WORLD_HEIGHT * 0.5f);
        Body leftGoal = world.createBody(bdef);
        leftGoal.createFixture(fdef).setUserData("GOAL_LEFT");

        bdef.position.set(WORLD_WIDTH + GOAL_DEPTH, WORLD_HEIGHT * 0.5f);
        Body rightGoal = world.createBody(bdef);
        rightGoal.createFixture(fdef).setUserData("GOAL_RIGHT");

        shape.dispose();
    }

    private static void createCenterLineAndCircle(World world) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        EdgeShape line = new EdgeShape();
        line.set(WORLD_WIDTH * 0.5f, 0f, WORLD_WIDTH * 0.5f, WORLD_HEIGHT);
        FixtureDef lineDef = new FixtureDef();
        lineDef.shape = line;
        lineDef.isSensor = true;
        body.createFixture(lineDef);
        line.dispose();

        CircleShape circle = new CircleShape();
        circle.setRadius(CENTER_CIRCLE_RADIUS);
        circle.setPosition(new Vector2(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f));
        FixtureDef circleDef = new FixtureDef();
        circleDef.shape = circle;
        circleDef.isSensor = true;
        body.createFixture(circleDef);
        circle.dispose();
    }
}
