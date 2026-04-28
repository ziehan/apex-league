package com.apexleague.game.commands;

import com.apexleague.game.components.InputComponent;

public class JumpCommand implements Command {
    @Override
    public void execute(InputComponent input) {
        input.jump = true;
    }
}

