package com.apexleague.game.entities;

import com.apexleague.game.components.InputComponent;
import com.apexleague.game.components.JumpComponent;
import com.apexleague.game.components.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public final class PlayerEntity {
    private PlayerEntity() {
    }

    public static Entity create(World world, float x, float y, float jumpForce) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

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

        body.setLinearDamping(1.5f);
        body.setAngularDamping(4f);

        boxShape.dispose();
        topShape.dispose();
        bottomShape.dispose();

        Entity player = new Entity();
        PhysicsComponent physicsComponent = new PhysicsComponent(body);
        player.add(physicsComponent);
        player.add(new InputComponent());
        player.add(new JumpComponent(jumpForce));
        body.setUserData(physicsComponent);
        return player;
    }
}
