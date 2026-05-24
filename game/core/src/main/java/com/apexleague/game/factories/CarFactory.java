package com.apexleague.game.factories;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public final class CarFactory {
    private CarFactory() {
    }

    public static Body createCarBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        attachCarFixtures(body);
        body.setLinearDamping(1.5f);
        body.setAngularDamping(4f);
        return body;
    }

    private static void attachCarFixtures(Body body) {
        float halfWidth = 0.512f;
        float halfHeight = 0.896f;
        float radius = halfWidth;
        float boxHalfHeight = halfHeight - radius;

        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(halfWidth, boxHalfHeight);

        CircleShape topShape = new CircleShape();
        topShape.setRadius(radius);
        topShape.setPosition(new Vector2(0f, boxHalfHeight));

        CircleShape bottomShape = new CircleShape();
        bottomShape.setRadius(radius);
        bottomShape.setPosition(new Vector2(0f, -boxHalfHeight));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.3f;

        fixtureDef.shape = boxShape;
        body.createFixture(fixtureDef).setUserData("PLAYER");
        fixtureDef.shape = topShape;
        body.createFixture(fixtureDef).setUserData("PLAYER");
        fixtureDef.shape = bottomShape;
        body.createFixture(fixtureDef).setUserData("PLAYER");

        boxShape.dispose();
        topShape.dispose();
        bottomShape.dispose();
    }
}

