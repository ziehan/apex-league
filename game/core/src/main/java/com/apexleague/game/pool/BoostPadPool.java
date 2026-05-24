package com.apexleague.game.pool;

import com.apexleague.game.managers.BoostManager;
import com.badlogic.gdx.utils.Pool;

public class BoostPadPool extends Pool<BoostManager.BoostPad> {
    @Override
    protected BoostManager.BoostPad newObject() {
        return new BoostManager.BoostPad();
    }
}

