package com.apexleague.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsComponent implements Component {
    public final Body body;
    public boolean isSupersonic = false;
    public float boostAmount = 33f;
    public float dashTimer = 0f;

    public PhysicsComponent(Body body) {
        this.body = body;
        this.isSupersonic = false;
        this.boostAmount = 33f;
        this.dashTimer = 0f;
    }
}
