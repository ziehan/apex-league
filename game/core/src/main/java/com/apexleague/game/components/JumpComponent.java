package com.apexleague.game.components;

import com.badlogic.ashley.core.Component;

public class JumpComponent implements Component {
    public boolean isJumping;
    public float jumpCooldown;
    public float jumpForce;

    public JumpComponent(float jumpForce) {
        this.jumpForce = jumpForce;
        this.isJumping = false;
        this.jumpCooldown = 0f;
    }
}

