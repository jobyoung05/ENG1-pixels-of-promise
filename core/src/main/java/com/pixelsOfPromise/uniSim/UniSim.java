package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class UniSim extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}