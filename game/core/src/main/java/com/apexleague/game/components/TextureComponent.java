package com.apexleague.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TextureComponent implements Component {
    public TextureRegion region;
    public Vector2 scale = new Vector2(1f, 1f);
}

