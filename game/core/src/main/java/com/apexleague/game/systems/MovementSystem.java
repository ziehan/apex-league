package com.apexleague.game.systems;

import com.apexleague.game.components.InputComponent;
import com.apexleague.game.components.PhysicsComponent;
import com.apexleague.game.state.GameManager;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MovementSystem extends IteratingSystem {
    private static final float THRUST_FORCE = 35f;
    private static final float BOOST_MULTIPLIER = 2f;
    private static final float FULL_GRIP = 0.98f;
    private static final float DRIFT_GRIP = 0.05f;
    private static final float STEER_FULL_SPEED = 5f;
    private static final float MAX_TURN_RATE = 3.2f;
    private static final float TURN_RESPONSE = 8f;
    private static final float TORQUE_FORCE = 40f;
    private static final float MIN_STEER_SPEED = 0.5f;
    private static final float SUPERSONIC_THRESHOLD = 8f;
    private static final float BOOST_DRAIN_PER_SEC = 30f;
    private static final float DRIFT_FORWARD_FORCE = 8f;

    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<InputComponent> inputMapper = ComponentMapper.getFor(InputComponent.class);
    private final GameManager gameManager = GameManager.getInstance();
    private final Vector2 rightAxis = new Vector2(1f, 0f);
    private final Vector2 forwardAxis = new Vector2(0f, 1f);
    private final Vector2 lateralVelocity = new Vector2();
    private final Vector2 forwardVelocity = new Vector2();
    private final Vector2 lateralImpulse = new Vector2();
    private final Vector2 force = new Vector2();
    private final Vector2 driftForce = new Vector2();

    public MovementSystem() {
        super(Family.all(PhysicsComponent.class, InputComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = physicsMapper.get(entity);
        if (physicsComponent.dashTimer > 0f) {
            physicsComponent.dashTimer -= deltaTime;
            return;
        }

        InputComponent inputComponent = inputMapper.get(entity);
        Body body = physicsComponent.body;

        boolean inputLocked = gameManager.isKickoff;
        boolean drifting = !inputLocked && inputComponent.drift;
        float thrustInput = inputLocked ? 0f : inputComponent.moveY;
        float steerInput = inputLocked ? 0f : inputComponent.moveX;
        float gripFactor = drifting ? DRIFT_GRIP : FULL_GRIP;

        if (drifting) {
            body.setAngularDamping(1.5f);
        } else {
            body.setAngularDamping(8f);
        }

        boolean boostActive = !inputLocked && inputComponent.boost && thrustInput > 0f && physicsComponent.boostAmount > 0f;
        float thrustMultiplier = boostActive ? BOOST_MULTIPLIER : 1f;
        float thrustForce = thrustInput * THRUST_FORCE * thrustMultiplier;

        if (boostActive) {
            physicsComponent.boostAmount = Math.max(0f, physicsComponent.boostAmount - BOOST_DRAIN_PER_SEC * deltaTime);
        }

        lateralImpulse.set(getLateralVelocity(body)).scl(-body.getMass() * gripFactor);
        body.applyLinearImpulse(lateralImpulse, body.getWorldCenter(), true);

        if (drifting) {
            driftForce.set(0f, DRIFT_FORWARD_FORCE).rotateRad(body.getAngle());
            body.applyForceToCenter(driftForce, true);
        }

        if (thrustInput != 0f) {
            force.set(0f, thrustForce).rotateRad(body.getAngle());
            body.applyForceToCenter(force, true);
        }

        float signedForwardSpeed = getForwardVelocity(body).dot(body.getWorldVector(forwardAxis));
        float absForwardSpeed = Math.abs(signedForwardSpeed);
        boolean shouldBeSupersonic = !inputLocked && thrustInput > 0f && body.getLinearVelocity().len() >= SUPERSONIC_THRESHOLD;
        boolean wasSupersonic = physicsComponent.isSupersonic;
        if (!wasSupersonic && shouldBeSupersonic) {
            System.out.println("SUPERSONIC ACTIVE!");
        }
        physicsComponent.isSupersonic = shouldBeSupersonic;
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
        torque = Math.signum(torque) * Math.min(Math.abs(torque), TORQUE_FORCE);
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
