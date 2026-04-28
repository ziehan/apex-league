package com.apexleague.game.commands;

import com.apexleague.game.components.InputComponent;

public interface Command {
    void execute(InputComponent input);
}

