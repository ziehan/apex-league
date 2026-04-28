package com.apexleague.game.commands;

import com.apexleague.game.components.InputComponent;

public class DriftCommand implements Command {
    @Override
    public void execute(InputComponent input) {
        input.drift = true;
    }
}

