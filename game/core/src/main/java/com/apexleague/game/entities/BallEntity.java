package com.apexleague.game.entities;

import com.apexleague.game.components.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public final class BallEntity {
    private static final float BALL_RADIUS = 0.75f;

    private BallEntity() {
    }

    public static Entity create(World world, float x, float y, float linearDamping) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(BALL_RADIUS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.8f;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.2f;

        Fixture ballFixture = body.createFixture(fixtureDef);
        ballFixture.setUserData("BALL");

        body.setLinearDamping(linearDamping);

        shape.dispose();

        Entity ball = new Entity();
        ball.add(new PhysicsComponent(body));
        return ball;
    }
}
