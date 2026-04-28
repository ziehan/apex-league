package com.apexleague.game.commands;

import com.apexleague.game.components.InputComponent;

public class BoostCommand implements Command {
    @Override
    public void execute(InputComponent input) {
        input.boost = true;
    }
}

