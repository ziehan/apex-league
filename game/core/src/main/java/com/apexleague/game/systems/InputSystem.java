package com.apexleague.game.systems;

import com.apexleague.game.commands.BoostCommand;
import com.apexleague.game.commands.Command;
import com.apexleague.game.commands.DriftCommand;
import com.apexleague.game.commands.JumpCommand;
import com.apexleague.game.commands.MoveCommand;
import com.apexleague.game.components.InputComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputSystem extends IteratingSystem {
    private static final Command JUMP_COMMAND = new JumpCommand();
    private static final Command DRIFT_COMMAND = new DriftCommand();
    private static final Command BOOST_COMMAND = new BoostCommand();

    private final ComponentMapper<InputComponent> inputMapper = ComponentMapper.getFor(InputComponent.class);

    public InputSystem() {
        super(Family.all(InputComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        InputComponent input = inputMapper.get(entity);
        input.moveX = 0f;
        input.moveY = 0f;
        input.jump = false;
        input.drift = false;
        input.boost = false;

        float moveY = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveY += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveY -= 1f;
        }

        float moveX = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX += 1f;
        }

        new MoveCommand(moveX, moveY).execute(input);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            JUMP_COMMAND.execute(input);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            DRIFT_COMMAND.execute(input);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
            BOOST_COMMAND.execute(input);
        }
    }
}

