package com.julio.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Collections;

import static com.julio.main.Board.fitViewport;
import static com.julio.main.Board.initialTime;
import static com.julio.main.Board.shufflerTiles;
import static com.julio.main.Constants.MARGIN;
import static com.julio.main.Constants.SHUFFLER_TILE_SIZE;
import static com.julio.main.Constants.ST_10P;
import static com.julio.main.Constants.WORLD_HEIGHT;
import static com.julio.main.Constants.WORLD_WIDTH;
import static com.julio.main.Tile.isBoardSwipable;
import static com.julio.main.Tile.mins;
import static com.julio.main.Tile.ones;
import static com.julio.main.Tile.tens;
import static com.julio.main.Tile.time;

public class NextLevel implements Screen {
    int width;
    int height;
    Vector2 position;
    Color color;


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
