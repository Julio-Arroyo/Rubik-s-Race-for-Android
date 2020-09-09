package com.julio.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static com.julio.main.AssetLoader.preferences;
import static com.julio.main.Constants.BUTTON_WIDTH;
import static com.julio.main.Constants.MARGIN;
import static com.julio.main.Constants.PLAY_BUTTON_RADIUS;
import static com.julio.main.Constants.SHUFFLER_TILE_SIZE;
import static com.julio.main.Constants.ST_10P;
import static com.julio.main.Constants.WORLD_HEIGHT;
import static com.julio.main.Constants.WORLD_WIDTH;
import static com.julio.main.Tile.isBoardSwipable;
import static com.julio.main.Tile.mins;
import static com.julio.main.Tile.ones;
import static com.julio.main.Tile.tens;
import static com.julio.main.Tile.time;

public class Board implements Screen {
	//TODO: Check if ExtendViewport could be used
	Stage stage;
	static FitViewport fitViewport;
	ShapeRenderer shapeRenderer;
	SpriteBatch spriteBatch, batch;
	//BitmapFont font1, font2, font3, font4, font5, font6, font7, font8,
	BitmapFont font9;
	BitmapFont font10, font11, font12, font13;
	TextButton testButton;
	TextButton.TextButtonStyle style;
	Skin playSkin, shareSkin, settingsSkin, customizeSkin;
	ImageButton playAgain, shareButton, settingsButton, customizeButton;
	Game game;
	Screen settingsScreen;

	static Vector2 emptySpace;
	Vector2 posRed1, posRed2, posRed3, posRed4, posOrange1, posOrange2, posOrange3, posOrange4;
	Vector2 posWhite1, posWhite2, posWhite3, posWhite4, posYellow1, posYellow2, posYellow3, posYellow4;
	Vector2 posBlue1, posBlue2, posBlue3, posBlue4, posGreen1, posGreen2, posGreen3, posGreen4;

	Tile red1, red2, red3, red4, orange1, orange2, orange3, orange4, white1, white2, white3, white4;
	Tile yellow1, yellow2, yellow3, yellow4, blue1, blue2, blue3, blue4, green1, green2, green3, green4;

	Tile red1x, red2x, red3x, red4x, orange1x, orange2x, orange3x, orange4x, white1x, white2x, white3x, white4x;
	Tile yellow1x, yellow2x, yellow3x, yellow4x, blue1x, blue2x, blue3x, blue4x, green1x, green2x, green3x, green4x;

	Tile newGame;

	static Tile fakeTile, fakeShufflerTile;

	static long initialTime;
	long startShuffleTime;
	double bestTime;

	static Color red, orange, white, yellow, blue, green, gray;

	ArrayList<Vector2> tilePositions;
	static ArrayList<Tile> tiles;
	static ArrayList<Tile> shufflerTiles;
	static ArrayList<Tile> shuffler;
	Random random = new Random();

	static AssetManager manager; //TODO: Lower volume of sounds and shorten duration of shuffle sound

	NumberFormat format = new DecimalFormat("00");

	public Board(Game g) {
		game = g;
		//settingsScreen = new SettingsScreen();
		fitViewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
		fitViewport.setScreenBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage = new Stage(fitViewport);
		playSkin = new Skin(Gdx.files.internal("play_again_skin.json"));
		playAgain = new ImageButton(playSkin);
		playAgain.setSize(29,29);
		playAgain.setPosition(42.5f,77.8333f);
		customizeSkin = new Skin(Gdx.files.internal("customize.json"));
		customizeButton = new ImageButton(customizeSkin);
		customizeButton.setSize(BUTTON_WIDTH - 1, BUTTON_WIDTH - 1);
		customizeButton.setPosition(6.5f, 6.5f);
		// Option to make icon gray: customizeButton.getImage().setColor(211/255f, 211/255f, 211/255f, 1);
		settingsSkin = new Skin(Gdx.files.internal("settings.json"));
		settingsButton = new ImageButton(settingsSkin);
		settingsButton.setSize(BUTTON_WIDTH - 1, BUTTON_WIDTH -1);
		settingsButton.setPosition(25.5f, 6.5f);
		settingsButton.addListener(new InputListener(){
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new SettingsScreen(game));
			}
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		shareSkin = new Skin(Gdx.files.internal("share_button.json"));
		shareButton = new ImageButton(shareSkin);
		shareButton.setSize(BUTTON_WIDTH - 1, BUTTON_WIDTH - 1);
		shareButton.setPosition(91,6);
		stage.addActor(playAgain);
		stage.addActor(settingsButton);
		stage.addActor(customizeButton);
		stage.addActor(shareButton);
	}

	@Override
	public void show() {
		shapeRenderer = new ShapeRenderer();

		emptySpace = new Vector2(4, 4);
		spriteBatch = new SpriteBatch();
		batch = new SpriteBatch();
		bestTime = 24.31;

		/*font1 = new BitmapFont(Gdx.files.internal("square_font.fnt"));
		font2 = new BitmapFont(Gdx.files.internal("BPreplay.ttf.fnt"));
		font3 = new BitmapFont(Gdx.files.internal("BPreplayBold.fnt"));
		font4 = new BitmapFont(Gdx.files.internal("BPreplayBoldItalics.fnt"));
		font5 = new BitmapFont(Gdx.files.internal("BPreplayItalics.fnt"));
		font6 = new BitmapFont(Gdx.files.internal("mockup_bold.fnt"));
		font7 = new BitmapFont(Gdx.files.internal("mockup_bold_italics.fnt"));
		font8 = new BitmapFont(Gdx.files.internal("mockup_italics.fnt"));*/
		font9 = new BitmapFont(Gdx.files.internal("mockup_regular.fnt"));
		font9.setColor(Color.BLACK);
		font9.getData().setScale(0.3f);
		font10 = new BitmapFont(Gdx.files.internal("GearsOfPeace_100.ttf.fnt"));
		font10.getData().setScale(0.06f);
		font11 = new BitmapFont(Gdx.files.internal("GearsOfPeace_100.ttf.fnt"));
		font11.getData().setScale(0.053f);
		font12 = new BitmapFont(Gdx.files.internal("GearsOfPeace_100.ttf.fnt"));
		font12.getData().setScale(0.1f);
		font13 = new BitmapFont(Gdx.files.internal("GearsOfPeace_100.ttf.fnt"));
		font13.getData().setScale(0.07f);

		initialTime = TimeUtils.nanoTime();

		manager = new AssetManager();
		manager.load("audio/swipe_down.wav", Sound.class);
		manager.load("audio/swipe_left.wav", Sound.class);
		manager.load("audio/swipe_right.wav", Sound.class);
		manager.load("audio/swipe_up.wav", Sound.class);
		manager.load("audio/shuffler.wav", Music.class);
		manager.finishLoading();

		red = new Color(185 / 255f, 0, 0, 1);
		orange = new Color(1, 89 / 255f, 0, 1);
		white = new Color(1, 1, 1, 1);
		yellow = new Color(1, 213 / 255f, 0, 1);
		blue = new Color(0, 69 / 255f, 173 / 255f, 1);
		green = new Color(0, 155 / 255f, 72 / 255f, 1);
		gray = new Color(150/255f, 150/255f, 150/255f, 1); //alt. 105/255f, 105/255f, 105/255f, 1

		posRed1 = new Vector2();
		posRed2 = new Vector2();
		posRed3 = new Vector2();
		posRed4 = new Vector2();

		posOrange1 = new Vector2();
		posOrange2 = new Vector2();
		posOrange3 = new Vector2();
		posOrange4 = new Vector2();

		posWhite1 = new Vector2();
		posWhite2 = new Vector2();
		posWhite3 = new Vector2();
		posWhite4 = new Vector2();

		posYellow1 = new Vector2();
		posYellow2 = new Vector2();
		posYellow3 = new Vector2();
		posYellow4 = new Vector2();

		posBlue1 = new Vector2();
		posBlue2 = new Vector2();
		posBlue3 = new Vector2();
		posBlue4 = new Vector2();

		posGreen1 = new Vector2();
		posGreen2 = new Vector2();
		posGreen3 = new Vector2();
		posGreen4 = new Vector2();

		tilePositions = new ArrayList<Vector2>();
		tilePositions.add(posRed1);
		tilePositions.add(posRed2);
		tilePositions.add(posRed3);
		tilePositions.add(posRed4);

		tilePositions.add(posOrange1);
		tilePositions.add(posOrange2);
		tilePositions.add(posOrange3);
		tilePositions.add(posOrange4);

		tilePositions.add(posWhite1);
		tilePositions.add(posWhite2);
		tilePositions.add(posWhite3);
		tilePositions.add(posWhite4);

		tilePositions.add(posYellow1);
		tilePositions.add(posYellow2);
		tilePositions.add(posYellow3);
		tilePositions.add(posYellow4);

		tilePositions.add(posBlue1);
		tilePositions.add(posBlue2);
		tilePositions.add(posBlue3);
		tilePositions.add(posBlue4);

		tilePositions.add(posGreen1);
		tilePositions.add(posGreen2);
		tilePositions.add(posGreen3);
		tilePositions.add(posGreen4);

		createStartBoard();

		tilePositions.add(posRed1);
		tilePositions.add(posRed2);
		tilePositions.add(posRed3);
		tilePositions.add(posRed4);

		tilePositions.add(posOrange1);
		tilePositions.add(posOrange2);
		tilePositions.add(posOrange3);
		tilePositions.add(posOrange4);

		tilePositions.add(posWhite1);
		tilePositions.add(posWhite2);
		tilePositions.add(posWhite3);
		tilePositions.add(posWhite4);

		tilePositions.add(posYellow1);
		tilePositions.add(posYellow2);
		tilePositions.add(posYellow3);
		tilePositions.add(posYellow4);

		tilePositions.add(posBlue1);
		tilePositions.add(posBlue2);
		tilePositions.add(posBlue3);
		tilePositions.add(posBlue4);

		tilePositions.add(posGreen1);
		tilePositions.add(posGreen2);
		tilePositions.add(posGreen3);
		tilePositions.add(posGreen4);

		red1 = new Tile(posRed1, red, "red");
		red2 = new Tile(posRed2, red, "red");
		red3 = new Tile(posRed3, red, "red");
		red4 = new Tile(posRed4, red, "red");

		orange1 = new Tile(posOrange1, orange, "orange");
		orange2 = new Tile(posOrange2, orange, "orange");
		orange3 = new Tile(posOrange3, orange, "orange");
		orange4 = new Tile(posOrange4, orange, "orange");

		white1 = new Tile(posWhite1, white, "white");
		white2 = new Tile(posWhite2, white, "white");
		white3 = new Tile(posWhite3, white, "white");
		white4 = new Tile(posWhite4, white, "white");

		yellow1 = new Tile(posYellow1, yellow, "yellow");
		yellow2 = new Tile(posYellow2, yellow, "yellow");
		yellow3 = new Tile(posYellow3, yellow, "yellow");
		yellow4 = new Tile(posYellow4, yellow, "yellow");

		blue1 = new Tile(posBlue1, blue, "blue");
		blue2 = new Tile(posBlue2, blue, "blue");
		blue3 = new Tile(posBlue3, blue, "blue");
		blue4 = new Tile(posBlue4, blue, "blue");

		green1 = new Tile(posGreen1, green, "green");
		green2 = new Tile(posGreen2, green, "green");
		green3 = new Tile(posGreen3, green, "green");
		green4 = new Tile(posGreen4, green, "green");

		newGame = new Tile(new Vector2(19.5f, 89.5f), Color.RED, "new_game_button");

		tiles = new ArrayList<Tile>();
		tiles.add(red1);
		tiles.add(red2);
		tiles.add(red3);
		tiles.add(red4);

		tiles.add(orange1);
		tiles.add(orange2);
		tiles.add(orange3);
		tiles.add(orange4);

		tiles.add(white1);
		tiles.add(white2);
		tiles.add(white3);
		tiles.add(white4);

		tiles.add(yellow1);
		tiles.add(yellow2);
		tiles.add(yellow3);
		tiles.add(yellow4);

		tiles.add(blue1);
		tiles.add(blue2);
		tiles.add(blue3);
		tiles.add(blue4);

		tiles.add(green1);
		tiles.add(green2);
		tiles.add(green3);
		tiles.add(green4);

		Gdx.input.setInputProcessor(stage);

		/*Gdx.input.setInputProcessor(red1);
		Gdx.input.setInputProcessor(red2);
		Gdx.input.setInputProcessor(red3);
		Gdx.input.setInputProcessor(red4);

		Gdx.input.setInputProcessor(orange1);
		Gdx.input.setInputProcessor(orange2);
		Gdx.input.setInputProcessor(orange3);
		Gdx.input.setInputProcessor(orange4);

		Gdx.input.setInputProcessor(white1);
		Gdx.input.setInputProcessor(white2);
		Gdx.input.setInputProcessor(white3);
		Gdx.input.setInputProcessor(white4);

		Gdx.input.setInputProcessor(yellow1);
		Gdx.input.setInputProcessor(yellow2);
		Gdx.input.setInputProcessor(yellow3);
		Gdx.input.setInputProcessor(yellow4);

		Gdx.input.setInputProcessor(blue1);
		Gdx.input.setInputProcessor(blue2);
		Gdx.input.setInputProcessor(blue3);
		Gdx.input.setInputProcessor(blue4);

		Gdx.input.setInputProcessor(green1);
		Gdx.input.setInputProcessor(green2);
		Gdx.input.setInputProcessor(green3);
		Gdx.input.setInputProcessor(green4);

		Gdx.input.setInputProcessor(newGame);*/

		red1x = new Tile(new Vector2(0, 0), red, "red");
		red2x = new Tile(new Vector2(0, 0), red, "red");
		red3x = new Tile(new Vector2(0, 0), red, "red");
		red4x = new Tile(new Vector2(0, 0), red, "red");

		orange1x = new Tile(new Vector2(0, 0), orange, "orange");
		orange2x = new Tile(new Vector2(0, 0), orange, "orange");
		orange3x = new Tile(new Vector2(0, 0), orange, "orange");
		orange4x = new Tile(new Vector2(0, 0), orange, "orange");

		white1x = new Tile(new Vector2(0, 0), white, "white");
		white2x = new Tile(new Vector2(0, 0), white, "white");
		white3x = new Tile(new Vector2(0, 0), white, "white");
		white4x = new Tile(new Vector2(0, 0), white, "white");

		yellow1x = new Tile(new Vector2(0, 0), yellow, "yellow");
		yellow2x = new Tile(new Vector2(0, 0), yellow, "yellow");
		yellow3x = new Tile(new Vector2(0, 0), yellow, "yellow");
		yellow4x = new Tile(new Vector2(0, 0), yellow, "yellow");

		blue1x = new Tile(new Vector2(0, 0), blue, "blue");
		blue2x = new Tile(new Vector2(0, 0), blue, "blue");
		blue3x = new Tile(new Vector2(0, 0), blue, "blue");
		blue4x = new Tile(new Vector2(0, 0), blue, "blue");

		green1x = new Tile(new Vector2(0, 0), green, "green");
		green2x = new Tile(new Vector2(0, 0), green, "green");
		green3x = new Tile(new Vector2(0, 0), green, "green");
		green4x = new Tile(new Vector2(0, 0), green, "green");

		shufflerTiles = new ArrayList<Tile>();
		shufflerTiles.add(red1x);
		shufflerTiles.add(red2x);
		shufflerTiles.add(red3x);
		shufflerTiles.add(red4x);

		shufflerTiles.add(orange1x);
		shufflerTiles.add(orange2x);
		shufflerTiles.add(orange3x);
		shufflerTiles.add(orange4x);

		shufflerTiles.add(white1x);
		shufflerTiles.add(white2x);
		shufflerTiles.add(white3x);
		shufflerTiles.add(white4x);

		shufflerTiles.add(yellow1x);
		shufflerTiles.add(yellow2x);
		shufflerTiles.add(yellow3x);
		shufflerTiles.add(yellow4x);

		shufflerTiles.add(blue1x);
		shufflerTiles.add(blue2x);
		shufflerTiles.add(blue3x);
		shufflerTiles.add(blue4x);

		shufflerTiles.add(green1x);
		shufflerTiles.add(green2x);
		shufflerTiles.add(green3x);
		shufflerTiles.add(green4x);

		shuffler = new ArrayList<>();

		createShuffler();

		fakeTile = new Tile(new Vector2(0,0), Color.BROWN, "brown fake");
		fakeShufflerTile = new Tile(new Vector2(0,0), Color.CORAL, "coral fake");

		AssetLoader.load();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		fitViewport.apply();
		shapeRenderer.setProjectionMatrix(fitViewport.getCamera().combined);
		spriteBatch.setProjectionMatrix(fitViewport.getCamera().combined);
		updateTiles(delta);
		//updateShuffler(delta);

		if (isBoardSwipable) {
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(MARGIN + ST_10P, 124, 40, 40 + 2 * Constants.ST_10P);
			shapeRenderer.rect(MARGIN, 124 + ST_10P, 40 + 2 * Constants.ST_10P, 40);
			shapeRenderer.arc(MARGIN + ST_10P, 124 + ST_10P, Constants.ST_10P, 180, 90, 20);
			shapeRenderer.arc(MARGIN + ST_10P + 40, 124 + ST_10P, Constants.ST_10P, 270, 90, 20);
			shapeRenderer.arc(MARGIN + ST_10P, 124 + ST_10P + 40, Constants.ST_10P, 90, 90, 20);
			shapeRenderer.arc(MARGIN + ST_10P + 40, 124 + ST_10P + 40, Constants.ST_10P, 0, 90, 20);

			shapeRenderer.rect(MARGIN + 2, MARGIN, 100, 104);
			shapeRenderer.rect(MARGIN, MARGIN + 2, 104, 100);
			shapeRenderer.arc(MARGIN + 2, MARGIN + 2, 2, 180, 90, 20);
			shapeRenderer.arc(MARGIN + 102, MARGIN + 2, 2, 270, 90, 20);
			shapeRenderer.arc(MARGIN + 2, MARGIN + 102, 2, 90, 90, 20);
			shapeRenderer.arc(MARGIN + 102, MARGIN + 102, 2, 0, 90, 20);

			renderTiles();
			renderShuffler();

			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(54, 124, 54, SHUFFLER_TILE_SIZE);
			shapeRenderer.rect(53, 125, 56, SHUFFLER_TILE_SIZE - 2);
			shapeRenderer.arc(54, 125, 1, 180, 90, 20);
			shapeRenderer.arc(54 + 54, 125, 1, 270, 90, 20);
			shapeRenderer.arc(54, 125 + SHUFFLER_TILE_SIZE - 2, 1, 90, 90, 20);
			shapeRenderer.arc(54 + 54, 125 + SHUFFLER_TILE_SIZE - 2, 1, 0, 90, 20);

			shapeRenderer.rect(54, 139, 54, 25 + 2 * Constants.ST_10P);
			shapeRenderer.rect(53, 139 + ST_10P, 56, 25);
			shapeRenderer.arc(54, 139 + ST_10P, 1, 180, 90, 20);
			shapeRenderer.arc(54 + 54, 139 + ST_10P, 1, 270, 90, 20);
			shapeRenderer.arc(54, 139 + ST_10P + 25, 1, 90, 90, 20);
			shapeRenderer.arc(54 + 54, 139 + ST_10P + 25, 1, 0, 90, 20);

			shapeRenderer.setColor(blue);
			shapeRenderer.rect(55.5f, 125.5f, 51, SHUFFLER_TILE_SIZE - 3f);
			shapeRenderer.rect(54.5f, 126.5f, 53, SHUFFLER_TILE_SIZE - 5);
			shapeRenderer.arc(55.5f, 126.5f, 1, 180, 90, 20);
			shapeRenderer.arc(55.5f + 51, 126.5f, 1, 270, 90, 20);
			shapeRenderer.arc(55.5f, 126.5f + SHUFFLER_TILE_SIZE - 5, 1, 90, 90, 20);
			shapeRenderer.arc(55.5f + 51, 126.5f + SHUFFLER_TILE_SIZE - 5, 1, 0, 90, 20);

			shapeRenderer.setColor(green);
			shapeRenderer.rect(55.5f, 140.5f, 51, 25 + 2 * Constants.ST_10P - 3);
			shapeRenderer.rect(54.5f, 141.5f, 53, 25 + 2 * Constants.ST_10P - 5);
			shapeRenderer.arc(55.5f, 141.5f, 1, 180, 90, 20);
			shapeRenderer.arc(55.5f + 51, 141.5f, 1, 270, 90, 20);
			shapeRenderer.arc(55.5f, 141.5f + 25 + 2 * Constants.ST_10P - 5, 1, 90, 90, 20);
			shapeRenderer.arc(55.5f + 51, 141.5f + 25 + 2 * Constants.ST_10P - 5, 1, 0, 90, 20);

			//createDivisions();
			shapeRenderer.end();

			spriteBatch.begin();
		/*font1.draw(spriteBatch, "BEST: 8136", 60, 10);
		font2.draw(spriteBatch, "BEST: 8136", 60, 50);
		font3.draw(spriteBatch, "BEST: 8136", 60,80);
		font4.draw(spriteBatch, "BEST: 8136", 60,110);
		font5.draw(spriteBatch, "BEST: 8136", 60,140);*/
			//font6.draw(spriteBatch, "BEST: 24.31s", 62,132);
			font10.setColor(white);
			font11.draw(spriteBatch, "BEST:", 54 + ST_10P, 133f);
			font10.draw(spriteBatch, AssetLoader.getBestTime(), 76 + ST_10P, 133.5f);
			//font10.setColor(yellow);
		/*font10.draw(spriteBatch, "E", 61.5f + ST_10P, 134.5f);
		//font10.setColor(red);
		font10.draw(spriteBatch, "S", 69 + ST_10P, 134.5f);
		//font10.setColor(blue);
		font10.draw(spriteBatch, "T", 74.5f + ST_10P, 134.5f);
		//font10.setColor(orange);
		font10.draw(spriteBatch, ":", 82 + ST_10P, 134.5f);*/
			font11.draw(spriteBatch, "TIME", 55.5f, 141.5f + 25 + 2 * Constants.ST_10P - 5);
			//font10.setColor(Color.PINK);
		/*font10.draw(spriteBatch, "" + 0, 87.5f, 134.5f);
		font10.draw(spriteBatch, "" + 0, 94.5f, 134.5f);
		font10.draw(spriteBatch, "" + 0, 101f, 134.5f);*/

			font12.draw(spriteBatch, "" + mins, 55.5f + 1,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);
			font12.draw(spriteBatch, ":", 55.5f + 10f,141.5f + 25 + 2 * Constants.ST_10P - 5 - 7);
			font12.draw(spriteBatch, "" + tens, 55.5f + 13,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);
			font12.draw(spriteBatch, "" + ones, 55.5f + 22,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);
			font12.draw(spriteBatch, ":", 55.5f + 31,141.5f + 25 + 2 * Constants.ST_10P - 5 - 7);
			font12.draw(spriteBatch, format.format(time * 100), 55.5f + 34,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);
			//font9.draw(spriteBatch, "FPS: " + 1/delta, 10, 122);
			//font12.draw(spriteBatch, "" + 0, 55.5f + 43,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);

		/*font7.draw(spriteBatch, "BEST: 8136", 60, 200);
		font8.draw(spriteBatch, "BEST: 8136", 60, 230);
		font9.draw(spriteBatch, "BEST: 8136", 60,260);*/
			spriteBatch.end();
		} else if (!isBoardSwipable) {
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(MARGIN + ST_10P, 124, 40, 40 + 2 * Constants.ST_10P);
			shapeRenderer.rect(MARGIN, 124 + ST_10P, 40 + 2 * Constants.ST_10P, 40);
			shapeRenderer.arc(MARGIN + ST_10P, 124 + ST_10P, Constants.ST_10P, 180, 90, 20);
			shapeRenderer.arc(MARGIN + ST_10P + 40, 124 + ST_10P, Constants.ST_10P, 270, 90, 20);
			shapeRenderer.arc(MARGIN + ST_10P, 124 + ST_10P + 40, Constants.ST_10P, 90, 90, 20);
			shapeRenderer.arc(MARGIN + ST_10P + 40, 124 + ST_10P + 40, Constants.ST_10P, 0, 90, 20);

			shapeRenderer.rect(MARGIN + 2, MARGIN, 100, 104);
			shapeRenderer.rect(MARGIN, MARGIN + 2, 104, 100);
			shapeRenderer.arc(MARGIN + 2, MARGIN + 2, 2, 180, 90, 20);
			shapeRenderer.arc(MARGIN + 102, MARGIN + 2, 2, 270, 90, 20);
			shapeRenderer.arc(MARGIN + 2, MARGIN + 102, 2, 90, 90, 20);
			shapeRenderer.arc(MARGIN + 102, MARGIN + 102, 2, 0, 90, 20);

			renderTiles();
			renderShuffler();

			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(7, 7, 100,20);
			shapeRenderer.rect(7, 27, 20, 60);
			shapeRenderer.rect(87, 27, 20, 60);
			shapeRenderer.rect(7, 87, 100, 20);

			//TODO: Winning animation
			//TODO: Only render the inner nine tiles
			//TODO: Create next level menu. Next game, share, settings, customize colors.

			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.setColor(0,0,0,0.75f);
			shapeRenderer.rect(0,0, WORLD_WIDTH, WORLD_HEIGHT);
			shapeRenderer.setColor(Color.RED);
			/*shapeRenderer.rect(newGame.position.x + 1.5f, newGame.position.y,42, 25);
			shapeRenderer.rect(newGame.position.x, newGame.position.y + 1.5f, 45, 22);
			shapeRenderer.arc(newGame.position.x + 1.5f, newGame.position.y + 1.5f, 1.5f, 180, 90, 20);
			shapeRenderer.arc(newGame.position.x + 1.5f + 42, newGame.position.y + 1.5f, 1.5f, 270, 90, 20);
			shapeRenderer.arc(newGame.position.x + 1.5f, newGame.position.y + 1.5f + 22, 1.5f, 90, 90, 20);
			shapeRenderer.arc(newGame.position.x + 1.5f + 42, newGame.position.y + 1.5f + 22, 1.5f, 0, 90, 20);
			*//*shapeRenderer.setColor(white);
			shapeRenderer.triangle(58f - (94.1666337f - 77.4999667f), 77.4999667f, 58f - (94.1666337f - 77.4999667f), 94.1666337f, 58,85.8333002f);
			shapeRenderer.triangle(58f, 77.4999667f, 58f, 94.1666337f, 58 + (94.1666337f - 77.4999667f),85.8333002f);*//*
			*//*batch.begin();
			font12.draw(batch, "New shuffle", newGame.position.x + 14, newGame.position.y + 5);
			batch.end();*/

			/*NEW SHUFFLE text button option
			shapeRenderer.rect(newGame.position.x + 1f, newGame.position.y,73, 15);
			shapeRenderer.rect(newGame.position.x, newGame.position.y + 1f, 75, 13);
			shapeRenderer.arc(newGame.position.x + 1f, newGame.position.y + 1f, 1f, 180, 90, 20);
			shapeRenderer.arc(newGame.position.x + 1f + 73, newGame.position.y + 1f, 1f, 270, 90, 20);
			shapeRenderer.arc(newGame.position.x + 1f, newGame.position.y + 1f + 13, 1f, 90, 90, 20);
			shapeRenderer.arc(newGame.position.x + 1f + 73, newGame.position.y + 1f + 13, 1f, 0, 90, 20);*/

			shapeRenderer.rect(42f + PLAY_BUTTON_RADIUS, 76.3333f, 30 - 2 * PLAY_BUTTON_RADIUS, 30);
			shapeRenderer.rect(42, 76.3333f + PLAY_BUTTON_RADIUS, 30, 30 - 2 * PLAY_BUTTON_RADIUS);
			shapeRenderer.arc(42 + PLAY_BUTTON_RADIUS, 76.3333f + PLAY_BUTTON_RADIUS, PLAY_BUTTON_RADIUS, 180, 90, 20);
			shapeRenderer.arc(42 + PLAY_BUTTON_RADIUS + 30 - 2 * PLAY_BUTTON_RADIUS, 76.3333f + PLAY_BUTTON_RADIUS, PLAY_BUTTON_RADIUS, 270, 90, 20);
			shapeRenderer.arc(42 + PLAY_BUTTON_RADIUS, 76.3333f + PLAY_BUTTON_RADIUS + 30 - 2 * PLAY_BUTTON_RADIUS, PLAY_BUTTON_RADIUS, 90, 90, 20);
			shapeRenderer.arc(42 + PLAY_BUTTON_RADIUS + 30 - 2 * PLAY_BUTTON_RADIUS, 76.3333f + PLAY_BUTTON_RADIUS + 30 - 2 * PLAY_BUTTON_RADIUS, PLAY_BUTTON_RADIUS, 0, 90, 20);

			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(54, 124, 54, SHUFFLER_TILE_SIZE);
			shapeRenderer.rect(53, 125, 56, SHUFFLER_TILE_SIZE - 2);
			shapeRenderer.arc(54, 125, 1, 180, 90, 20);
			shapeRenderer.arc(54 + 54, 125, 1, 270, 90, 20);
			shapeRenderer.arc(54, 125 + SHUFFLER_TILE_SIZE - 2, 1, 90, 90, 20);
			shapeRenderer.arc(54 + 54, 125 + SHUFFLER_TILE_SIZE - 2, 1, 0, 90, 20);

			shapeRenderer.rect(54, 139, 54, 25 + 2 * Constants.ST_10P);
			shapeRenderer.rect(53, 139 + ST_10P, 56, 25);
			shapeRenderer.arc(54, 139 + ST_10P, 1, 180, 90, 20);
			shapeRenderer.arc(54 + 54, 139 + ST_10P, 1, 270, 90, 20);
			shapeRenderer.arc(54, 139 + ST_10P + 25, 1, 90, 90, 20);
			shapeRenderer.arc(54 + 54, 139 + ST_10P + 25, 1, 0, 90, 20);

			shapeRenderer.setColor(blue);
			shapeRenderer.rect(55.5f, 125.5f, 51, SHUFFLER_TILE_SIZE - 3f);
			shapeRenderer.rect(54.5f, 126.5f, 53, SHUFFLER_TILE_SIZE - 5);
			shapeRenderer.arc(55.5f, 126.5f, 1, 180, 90, 20);
			shapeRenderer.arc(55.5f + 51, 126.5f, 1, 270, 90, 20);
			shapeRenderer.arc(55.5f, 126.5f + SHUFFLER_TILE_SIZE - 5, 1, 90, 90, 20);
			shapeRenderer.arc(55.5f + 51, 126.5f + SHUFFLER_TILE_SIZE - 5, 1, 0, 90, 20);

			shapeRenderer.setColor(green);
			shapeRenderer.rect(55.5f, 140.5f, 51, 25 + 2 * Constants.ST_10P - 3);
			shapeRenderer.rect(54.5f, 141.5f, 53, 25 + 2 * Constants.ST_10P - 5);
			shapeRenderer.arc(55.5f, 141.5f, 1, 180, 90, 20);
			shapeRenderer.arc(55.5f + 51, 141.5f, 1, 270, 90, 20);
			shapeRenderer.arc(55.5f, 141.5f + 25 + 2 * Constants.ST_10P - 5, 1, 90, 90, 20);
			shapeRenderer.arc(55.5f + 51, 141.5f + 25 + 2 * Constants.ST_10P - 5, 1, 0, 90, 20);

			drawRoundedEdgeRectangle(gray, 6,6, BUTTON_WIDTH, BUTTON_WIDTH, 68/30f, 20);
			drawRoundedEdgeRectangle(gray, 25,6, BUTTON_WIDTH, BUTTON_WIDTH, 68/30f, 20);
			drawRoundedEdgeRectangle(gray, 91,6, BUTTON_WIDTH, BUTTON_WIDTH, 68/30f, 20);

			//createDivisions();
			shapeRenderer.end();

			spriteBatch.begin();
		/*font1.draw(spriteBatch, "BEST: 8136", 60, 10);
		font2.draw(spriteBatch, "BEST: 8136", 60, 50);
		font3.draw(spriteBatch, "BEST: 8136", 60,80);
		font4.draw(spriteBatch, "BEST: 8136", 60,110);
		font5.draw(spriteBatch, "BEST: 8136", 60,140);*/
			//font6.draw(spriteBatch, "BEST: 24.31s", 62,132);
			font10.setColor(white);
			font11.draw(spriteBatch, "BEST:", 54 + ST_10P, 133f);
			font10.draw(spriteBatch, AssetLoader.getBestTime(), 76 + ST_10P, 133.5f);
			//font10.setColor(yellow);
		/*font10.draw(spriteBatch, "E", 61.5f + ST_10P, 134.5f);
		//font10.setColor(red);
		font10.draw(spriteBatch, "S", 69 + ST_10P, 134.5f);
		//font10.setColor(blue);
		font10.draw(spriteBatch, "T", 74.5f + ST_10P, 134.5f);
		//font10.setColor(orange);
		font10.draw(spriteBatch, ":", 82 + ST_10P, 134.5f);*/
			font11.draw(spriteBatch, "TIME", 55.5f, 141.5f + 25 + 2 * Constants.ST_10P - 5);
			//font10.setColor(Color.PINK);
		/*font10.draw(spriteBatch, "" + 0, 87.5f, 134.5f);
		font10.draw(spriteBatch, "" + 0, 94.5f, 134.5f);
		font10.draw(spriteBatch, "" + 0, 101f, 134.5f);*/

			font12.draw(spriteBatch, "" + mins, 55.5f + 1,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);
			font12.draw(spriteBatch, ":", 55.5f + 10f,141.5f + 25 + 2 * Constants.ST_10P - 5 - 7);
			font12.draw(spriteBatch, "" + tens, 55.5f + 13,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);
			font12.draw(spriteBatch, "" + ones, 55.5f + 22,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);
			font12.draw(spriteBatch, ":", 55.5f + 31,141.5f + 25 + 2 * Constants.ST_10P - 5 - 7);
			font12.draw(spriteBatch, format.format(time * 100), 55.5f + 34,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);
			//font13.draw(spriteBatch, "NEW SHUFFLE", newGame.position.x + 3.5f, newGame.position.y + 11.5f);
			//font9.draw(spriteBatch, "FPS: " + 1/delta, 10, 122);
			//font12.draw(spriteBatch, "" + 0, 55.5f + 43,141.5f + 25 + 2 * Constants.ST_10P - 5 - 9);

		/*font7.draw(spriteBatch, "BEST: 8136", 60, 200);
		font8.draw(spriteBatch, "BEST: 8136", 60, 230);
		font9.draw(spriteBatch, "BEST: 8136", 60,260);*/
			spriteBatch.end();
			stage.act();
			stage.draw();
		}



		if (isPlaying()) {
			if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - initialTime) >= 2) {
				time += delta;
			}
			if (time >= 1) {
				ones++;
				time = 0;
			}
			if (ones > 9) {
				tens++;
				ones = 0;
			}
			if (tens > 5) {
				mins++;
				tens = 0;
			}
		}


		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		renderTiles();
		renderShuffler();
		createDivisions();
		shapeRenderer.end();*/

		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		createDivisions();
		shapeRenderer.end();*/
	}

	@Override
	public void resize(int width, int height) {
		fitViewport.update(width, height, true);
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
		shapeRenderer.dispose();
		spriteBatch.dispose();
		font10.dispose();
		font11.dispose();
		font12.dispose();
	}

	public void createBorders(Color color) {
		shapeRenderer.setColor(color);
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				shapeRenderer.rect((float) (20 * i), (float) (20 * j), 20, 20);
			}
		}
	}

	public void createStartBoard() {
		int currentIndex;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (i != 4 || j != 4) {
					currentIndex = random.nextInt(tilePositions.size());
					tilePositions.get(currentIndex).x = i * 20 + MARGIN + 2;
					tilePositions.get(currentIndex).y = j * 20 + MARGIN + 2;
					tilePositions.remove(currentIndex);
				}
			}
		}
	}

	public void renderTiles() {
		for (Tile t:tiles) {
			shapeRenderer.setColor(t.color);
			shapeRenderer.rect(t.position.x + 2, t.position.y + 1, 16,18);
			shapeRenderer.rect(t.position.x + 1, t.position.y + 2, 18,16);
			shapeRenderer.arc(t.position.x + 2, t.position.y + 2, 1, 180, 90, 20);
			shapeRenderer.arc(t.position.x + 18, t.position.y + 2, 1, 270, 90, 20);
			shapeRenderer.arc(t.position.x + 2, t.position.y + 18, 1, 90, 90, 20);
			shapeRenderer.arc(t.position.x + 18, t.position.y + 18, 1, 0, 90, 20);
		}
	}

	public void createShuffler() {
		int currentIndex;
		int counter = 0;
		Collections.shuffle(shufflerTiles);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				//currentIndex = random.nextInt(shufflerTiles.size());
				shufflerTiles.get(counter).position.x = i * 13.3333f + MARGIN + ST_10P;
				shufflerTiles.get(counter).position.y = j * 13.3333f + 124 + ST_10P;
				counter++;
				/*if (shufflerTiles.get(currentIndex).position.x == 0 && shufflerTiles.get(currentIndex).position.y == 0) {
					shufflerTiles.get(currentIndex).position.x = i * 13.3333f + 5;
					shufflerTiles.get(currentIndex).position.y = j * 13.3333f + 115;
				}*/
			}
		}
		for (int i = 0; i < shufflerTiles.size(); i++) {
			if (shufflerTiles.get(i).position.x != 0 && shufflerTiles.get(i).position.y != 0) {
				shuffler.add(shufflerTiles.get(i));
			}
		}
	}

	public static void newShuffle() {
		int counter = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				//currentIndex = random.nextInt(shufflerTiles.size());
				shufflerTiles.get(counter).position.x = 0;
				shufflerTiles.get(counter).position.y = 0;
				counter++;
				/*if (shufflerTiles.get(currentIndex).position.x == 0 && shufflerTiles.get(currentIndex).position.y == 0) {
					shufflerTiles.get(currentIndex).position.x = i * 13.3333f + 5;
					shufflerTiles.get(currentIndex).position.y = j * 13.3333f + 115;
				}*/
			}
		}
		counter = 0;
		Collections.shuffle(shufflerTiles);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				//currentIndex = random.nextInt(shufflerTiles.size());
				shufflerTiles.get(counter).position.x = i * 13.3333f + MARGIN + ST_10P;
				shufflerTiles.get(counter).position.y = j * 13.3333f + 124 + ST_10P;
				counter++;
				/*if (shufflerTiles.get(currentIndex).position.x == 0 && shufflerTiles.get(currentIndex).position.y == 0) {
					shufflerTiles.get(currentIndex).position.x = i * 13.3333f + 5;
					shufflerTiles.get(currentIndex).position.y = j * 13.3333f + 115;
				}*/
			}
		}
		counter = 0;
		for (int i = 0; i < shufflerTiles.size(); i++) {
			if (shufflerTiles.get(i).position.x != 0 && shufflerTiles.get(i).position.y != 0) {
				shuffler.set(counter, shufflerTiles.get(i));
				counter++;
			}
		}
	}

	public void renderShuffler() {
		if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - initialTime) < 2) {
			/*int counter = 0;
			Collections.shuffle(shufflerTiles);
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					//currentIndex = random.nextInt(shufflerTiles.size());
					shufflerTiles.get(counter).position.x = i * 13.3333f + MARGIN + ST_10P;
					shufflerTiles.get(counter).position.y = j * 13.3333f + 124 + ST_10P;
					counter++;
				*//*if (shufflerTiles.get(currentIndex).position.x == 0 && shufflerTiles.get(currentIndex).position.y == 0) {
					shufflerTiles.get(currentIndex).position.x = i * 13.3333f + 5;
					shufflerTiles.get(currentIndex).position.y = j * 13.3333f + 115;
				}*//*
				}
			}*/
			ArrayList<Color> animationColors = new ArrayList<Color>();
			animationColors.add(green);
			animationColors.add(blue);
			animationColors.add(yellow);
			animationColors.add(white);
			animationColors.add(red);
			animationColors.add(orange);
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					float animation_x = i * 13.3333f + MARGIN + ST_10P;
					float animation_y = j * 13.3333f + 124 + ST_10P;
					shapeRenderer.setColor(animationColors.get(random.nextInt(animationColors.size())));
					shapeRenderer.rect(animation_x + Constants.ST_10P, animation_y + Constants.ST_5P, Constants.ST_80P,Constants.ST_90P);
					shapeRenderer.rect(animation_x + Constants.ST_5P, animation_y + Constants.ST_10P, Constants.ST_90P,Constants.ST_80P);
					shapeRenderer.arc(animation_x+ Constants.ST_10P, animation_y + Constants.ST_10P, Constants.ST_5P, 180, 90);
					shapeRenderer.arc(animation_x + Constants.ST_90P, animation_y + Constants.ST_10P, Constants.ST_5P, 270, 90);
					shapeRenderer.arc(animation_x + Constants.ST_10P, animation_y + Constants.ST_90P, Constants.ST_5P, 90, 90);
					shapeRenderer.arc(animation_x + Constants.ST_90P, animation_y + Constants.ST_90P, Constants.ST_5P, 0, 90);
				}
			}
		} else {
			for (Tile t:shuffler) {
				if (t.position.x != 0 && t.position.y != 0) {
					shapeRenderer.setColor(t.color);
					//shapeRenderer.rect(t.position.x, t.position.y, 13.3333f,13.3333f);
					shapeRenderer.rect(t.position.x + Constants.ST_10P, t.position.y + Constants.ST_5P, Constants.ST_80P,Constants.ST_90P);
					shapeRenderer.rect(t.position.x + Constants.ST_5P, t.position.y + Constants.ST_10P, Constants.ST_90P,Constants.ST_80P);
					shapeRenderer.arc(t.position.x + Constants.ST_10P, t.position.y + Constants.ST_10P, Constants.ST_5P, 180, 90);
					shapeRenderer.arc(t.position.x + Constants.ST_90P, t.position.y + Constants.ST_10P, Constants.ST_5P, 270, 90);
					shapeRenderer.arc(t.position.x + Constants.ST_10P, t.position.y + Constants.ST_90P, Constants.ST_5P, 90, 90);
					shapeRenderer.arc(t.position.x + Constants.ST_90P, t.position.y + Constants.ST_90P, Constants.ST_5P, 0, 90);
				}
			}
		}
	}

	public void updateTiles(float d) {
		for (Tile til:tiles) {
			til.update(d);
		}
	}

	/*public void updateShuffler(float d) {
		for (Tile til:shufflerTiles) {
			til.update(d);
		}
	}*/

	/*public void createDivisions() {
		shapeRenderer.setColor(Color.BLACK);
		for (int i = 0; i < 6; i++) {
			shapeRenderer.rectLine(i * 20, 0, i * 20, 100, 2f);
			shapeRenderer.rectLine(0, i * 20, 100, i * 20, 2f);
		}

		for (int i = 0; i < 4; i++) {
			shapeRenderer.rectLine(i * 13.3333f + 5, 115, i * 13.3333f + 5, 155, 1f);
			shapeRenderer.rectLine(5, i * 13.3333f + 115, 45, i * 13.3333f + 115, 1f);
		}
	}*/

	public static Tile getFlickedTile(Vector2 vector) {
		for (Tile t: tiles) {
			if (t.position.x == vector.x && t.position.y == vector.y) {
				return t;
			}
		}
		return fakeTile;
	}
	public static Tile getShufflerTile(Vector2 vector) {
		for (Tile t: shufflerTiles) {
			if (t.position.x == vector.x && t.position.y == vector.y) {
				return t;
			}
		}
		return fakeTile;
	}

	public static boolean isPlaying() {
		int matchedTilesCount = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				String tile1 = getFlickedTile( new Vector2((i + 1) * 20 + MARGIN + 2, (j + 1) * 20 + MARGIN + 2)).name;
				String tile2 = getShufflerTile( new Vector2(i * 13.3333f + MARGIN + ST_10P, j * 13.3333f + 124 + ST_10P)).name;
				if (!(tile1.equals(tile2))) {
					return true;
				}
			}
		}
		return false;
	}
	public void drawRoundedEdgeRectangle(Color color, float x, float y, float width, float height, float radius, int segments) {
		shapeRenderer.setColor(color);
		shapeRenderer.rect(x + radius, y, width - 2*radius, height);
		shapeRenderer.rect(x, y + radius, width, height - 2 * radius);
		shapeRenderer.arc(x + radius, y + radius, radius, 180, 90, segments);
		shapeRenderer.arc(x + radius, y + radius + height - 2 * radius, radius, 90, 90, segments);
		shapeRenderer.arc(x + radius + width - 2 * radius, y + radius, radius, 270, 90, segments);
		shapeRenderer.arc(x + radius + width - 2 * radius, y + radius + height - 2 * radius, radius, 0, 90, segments);
	}
}
