package com.apexleague.game.components;

import com.badlogic.ashley.core.Component;

public class InputComponent implements Component {
    public float moveX;
    public float moveY;
    public boolean jump;
    public boolean drift;
    public boolean boost;
}

