package com.apexleague.game.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoostManager {
    private final List<BoostPad> boostPads = new ArrayList<>();

    public static final class BoostPad {
        private final Vector2 position;
        private final float radius;
        private final boolean isLarge;
        private boolean isActive;
        private float cooldownTimer;

        private BoostPad(float x, float y, float radius, boolean isLarge) {
            this.position = new Vector2(x, y);
            this.radius = radius;
            this.isLarge = isLarge;
            this.isActive = true;
            this.cooldownTimer = 0f;
        }

        public Vector2 getPosition() {
            return position;
        }

        public float getRadius() {
            return radius;
        }

        public boolean isLarge() {
            return isLarge;
        }

        public boolean isActive() {
            return isActive;
        }

        public void deactivate(float cooldown) {
            isActive = false;
            cooldownTimer = cooldown;
        }

        private void update(float delta) {
            if (!isActive) {
                cooldownTimer -= delta;
                if (cooldownTimer <= 0f) {
                    cooldownTimer = 0f;
                    isActive = true;
                }
            }
        }
    }

    public void createBoostPads(World world, float width, float height) {
        createBoostPad(world, 4f, 4f, 1f, true);
        createBoostPad(world, width - 4f, 4f, 1f, true);
        createBoostPad(world, 4f, height - 4f, 1f, true);
        createBoostPad(world, width - 4f, height - 4f, 1f, true);
        createBoostPad(world, width * 0.5f, 4f, 1f, true);
        createBoostPad(world, width * 0.5f, height - 4f, 1f, true);

        float centerX = width * 0.5f;
        float centerY = height * 0.5f;
        float ring = 6f;
        createBoostPad(world, centerX + ring, centerY, 0.5f, false);
        createBoostPad(world, centerX - ring, centerY, 0.5f, false);
        createBoostPad(world, centerX, centerY + ring, 0.5f, false);
        createBoostPad(world, centerX, centerY - ring, 0.5f, false);
    }

    private void createBoostPad(World world, float x, float y, float radius, boolean isLarge) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x, y);
        Body body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        BoostPad pad = new BoostPad(x, y, radius, isLarge);
        body.createFixture(fdef).setUserData(pad);
        boostPads.add(pad);
        shape.dispose();
    }

    public void update(float delta) {
        for (BoostPad pad : boostPads) {
            pad.update(delta);
        }
    }

    public void renderPads(ShapeRenderer renderer, Matrix4 projectionMatrix) {
        renderer.setProjectionMatrix(projectionMatrix);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.YELLOW);
        for (BoostPad pad : boostPads) {
            if (pad.isActive()) {
                renderer.circle(pad.getPosition().x, pad.getPosition().y, pad.getRadius(), 24);
            }
        }
        renderer.end();
    }

    public List<BoostPad> getBoostPads() {
        return Collections.unmodifiableList(boostPads);
    }
}

