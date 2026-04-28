package com.apexleague.game.entities;

import com.apexleague.game.components.InputComponent;
import com.apexleague.game.components.JumpComponent;
import com.apexleague.game.components.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
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

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.8f, 1.4f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.3f;

        body.createFixture(fixtureDef);
        body.setLinearDamping(1.5f);
        body.setAngularDamping(4f);

        shape.dispose();

        Entity player = new Entity();
        player.add(new PhysicsComponent(body));
        player.add(new InputComponent());
        player.add(new JumpComponent(jumpForce));
        return player;
    }
}

