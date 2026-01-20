package io.github.myCity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.myCity.Main;

/**
 * Screen for selecting which episode to play.
 */
public class EpisodeScreen implements Screen {
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;

    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    private static final int MIN_EPISODE = 1;
    private static final int MAX_EPISODE = 3;

    private Stage stage;
    private Skin skin;
    private Label episodeLabel;
    private TextField player1NameField;
    private TextField player2NameField;
    private int currentEpisode = 1;

    public EpisodeScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    @Override
    public void show() {
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();
        skin.add("default-font", game.font);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default-font");
        textFieldStyle.fontColor = UIStyle.PRIMARY_TEXT;
        textFieldStyle.background = skin.newDrawable("white", UIStyle.SECONDARY_SOFT);
        textFieldStyle.cursor = skin.newDrawable("white", UIStyle.ACCENT);
        textFieldStyle.selection = skin.newDrawable("white", UIStyle.DIVIDER);
        skin.add("default", textFieldStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = UIStyle.createRoundedDrawable(UIStyle.BUTTON, 10);
        textButtonStyle.down = UIStyle.createRoundedDrawable(UIStyle.SECONDARY, 10);
        textButtonStyle.over = UIStyle.createRoundedDrawable(UIStyle.BUTTON_HOVER, 10);
        textButtonStyle.font = skin.getFont("default-font");
        textButtonStyle.fontColor = UIStyle.PRIMARY_TEXT;
        textButtonStyle.overFontColor = UIStyle.PRIMARY_TEXT;
        skin.add("default", textButtonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = UIStyle.PRIMARY_TEXT;
        skin.add("default", labelStyle);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = skin.getFont("default-font");
        titleStyle.fontColor = UIStyle.ACCENT;
        skin.add("title", titleStyle);

        Label.LabelStyle mutedLabelStyle = new Label.LabelStyle();
        mutedLabelStyle.font = skin.getFont("default-font");
        mutedLabelStyle.fontColor = UIStyle.SECONDARY_TEXT;
        skin.add("muted", mutedLabelStyle);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(30);
        mainTable.setBackground(skin.newDrawable("white", UIStyle.PANEL));
        stage.addActor(mainTable);

        Label title = new Label("Select Episode", skin, "title");
        title.setFontScale(1.4f);
        mainTable.add(title).padBottom(30).row();

        mainTable.add(new Label("Player 1", skin, "muted")).left().row();
        player1NameField = new TextField("Player 1", skin);
        mainTable.add(player1NameField).width(260).padBottom(12).row();

        mainTable.add(new Label("Player 2", skin, "muted")).left().row();
        player2NameField = new TextField("Player 2", skin);
        mainTable.add(player2NameField).width(260).padBottom(20).row();

        Table episodeSelectorTable = new Table();

        TextButton leftButton = new TextButton("<", skin);
        TextButton rightButton = new TextButton(">", skin);
        episodeLabel = new Label(getEpisodeText(), skin);
        episodeLabel.setFontScale(1.2f);

        leftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentEpisode > MIN_EPISODE) {
                    currentEpisode--;
                    updateEpisodeLabel();
                }
            }
        });

        rightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentEpisode < MAX_EPISODE) {
                    currentEpisode++;
                    updateEpisodeLabel();
                }
            }
        });

        episodeSelectorTable.add(leftButton).width(50).height(50).padRight(20);
        episodeSelectorTable.add(episodeLabel).width(150).center();
        episodeSelectorTable.add(rightButton).width(50).height(50).padLeft(20);

        mainTable.add(episodeSelectorTable).padBottom(30).row();

        TextButton startButton = new TextButton("Start Episode", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String player1Name = player1NameField.getText();
                String player2Name = player2NameField.getText();
                Gdx.app.log("Episode", "Starting Episode " + currentEpisode + " with " + player1Name + " and " + player2Name);
                game.setScreen(new GameScreen(game, player1Name, player2Name, currentEpisode));
            }
        });
        mainTable.add(startButton).width(200).height(46).padBottom(15).row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        mainTable.add(backButton).width(200).height(46);
    }

    private String getEpisodeText() {
        return "Episode " + currentEpisode;
    }

    private void updateEpisodeLabel() {
        episodeLabel.setText(getEpisodeText());
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
