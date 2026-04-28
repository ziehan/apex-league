package com.apexleague.game.systems;

import com.apexleague.game.components.InputComponent;
import com.apexleague.game.components.PhysicsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MovementSystem extends IteratingSystem {
    private static final float THRUST_FORCE = 70f;
    private static final float BOOST_MULTIPLIER = 2f;
    private static final float FULL_GRIP = 1f;
    private static final float DRIFT_GRIP = 0.18f;
    private static final float STEER_FULL_SPEED = 8f;
    private static final float MAX_TURN_RATE = 3.2f;
    private static final float TURN_RESPONSE = 8f;
    private static final float MIN_STEER_SPEED = 0.5f;
    private static final float SUPERSONIC_SPEED = 20f;

    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<InputComponent> inputMapper = ComponentMapper.getFor(InputComponent.class);
    private final Vector2 rightAxis = new Vector2(1f, 0f);
    private final Vector2 forwardAxis = new Vector2(0f, 1f);
    private final Vector2 lateralVelocity = new Vector2();
    private final Vector2 forwardVelocity = new Vector2();
    private final Vector2 lateralImpulse = new Vector2();
    private final Vector2 force = new Vector2();

    public MovementSystem() {
        super(Family.all(PhysicsComponent.class, InputComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = physicsMapper.get(entity);
        InputComponent inputComponent = inputMapper.get(entity);
        Body body = physicsComponent.body;

        float thrustInput = inputComponent.moveY;
        float steerInput = inputComponent.moveX;
        float gripFactor = inputComponent.drift ? DRIFT_GRIP : FULL_GRIP;
        float thrustMultiplier = inputComponent.boost && thrustInput > 0f ? BOOST_MULTIPLIER : 1f;
        float thrustForce = thrustInput * THRUST_FORCE * thrustMultiplier;

        lateralImpulse.set(getLateralVelocity(body)).scl(-body.getMass() * gripFactor);
        body.applyLinearImpulse(lateralImpulse, body.getWorldCenter(), true);

        if (thrustInput != 0f) {
            force.set(0f, thrustForce).rotateRad(body.getAngle());
            body.applyForceToCenter(force, true);
        }

        float signedForwardSpeed = getForwardVelocity(body).dot(body.getWorldVector(forwardAxis));
        float absForwardSpeed = Math.abs(signedForwardSpeed);
        physicsComponent.isSupersonic = body.getLinearVelocity().len2() >= SUPERSONIC_SPEED * SUPERSONIC_SPEED;
        boolean canSteer = thrustInput != 0f || absForwardSpeed > MIN_STEER_SPEED;

        if (!canSteer || steerInput == 0f) {
            body.setAngularVelocity(0f);
            return;
        }

        float speedFactor = Math.min(1f, absForwardSpeed / STEER_FULL_SPEED);
        float directionFactor = signedForwardSpeed >= 0f ? 1f : -1f;
        float targetAngularVelocity = -steerInput * MAX_TURN_RATE * speedFactor * directionFactor;
        float angularVelocityDelta = targetAngularVelocity - body.getAngularVelocity();
        float torque = body.getInertia() * angularVelocityDelta * TURN_RESPONSE;
        body.applyTorque(torque, true);
    }

    private Vector2 getLateralVelocity(Body body) {
        Vector2 currentRightNormal = body.getWorldVector(rightAxis);
        return lateralVelocity.set(currentRightNormal).scl(currentRightNormal.dot(body.getLinearVelocity()));
    }

    private Vector2 getForwardVelocity(Body body) {
        Vector2 currentForwardNormal = body.getWorldVector(forwardAxis);
        return forwardVelocity.set(currentForwardNormal).scl(currentForwardNormal.dot(body.getLinearVelocity()));
    }
}

