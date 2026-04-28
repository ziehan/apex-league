package com.apexleague.game.commands;

import com.apexleague.game.components.InputComponent;

public class MoveCommand implements Command {
    private final float moveX;
    private final float moveY;

    public MoveCommand(float moveX, float moveY) {
        this.moveX = moveX;
        this.moveY = moveY;
    }

    @Override
    public void execute(InputComponent input) {
        input.moveX = moveX;
        input.moveY = moveY;
    }
}

