package com.apexleague.game;

import com.apexleague.game.screens.PlayScreen;
import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    @Override
    public void create() {
        setScreen(new PlayScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
