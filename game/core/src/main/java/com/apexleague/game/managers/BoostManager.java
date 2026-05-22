package com.apexleague.game.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private final Texture bigPadTex = new Texture("images/big_boost_pad.png");
    private final Texture bigBoostTex = new Texture("images/big_boost.png");
    private final Texture smallPadTex = new Texture("images/small_boost_pad.png");
    private final Texture smallBoostTex = new Texture("images/small_boost.png");

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

    public void renderPads(SpriteBatch batch) {
        for (BoostPad pad : boostPads) {
            Texture padTex = pad.isLarge() ? bigPadTex : smallPadTex;
            Texture boostTex = pad.isLarge() ? bigBoostTex : smallBoostTex;
            float size = pad.getRadius() * 2f;
            float x = pad.getPosition().x - pad.getRadius();
            float y = pad.getPosition().y - pad.getRadius();
            batch.draw(padTex, x, y, size, size);
            if (pad.isActive()) {
                batch.draw(boostTex, x, y, size, size);
            }
        }
    }

    public void dispose() {
        bigPadTex.dispose();
        bigBoostTex.dispose();
        smallPadTex.dispose();
        smallBoostTex.dispose();
    }

    public List<BoostPad> getBoostPads() {
        return Collections.unmodifiableList(boostPads);
    }
}
