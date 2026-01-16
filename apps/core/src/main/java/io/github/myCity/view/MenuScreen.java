package io.github.myCity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin; // Added import for Label
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.myCity.Main;
import io.github.myCity.view.UIStyle;

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

        // Create a skin that matches the GameScreen palette
        skin = new Skin();
        skin.add("default-font", game.font);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        // Text field styling
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default-font");
        textFieldStyle.fontColor = UIStyle.PRIMARY_TEXT;
        textFieldStyle.background = skin.newDrawable("white", UIStyle.SECONDARY_SOFT);
        textFieldStyle.cursor = skin.newDrawable("white", UIStyle.ACCENT);
        textFieldStyle.selection = skin.newDrawable("white", UIStyle.DIVIDER);
        skin.add("default", textFieldStyle);

        // Button styling
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", UIStyle.BUTTON);
        textButtonStyle.down = skin.newDrawable("white", UIStyle.SECONDARY);
        textButtonStyle.over = skin.newDrawable("white", UIStyle.SECONDARY_SOFT);
        textButtonStyle.checked = skin.newDrawable("white", UIStyle.SECONDARY);
        textButtonStyle.font = skin.getFont("default-font");
        textButtonStyle.fontColor = UIStyle.PRIMARY_TEXT;
        textButtonStyle.overFontColor = UIStyle.PRIMARY_TEXT;
        skin.add("default", textButtonStyle);

        // Labels
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = UIStyle.PRIMARY_TEXT;
        skin.add("default", labelStyle);

        Label.LabelStyle mutedLabelStyle = new Label.LabelStyle();
        mutedLabelStyle.font = skin.getFont("default-font");
        mutedLabelStyle.fontColor = UIStyle.SECONDARY_TEXT;
        skin.add("muted", mutedLabelStyle);


        Table table = new Table();
        table.setFillParent(true);
        table.pad(30);
        table.defaults().pad(6);
        table.setBackground(skin.newDrawable("white", UIStyle.PANEL));
        stage.addActor(table);

        player1NameField = new TextField("Player 1", skin);
        player2NameField = new TextField("Player 2", skin);

        newGameButton = new TextButton("New Game", skin);
        kapitelButton = new TextButton("Kapitel", skin);
        loadButton = new TextButton("Load", skin);
        quitButton = new TextButton("Quit", skin);

        Label title = new Label("My City", skin);
        title.setFontScale(1.4f);
        table.add(title).padBottom(16).row();

        table.add(new Label("Player 1", skin, "muted")).left();
        table.row();
        table.add(player1NameField).width(260).padBottom(12);
        table.row();
        table.add(new Label("Player 2", skin, "muted")).left();
        table.row();
        table.add(player2NameField).width(260).padBottom(18);
        table.row();

        table.add(newGameButton).width(260).height(46).padBottom(10);
        table.row();
        table.add(kapitelButton).width(260).height(46).padBottom(10);
        table.row();
        table.add(loadButton).width(260).height(46).padBottom(10);
        table.row();
        table.add(quitButton).width(260).height(46);

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
        Gdx.gl.glClearColor(UIStyle.BACKGROUND.r, UIStyle.BACKGROUND.g, UIStyle.BACKGROUND.b, 1f);
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