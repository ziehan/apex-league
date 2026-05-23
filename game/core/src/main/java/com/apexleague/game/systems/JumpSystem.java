package com.apexleague.game.systems;

import com.apexleague.game.components.InputComponent;
import com.apexleague.game.components.JumpComponent;
import com.apexleague.game.components.PhysicsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class JumpSystem extends IteratingSystem {
    private static final float DEFAULT_JUMP_COOLDOWN = 1.5f;

    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<JumpComponent> jumpMapper = ComponentMapper.getFor(JumpComponent.class);
    private final ComponentMapper<InputComponent> inputMapper = ComponentMapper.getFor(InputComponent.class);
    private final Vector2 jumpDirection = new Vector2();
    private final Vector2 impulse = new Vector2();

    public JumpSystem() {
        super(Family.all(PhysicsComponent.class, JumpComponent.class, InputComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = physicsMapper.get(entity);
        JumpComponent jump = jumpMapper.get(entity);
        InputComponent input = inputMapper.get(entity);

        if (jump.jumpCooldown > 0f) {
            jump.jumpCooldown = Math.max(0f, jump.jumpCooldown - deltaTime);
            if (jump.jumpCooldown == 0f) {
                jump.isJumping = false;
            }
        }

        if (!input.jump) {
            return;
        }

        if (jump.isJumping || jump.jumpCooldown > 0f) {
            input.jump = false;
            return;
        }

        float dx = input.moveX;
        float dy = input.moveY;

        if (dx == 0f && dy == 0f) {
            dy = 1f;
        }

        jumpDirection.set(dx, dy).nor();
        Vector2 currentVel = physics.body.getLinearVelocity();
        physics.body.setLinearVelocity(currentVel.scl(0.3f).add(impulse.set(physics.body.getWorldVector(jumpDirection)).scl(8.75f)));
        physics.dashTimer = 0.4f;

        jump.isJumping = true;
        jump.jumpCooldown = DEFAULT_JUMP_COOLDOWN;
        input.jump = false;
    }
}
