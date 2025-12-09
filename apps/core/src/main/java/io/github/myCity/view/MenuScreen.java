package io.github.myCity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label; // Added import for Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;


import io.github.myCity.Main;

public class MenuScreen implements Screen {
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Vector3 touchPoint; // This is no longer directly used for UI, but might be for other interactions

    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    private Stage stage;
    private Skin skin;
    private TextField player1NameField;
    private TextField player2NameField;
    private TextButton newGameButton;
    private TextButton kapitelButton;
    private TextButton loadButton;
    private TextButton quitButton;


    public MenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        touchPoint = new Vector3();
    }

    @Override
    public void show() {
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        // Create a skin for the UI elements
        skin = new Skin();

        // Use the font from Main.java
        skin.add("default-font", game.font);

        // Create a texture for the text field background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        // Create a TextFieldStyle
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default-font");
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = skin.newDrawable("white", Color.LIGHT_GRAY);
        textFieldStyle.cursor = skin.newDrawable("white", Color.BLACK);
        textFieldStyle.selection = skin.newDrawable("white", Color.BLUE);
        skin.add("default", textFieldStyle);

        // Create a TextButtonStyle
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default-font");
        skin.add("default", textButtonStyle);

        // Create a LabelStyle
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE; // Or any color you prefer
        skin.add("default", labelStyle);


        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        player1NameField = new TextField("Player 1", skin);
        player2NameField = new TextField("Player 2", skin);

        newGameButton = new TextButton("New Game", skin);
        kapitelButton = new TextButton("Kapitel", skin);
        loadButton = new TextButton("Load", skin);
        quitButton = new TextButton("Quit", skin);

        table.add(new Label("Player 1 Name:", skin)).padBottom(10).row();
        table.add(player1NameField).width(200).padBottom(10).row();
        table.add(new Label("Player 2 Name:", skin)).padBottom(10).row();
        table.add(player2NameField).width(200).padBottom(20).row();

        table.add(newGameButton).width(200).height(40).padBottom(10).row();
        table.add(kapitelButton).width(200).height(40).padBottom(10).row();
        table.add(loadButton).width(200).height(40).padBottom(10).row();
        table.add(quitButton).width(200).height(40).padBottom(10).row();

        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Menu", "New Game clicked");
                game.setScreen(new GameScreen(game, player1NameField.getText(), player2NameField.getText()));
            }
        });

        kapitelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Menu", "Kapitel clicked");
                game.setScreen(new KapitelScreen(game));
            }
        });

        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Menu", "Load clicked");
                // Here you would load a game
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Menu", "Quit clicked");
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}