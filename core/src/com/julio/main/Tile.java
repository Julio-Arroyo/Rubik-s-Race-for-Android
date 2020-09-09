package com.julio.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;

import static com.julio.main.AssetLoader.preferences;
import static com.julio.main.Board.emptySpace;
import static com.julio.main.Board.fitViewport;
import static com.julio.main.Board.getFlickedTile;
import static com.julio.main.Board.getShufflerTile;
import static com.julio.main.Board.initialTime;
import static com.julio.main.Board.isPlaying;
import static com.julio.main.Board.newShuffle;
import static com.julio.main.Board.shuffler;
import static com.julio.main.Board.shufflerTiles;
import static com.julio.main.Board.tiles;
import static com.julio.main.Constants.MARGIN;
import static com.julio.main.Constants.ST_10P;
import static com.julio.main.Constants.dt;

public class Tile extends InputAdapter {
    Vector2 position, flickStart, flickEnd, flickedTileStartIndex, flickedTileStartPos, velocity, acceleration;
    Color color;
    boolean isFlicking = false;
    float swipeTime;
    float x_i;
    String name;
    static int ones = 0;
    static int tens = 0;
    static int mins = 0;
    static float time = 0;
    float startXSaver;
    static boolean isBoardSwipable = true;

    Gameplay gameplay = new Gameplay();
    //ArrayList<Tile> movingTiles = new ArrayList<Tile>();

    public Tile(Vector2 p, Color c, String n) {
        position = p;
        color = c;
        velocity = new Vector2(0,0);
        acceleration = new Vector2(0,0);
        name = n;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 worldClick = fitViewport.unproject(new Vector2(screenX, screenY));
        if (isBoardSwipable && ((worldClick.x >= 7 && worldClick.x <= 107) && (worldClick.y >= 7 && worldClick.y <= 107))) {
            isFlicking = true;
            flickStart = new Vector2(worldClick.x, worldClick.y);
            flickedTileStartIndex = new Vector2((float) ((int) (worldClick.x - 7) / 20), (float) ((int) (worldClick.y - 7) / 20));
            System.out.println("flickedTileStartIndex: (" + flickedTileStartIndex.x + "," + flickedTileStartIndex.y + ")");
        } else if (!isBoardSwipable && ((worldClick.x >= 42f && worldClick.x <=72f) && (worldClick.y >= 76.3333f && worldClick.y <=106.3333f))) {
            isBoardSwipable = true;
            initialTime = TimeUtils.nanoTime();
            newShuffle();
            time = 0;
            ones = 0;
            tens = 0;
            mins = 0;
        } /*else if (!isBoardSwipable && ((worldClick.x >= 25.5f && worldClick.x <=41.5f) && (worldClick.y >= 6.5f && worldClick.y <=22.5f))) {
            game.setScreen(new SettingsScreen(game));
        }*/
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (isFlicking) {
            isFlicking = false;
            flickEnd = fitViewport.unproject(new Vector2(screenX, screenY));
            Vector2 flickVector = new Vector2(flickEnd.x - flickStart.x, flickEnd.y - flickStart.y);
            int tilesToFlickNum;
            if (Math.abs(flickVector.x) > Math.abs(flickVector.y)) {
                if ((emptySpace.y == flickedTileStartIndex.y && emptySpace.x > flickedTileStartIndex.x) && flickVector.x > 10) {
                    tilesToFlickNum = (int) (emptySpace.x - flickedTileStartIndex.x);
                    for (int i = tilesToFlickNum - 1; i >= 0 ; i--) {
                        //Board.getFlickedTile(new Vector2(20 * (flickedTileStartPos.x + i), 20 * (flickedTileStartPos.y))).velocity.x += 10;
                        flickedTileStartPos = new Vector2(20 * (flickedTileStartIndex.x + i) + 7, 20 * (flickedTileStartIndex.y) + 7);
                        //swipeTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - initialTime);
                        //movingTiles.add(Board.getFlickedTile(flickedTileStartPos));
                        getFlickedTile(flickedTileStartPos).velocity.x = 480;
                        getFlickedTile(flickedTileStartPos).acceleration.x = 1000f;
                        //System.out.println("Color: " + Board.getFlickedTile(flickedTileStartPos).color + ". Position: (" + Board.getFlickedTile(flickedTileStartPos).position.x + ", " + Board.getFlickedTile(flickedTileStartPos).position.y + ")");
                    }
                    Board.manager.get("audio/swipe_right.wav", Sound.class).play();
                    emptySpace.x = flickedTileStartIndex.x;
                    emptySpace.y = flickedTileStartIndex.y;
                    System.out.println("Empty space index: (" + emptySpace.x + "," + emptySpace.y + ")");
                    x_i = flickedTileStartPos.x;
                    //System.out.println("x_i: " + x_i);
                }
                else if ((emptySpace.y == flickedTileStartIndex.y && emptySpace.x < flickedTileStartIndex.x) && flickVector.x < -10) {
                    tilesToFlickNum = Math.abs((int) ((emptySpace.x - flickedTileStartIndex.x)));
                    for (int i = tilesToFlickNum - 1; i >= 0 ; i--) {
                        //Board.getFlickedTile(new Vector2(flickedTileStartPos.x + i, flickedTileStartPos.y)).velocity.x -= flickVector.x * Constants.FLICK_MULTIPLIER;
                        flickedTileStartPos = new Vector2(20 * (flickedTileStartIndex.x - i) + 7, 20 * (flickedTileStartIndex.y) + 7);
                        //Board.getFlickedTile(flickedTileStartPos).position.x -= 20;
                        getFlickedTile(flickedTileStartPos).velocity.x = -480;
                        getFlickedTile(flickedTileStartPos).acceleration.x = -1000f;
                    }
                    Board.manager.get("audio/swipe_left.wav", Sound.class).play();
                    emptySpace.x = flickedTileStartIndex.x;
                    emptySpace.y = flickedTileStartIndex.y;
                    System.out.println("Empty space index: (" + emptySpace.x + "," + emptySpace.y + ")");
                }
            }
            else if (Math.abs(flickVector.y) > Math.abs(flickVector.x)) {
                if ((emptySpace.x == flickedTileStartIndex.x && emptySpace.y > flickedTileStartIndex.y) && flickVector.y > 10) {
                    tilesToFlickNum = (int) ((emptySpace.y - flickedTileStartIndex.y));
                    for (int i = tilesToFlickNum - 1; i >= 0 ; i--) {
                        //Board.getFlickedTile(new Vector2(flickedTileStartIndex.x, flickedTileStartIndex.y + i)).velocity.y += flickVector.y * Constants.FLICK_MULTIPLIER;
                        flickedTileStartPos = new Vector2(20 * (flickedTileStartIndex.x) + 7, 20 * (flickedTileStartIndex.y + i) + 7);
                        //Board.getFlickedTile(flickedTileStartPos).position.y += 20;
                        getFlickedTile(flickedTileStartPos).velocity.y = 480;
                        getFlickedTile(flickedTileStartPos).acceleration.y = 1000f;
                    }
                    Board.manager.get("audio/swipe_up.wav", Sound.class).play();
                    emptySpace.x = flickedTileStartIndex.x;
                    emptySpace.y = flickedTileStartIndex.y;
                    System.out.println("Empty space index: (" + emptySpace.x + "," + emptySpace.y + ")");
                }
                else if ((emptySpace.x == flickedTileStartIndex.x && emptySpace.y < flickedTileStartIndex.y) && flickVector.y < -10) {
                    tilesToFlickNum = Math.abs((int) ((emptySpace.y - flickedTileStartIndex.y)));
                    for (int i = tilesToFlickNum - 1; i >= 0 ; i--) {
                        //Board.getFlickedTile(new Vector2(flickedTileStartIndex.x + i, flickedTileStartIndex.y + i)).velocity.y -= flickVector.y * Constants.FLICK_MULTIPLIER;
                        flickedTileStartPos = new Vector2(20 * (flickedTileStartIndex.x) + 7, 20 * (flickedTileStartIndex.y - i) + 7);
                        //Board.getFlickedTile(flickedTileStartPos).position.y -= 20;
                        getFlickedTile(flickedTileStartPos).velocity.y = -480;
                        getFlickedTile(flickedTileStartPos).acceleration.y = -1000f;
                    }
                    Board.manager.get("audio/swipe_down.wav", Sound.class).play();
                    emptySpace.x = flickedTileStartIndex.x;
                    emptySpace.y = flickedTileStartIndex.y;
                    System.out.println("Empty space index: (" + emptySpace.x + "," + emptySpace.y + ")");
                }
            }
        }
        String tile1 = getFlickedTile( new Vector2((0 + 1) * 20 + MARGIN + 2, (0 + 1) * 20 + MARGIN + 2)).name;
        String tile2 = getShufflerTile( new Vector2(0 * 13.3333f + MARGIN + ST_10P, 0 * 13.3333f + 124 + ST_10P)).name;
        System.out.println("Tile lower left tablero: " + tile1);
        System.out.println("Tile lower left shuffler: " + tile2);
        System.out.println("is playing? " + isPlaying());
        return true;
    }

    public void update(float delta) {
        if (Board.fakeTile.velocity.x != 0 || Board.fakeTile.velocity.y != 0) {
            Board.fakeTile.velocity.x = 0;
            Board.fakeTile.velocity.y = 0;
        }

        if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - initialTime) <= 2) {
            Board.manager.get("audio/shuffler.wav", Music.class).play();
        }

        if (!(isPlaying())) {
            if (preferences.getInteger("minMins") == 0 && ((preferences.getInteger("minTens") == 0 && preferences.getInteger("minOnes") == 0) && preferences.getInteger("minTime") == 0 )) {
                preferences.putInteger("minMins", mins);
                preferences.putInteger("minTens", tens);
                preferences.putInteger("minOnes", ones);
                preferences.putInteger("minTime", (int) (time * 100));
                preferences.flush();
            }
            else if (mins < preferences.getInteger("minMins")) {
                preferences.putInteger("minMins", mins);
                preferences.putInteger("minTens", tens);
                preferences.putInteger("minOnes", ones);
                preferences.putInteger("minTime", (int) (time * 100));
                preferences.flush();
            }
            else if (mins == preferences.getInteger("minMins")) {
                if (tens < preferences.getInteger("minTens")) {
                    preferences.putInteger("minMins", mins);
                    preferences.putInteger("minTens", tens);
                    preferences.putInteger("minOnes", ones);
                    preferences.putInteger("minTime", (int) (time * 100));
                    preferences.flush();
                }
                else if(tens == preferences.getInteger("minTens")) {
                    if (ones < preferences.getInteger("minOnes")) {
                        preferences.putInteger("minMins", mins);
                        preferences.putInteger("minTens", tens);
                        preferences.putInteger("minOnes", ones);
                        preferences.putInteger("minTime", (int) (time * 100));
                        preferences.flush();
                    }
                    else if (ones == preferences.getInteger("minOnes")) {
                        if (time < preferences.getInteger("minTime")) {
                            preferences.putInteger("minMins", mins);
                            preferences.putInteger("minTens", tens);
                            preferences.putInteger("minOnes", ones);
                            preferences.putInteger("minTime", (int) (time * 100));
                            preferences.flush();
                        }
                    }
                }
            }
            isBoardSwipable = false;
        }

        /*if (Math.abs(velocity.x) > 40) {
            velocity.x = 0;
            acceleration.x = 0;
        }
        if (velocity.y > 40) {
            velocity.y = 0;
            acceleration.y = 0;
        }*/
        /*if (velocity.x != 0 && Math.abs(position.x - x_i) > 20) {
            velocity.x = 0;
            position.x = x_i + 20;
        }*/

        if (acceleration.x > 1000f) {
            acceleration.x = 0;
            velocity.x = 0;
            position.x = (float) (((int) (position.x / 20)) * 20) + 7;
            position.x += 20;
        }
        if (acceleration.x < -1000f) {
            acceleration.x = 0;
            velocity.x = 0;
            position.x = (float) (((int) (position.x / 20)) * 20) + 7;
        }
        if (acceleration.y > 1000) {
            acceleration.y = 0;
            velocity.y = 0;
            position.y = (float) (((int) (position.y / 20)) * 20) + 7;
            position.y += 20;
        }
        if (acceleration.y < -1000) {
            acceleration.y = 0;
            velocity.y = 0;
            position.y = (float) (((int) (position.y / 20)) * 20) + 7;
        }

        acceleration.x += delta * acceleration.x;
        acceleration.y += delta * acceleration.y;
        position.x += dt * velocity.x;
        position.y += dt * velocity.y;

    }
    public float getNextPos(float currentPos) {
        int increase = (int) (currentPos + 20);
        int index = increase/ 20;
        return (float) (index * 20);
    }
}
