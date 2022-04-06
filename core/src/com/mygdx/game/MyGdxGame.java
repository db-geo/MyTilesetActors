package com.mygdx.game;

import com.badlogic.gdx.Game;

public class MyGdxGame extends Game {
    @Override
    public void create() {
        setScreen(new MyScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
    }
}
