package com.julio.main;

import com.badlogic.gdx.Game;

public class Gameplay extends Game {
    Game game;
    public Gameplay() {
        game = this;
    }
    @Override
    public void create() {
        setScreen(new Board(game));
    }
}
