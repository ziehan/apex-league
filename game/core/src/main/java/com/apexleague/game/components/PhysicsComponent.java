package com.apexleague.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsComponent implements Component {
    public final Body body;
    public boolean isSupersonic;

    public PhysicsComponent(Body body) {
        this.body = body;
        this.isSupersonic = false;
    }
}

