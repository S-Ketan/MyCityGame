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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.myCity.Main;
import io.github.myCity.model.Spiel;
import io.github.myCity.model.Spieler;
import io.github.myCity.model.Stadtplan;

/**
 * Screen displayed at the end of each chapter showing detailed score breakdown.
 */
public class ChapterEndScreen implements Screen {
    private final Main game;
    private final Spiel spiel;
    private final int chapterNumber;
    private final int[] progressAwarded;

    // Episode bonuses (calculated in constructor)
    private final int[] colorBonus;
    private final int[] brunnenBonus;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private Stage stage;
    private Skin skin;

    private static final float VIRTUAL_WIDTH = 850;
    private static final float VIRTUAL_HEIGHT = 700;

    /**
     * Creates a new chapter end screen.
     *
     * @param game The main game instance
     * @param spiel The game state
     * @param chapterNumber The chapter that was just completed
     */
    public ChapterEndScreen(Main game, Spiel spiel, int chapterNumber) {
        this.game = game;
        this.spiel = spiel;
        this.chapterNumber = chapterNumber;

        Spieler[] players = spiel.getSpieler();

        // Calculate and apply episode bonuses BEFORE awarding progress points
        colorBonus = new int[2];
        brunnenBonus = new int[2];

        // Episode 2+: Connected color bonus
        if (chapterNumber >= 2) {
            colorBonus[0] = players[0].getStadtplan().berechneVerbundeneFarbenBonus();
            colorBonus[1] = players[1].getStadtplan().berechneVerbundeneFarbenBonus();
            players[0].setPunktzahl(players[0].getPunktzahl() + colorBonus[0]);
            players[1].setPunktzahl(players[1].getPunktzahl() + colorBonus[1]);
        }

        // Episode 3: Brunnen bonus
        if (chapterNumber == 3) {
            brunnenBonus[0] = players[0].getStadtplan().berechneBrunnenBonus();
            brunnenBonus[1] = players[1].getStadtplan().berechneBrunnenBonus();
            players[0].setPunktzahl(players[0].getPunktzahl() + brunnenBonus[0]);
            players[1].setPunktzahl(players[1].getPunktzahl() + brunnenBonus[1]);
        }

        // Award progress points AFTER all bonuses are applied
        this.progressAwarded = spiel.awardProgressPoints();

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    @Override
    public void show() {
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        createSkin();
        createUI();
    }

    private void createSkin() {
        skin = new Skin();
        skin.add("default-font", game.font);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        // Label styles
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = game.font;
        titleStyle.fontColor = Color.GOLD;
        skin.add("title", titleStyle);

        Label.LabelStyle headerStyle = new Label.LabelStyle();
        headerStyle.font = game.font;
        headerStyle.fontColor = Color.CYAN;
        skin.add("header", headerStyle);

        Label.LabelStyle defaultStyle = new Label.LabelStyle();
        defaultStyle.font = game.font;
        defaultStyle.fontColor = Color.WHITE;
        skin.add("default", defaultStyle);

        Label.LabelStyle positiveStyle = new Label.LabelStyle();
        positiveStyle.font = game.font;
        positiveStyle.fontColor = Color.GREEN;
        skin.add("positive", positiveStyle);

        Label.LabelStyle negativeStyle = new Label.LabelStyle();
        negativeStyle.font = game.font;
        negativeStyle.fontColor = Color.RED;
        skin.add("negative", negativeStyle);

        Label.LabelStyle totalStyle = new Label.LabelStyle();
        totalStyle.font = game.font;
        totalStyle.fontColor = Color.YELLOW;
        skin.add("total", totalStyle);

        // Button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.newDrawable("white", new Color(0.2f, 0.5f, 0.2f, 1f));
        buttonStyle.down = skin.newDrawable("white", new Color(0.1f, 0.3f, 0.1f, 1f));
        buttonStyle.over = skin.newDrawable("white", new Color(0.3f, 0.6f, 0.3f, 1f));
        buttonStyle.font = game.font;
        skin.add("default", buttonStyle);

        TextButton.TextButtonStyle blueButtonStyle = new TextButton.TextButtonStyle();
        blueButtonStyle.up = skin.newDrawable("white", new Color(0.2f, 0.2f, 0.5f, 1f));
        blueButtonStyle.down = skin.newDrawable("white", new Color(0.1f, 0.1f, 0.3f, 1f));
        blueButtonStyle.over = skin.newDrawable("white", new Color(0.3f, 0.3f, 0.6f, 1f));
        blueButtonStyle.font = game.font;
        skin.add("blue", blueButtonStyle);
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        stage.addActor(mainTable);

        // Title - use "Episode" for chapter 1 (episodes 1-3), "Chapter" for later
        String titleText;
        if (chapterNumber <= 3) {
            titleText = "Episode " + chapterNumber + " Complete!";
        } else {
            titleText = "Chapter " + chapterNumber + " Complete!";
        }
        Label titleLabel = new Label(titleText, skin, "title");
        titleLabel.setFontScale(2.0f);
        mainTable.add(titleLabel).colspan(3).padBottom(30).row();

        // Create score tables for both players
        Table scoresTable = new Table();

        // Player headers
        Spieler[] players = spiel.getSpieler();

        // Empty cell for row labels
        scoresTable.add(new Label("", skin)).width(200).pad(5);
        scoresTable.add(new Label(players[0].getName(), skin, "header")).width(150).pad(5);
        scoresTable.add(new Label(players[1].getName(), skin, "header")).width(150).pad(5);
        scoresTable.row();

        // Add separator
        scoresTable.add(new Label("-------------------", skin)).colspan(3).row();

        // Score breakdown rows
        addScoreRow(scoresTable, "Base Score", 10, 10, "default");

        // Trees
        int trees1 = players[0].getStadtplan().bäumeZählen();
        int trees2 = players[1].getStadtplan().bäumeZählen();
        addScoreRow(scoresTable, "Trees", trees1, trees2, "positive");

        // Rocks (negative)
        int rocks1 = players[0].getStadtplan().felsenZählen();
        int rocks2 = players[1].getStadtplan().felsenZählen();
        addScoreRow(scoresTable, "Rocks", rocks1, rocks2, "negative");

        // Empty spaces (negative)
        int empty1 = players[0].getStadtplan().leereWiesenZählen();
        int empty2 = players[1].getStadtplan().leereWiesenZählen();
        addScoreRow(scoresTable, "Empty Spaces", empty1, empty2, "negative");

        // Skipped buildings (negative)
        int skipped1 = -players[0].getÜbersprungeneGebäude();
        int skipped2 = -players[1].getÜbersprungeneGebäude();
        addScoreRow(scoresTable, "Skipped Buildings", skipped1, skipped2, "negative");

        // Episode 2+: Connected color bonus (pre-calculated in constructor)
        if (chapterNumber >= 2) {
            addScoreRow(scoresTable, "Connected Colors", colorBonus[0], colorBonus[1], "positive");
        }

        // Episode 3: Brunnen bonus (pre-calculated in constructor)
        if (chapterNumber == 3) {
            addScoreRow(scoresTable, "Brunnen Bonus", brunnenBonus[0], brunnenBonus[1], "positive");
        }

        // Add separator
        scoresTable.add(new Label("===================", skin)).colspan(3).row();

        // Total score (already includes bonuses applied in constructor)
        addScoreRow(scoresTable, "TOTAL SCORE", players[0].getPunktzahl(),
                    players[1].getPunktzahl(), "total");

        mainTable.add(scoresTable).colspan(3).padBottom(30).row();

        // Winner announcement (if applicable)
        int score1 = players[0].getPunktzahl();
        int score2 = players[1].getPunktzahl();
        String resultText;
        String winnerName;
        String loserName;
        if (score1 > score2) {
            resultText = players[0].getName() + " wins this episode!";
            winnerName = players[0].getName();
            loserName = players[1].getName();
        } else if (score2 > score1) {
            resultText = players[1].getName() + " wins this episode!";
            winnerName = players[1].getName();
            loserName = players[0].getName();
        } else {
            resultText = "It's a tie!";
            winnerName = players[0].getName();
            loserName = players[1].getName();
        }
        Label resultLabel = new Label(resultText, skin, "header");
        resultLabel.setFontScale(1.2f);
        mainTable.add(resultLabel).colspan(3).padBottom(15).row();

        // Progress points section
        mainTable.add(new Label("--- Progress Points ---", skin, "header")).colspan(3).padTop(10).padBottom(5).row();

        // Progress points awarded this chapter
        Table progressTable = new Table();
        progressTable.add(new Label("Awarded:", skin)).width(200).pad(5).left();
        progressTable.add(new Label("+" + progressAwarded[0], skin, "positive")).width(150).pad(5).center();
        progressTable.add(new Label("+" + progressAwarded[1], skin, "positive")).width(150).pad(5).center();
        progressTable.row();

        // Total progress
        progressTable.add(new Label("Total Progress:", skin)).width(200).pad(5).left();
        progressTable.add(new Label(players[0].getProgressPoints() + "/100", skin, "total")).width(150).pad(5).center();
        progressTable.add(new Label(players[1].getProgressPoints() + "/100", skin, "total")).width(150).pad(5).center();
        progressTable.row();

        mainTable.add(progressTable).colspan(3).padBottom(15).row();

        // Check for game winner
        Spieler gameWinner = spiel.checkForGameWinner();
        if (gameWinner != null) {
            Label gameWinnerLabel = new Label(gameWinner.getName() + " WINS THE GAME!", skin, "title");
            gameWinnerLabel.setFontScale(1.5f);
            mainTable.add(gameWinnerLabel).colspan(3).padBottom(15).row();
        }

        // For episodes 1-3: Show sticker placement info
        if (chapterNumber >= 1 && chapterNumber <= 3) {
            Label stickerInfoLabel = new Label("Sticker Placement:", skin, "default");
            mainTable.add(stickerInfoLabel).colspan(3).padBottom(5).row();

            Label winnerStickerLabel = new Label(winnerName + " places a ROCK sticker (penalty)", skin, "negative");
            mainTable.add(winnerStickerLabel).colspan(3).padBottom(5).row();

            Label loserStickerLabel = new Label(loserName + " places a TREE sticker (bonus)", skin, "positive");
            mainTable.add(loserStickerLabel).colspan(3).padBottom(20).row();
        } else {
            mainTable.add(new Label("", skin)).padBottom(15).row(); // Spacer
        }

        // Buttons
        Table buttonTable = new Table();

        TextButton saveButton = new TextButton("Save & Continue", skin, "default");
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                spiel.spielstandSpeichern();
                Gdx.app.log("ChapterEnd", "Game saved!");
                startNextChapter();
            }
        });

        // Different button text for episodes 1-3
        String continueText = (chapterNumber >= 1 && chapterNumber <= 3) ?
                             "Place Stickers" : "Continue";
        TextButton continueButton = new TextButton(continueText, skin, "blue");
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("ChapterEnd", "Continuing without saving");
                startNextChapter();
            }
        });

        buttonTable.add(saveButton).width(200).height(50).pad(10);
        buttonTable.add(continueButton).width(200).height(50).pad(10);

        mainTable.add(buttonTable).colspan(3).row();
    }

    private void addScoreRow(Table table, String label, int value1, int value2, String style) {
        table.add(new Label(label, skin)).width(200).pad(5).left();

        String val1Str = formatValue(value1);
        String val2Str = formatValue(value2);

        // Determine style based on value for non-fixed styles
        String style1 = getStyleForValue(value1, style);
        String style2 = getStyleForValue(value2, style);

        table.add(new Label(val1Str, skin, style1)).width(150).pad(5).center();
        table.add(new Label(val2Str, skin, style2)).width(150).pad(5).center();
        table.row();
    }

    private String formatValue(int value) {
        if (value > 0) return "+" + value;
        if (value == 0) return "0";
        return String.valueOf(value);
    }

    private String getStyleForValue(int value, String defaultStyle) {
        if (defaultStyle.equals("total") || defaultStyle.equals("header") || defaultStyle.equals("default")) {
            return defaultStyle;
        }
        if (value > 0) return "positive";
        if (value < 0) return "negative";
        return "default";
    }

    private void startNextChapter() {
        // Check if someone won the game
        Spieler gameWinner = spiel.checkForGameWinner();
        if (gameWinner != null) {
            Gdx.app.log("ChapterEnd", gameWinner.getName() + " has won the game with 100 progress points!");
            spiel.endpunkteBerechnen();
            game.setScreen(new MenuScreen(game));
            return;
        }

        int nextChapter = chapterNumber + 1;

        if (nextChapter > 24) {
            // Game completed - show final results
            spiel.endpunkteBerechnen();
            Gdx.app.log("ChapterEnd", "All chapters completed! Returning to menu...");
            game.setScreen(new MenuScreen(game));
            return;
        }

        // For episodes 1-3 (Chapter 1): Go to sticker placement screen
        if (chapterNumber >= 1 && chapterNumber <= 3) {
            // Determine winner (higher score wins, gets ROCK penalty)
            Spieler[] players = spiel.getSpieler();
            int score1 = players[0].getPunktzahl();
            int score2 = players[1].getPunktzahl();

            int winnerIndex;
            if (score1 > score2) {
                winnerIndex = 0;
            } else if (score2 > score1) {
                winnerIndex = 1;
            } else {
                // Tie - player 0 is considered "winner" for sticker placement
                winnerIndex = 0;
            }

            Gdx.app.log("ChapterEnd", "Episode " + chapterNumber + " winner: " +
                       players[winnerIndex].getName() + " - Going to sticker placement");

            // Go to sticker placement screen
            game.setScreen(new StickerPlacementScreen(game, spiel, chapterNumber, winnerIndex));
            return;
        }

        // For episodes 4+: Continue normally
        spiel.resetEpisodeTracking();
        spiel.kapitelLaden(nextChapter, true);

        Gdx.app.log("ChapterEnd", "Starting Chapter " + nextChapter);

        // Return to game screen
        game.setScreen(new GameScreen(game, spiel));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.12f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
    }
}
