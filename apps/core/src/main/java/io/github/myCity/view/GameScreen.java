package io.github.myCity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.myCity.Main;
import io.github.myCity.model.Feld;
import io.github.myCity.model.Gebäudekarte;
import io.github.myCity.model.Geländetyp;
import io.github.myCity.model.Kapitel;
import io.github.myCity.model.Plättchen;
import io.github.myCity.model.Spiel;
import io.github.myCity.model.Spieler;
import io.github.myCity.model.Stadtplan;

public class GameScreen implements Screen {
    private final Main game;
    private static final int GRID_SIZE = 11;
    private static final float TILE_SIZE = 50;
    private static final float GRID_WORLD_SIZE = GRID_SIZE * TILE_SIZE;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Spiel spiel;
    private Gebäudekarte currentCard;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    public GameScreen(Main game, String player1Name, String player2Name) {
        this.game = game;
        this.spiel = new Spiel();
        this.spiel.neuesSpielStarten(player1Name, player2Name);
        this.currentCard = spiel.getAktuellesKapitel().zieheKarte();

        camera = new OrthographicCamera();
        // We want the grid to be visible, so we make the viewport a bit larger than the grid
        viewport = new FitViewport(GRID_WORLD_SIZE + 300, GRID_WORLD_SIZE + 100, camera);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera and set the projection matrix for the shape renderer
        camera.update();
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        drawGrid(spiel.getCurrentPlayer().getStadtplan());
        drawDeckAndCurrentCard(spiel.getAktuellesKapitel(), currentCard);

        // Draw current player info
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        Spieler currentPlayer = spiel.getCurrentPlayer();
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.0f); // Reset scale to default for layout calculation

        String playerText = "Current Player: " + currentPlayer.getName();
        glyphLayout.setText(game.font, playerText);
        float playerX = viewport.getWorldWidth() - glyphLayout.width - 20;
        game.font.draw(game.batch, glyphLayout, playerX, viewport.getWorldHeight() - 20);
        
        String scoreText = "Score: " + currentPlayer.getPunktzahl();
        glyphLayout.setText(game.font, scoreText);
        float scoreX = viewport.getWorldWidth() - glyphLayout.width - 20;
        game.font.draw(game.batch, glyphLayout, scoreX, viewport.getWorldHeight() - 50);

        game.batch.end();
    }

    private void drawGrid(Stadtplan stadtplan) {
        float startX = 50; // Keep some padding from the left edge
        float startY = (viewport.getWorldHeight() - GRID_WORLD_SIZE) / 2f;

        // 1. Draw base terrain
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Feld feld = stadtplan.getFeld(i, j);
                if (feld != null) {
                    game.shapeRenderer.setColor(getColorForGeländetyp(feld.getGelände()));
                    game.shapeRenderer.rect(startX + i * TILE_SIZE, startY + j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        game.shapeRenderer.end();

        // 2. Draw grid borders, coloring river edges blue with thicker lines
        float LINE_THICKNESS = 2.0f; // Define a constant for thickness
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Feld feld = stadtplan.getFeld(i, j);
                if (feld == null) continue;

                float x = startX + i * TILE_SIZE;
                float y = startY + j * TILE_SIZE;

                // Bottom edge
                game.shapeRenderer.setColor(feld.flussUnten ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x, y, TILE_SIZE, LINE_THICKNESS);

                // Left edge
                game.shapeRenderer.setColor(feld.flussLinks ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x, y, LINE_THICKNESS, TILE_SIZE);

                // Top edge
                game.shapeRenderer.setColor(feld.flussOben ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x, y + TILE_SIZE - LINE_THICKNESS, TILE_SIZE, LINE_THICKNESS);

                // Right edge
                game.shapeRenderer.setColor(feld.flussRechts ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x + TILE_SIZE - LINE_THICKNESS, y, LINE_THICKNESS, TILE_SIZE);
            }
        }
        game.shapeRenderer.end();
    }

    private void drawDeckAndCurrentCard(Kapitel kapitel, Gebäudekarte currentCard) {
        if (currentCard == null) {
            return; // No card to draw
        }

        Plättchen plättchen = currentCard.getPlättchen();
        if (plättchen == null) {
            return; // No plättchen on the card
        }

        // --- Draw the deck ---
        float gridEndX = 50 + GRID_WORLD_SIZE;
        float rightAreaWidth = viewport.getWorldWidth() - gridEndX;


        // --- Draw the current Plättchen ---
        float plättchenWidth = plättchen.getBreite() * TILE_SIZE;
        float startX = gridEndX + (rightAreaWidth - plättchenWidth) * 0.75f;
        float startY = (viewport.getWorldHeight() - (plättchen.getHöhe() * TILE_SIZE)) / 2f;

        boolean[][] form = plättchen.getForm();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(Color.ORANGE);
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    game.shapeRenderer.rect(startX + j * TILE_SIZE, startY + (form.length - 1 - i) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        game.shapeRenderer.end();

        // Draw borders for the Plättchen
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(Color.BLACK);
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    game.shapeRenderer.rect(startX + j * TILE_SIZE, startY + (form.length - 1 - i) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        game.shapeRenderer.end();
    }


    private Color getColorForGeländetyp(Geländetyp typ) {
        switch (typ) {
            case WIESE:
                return Color.GREEN;
            case BAUM:
                return Color.FOREST;
            case FELS:
                return Color.GRAY;
            case GEBIRGE:
                return Color.DARK_GRAY;
            case WALD:
                return new Color(0, 0.5f, 0, 1);
            default:
                return Color.WHITE;
        }
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport and center the camera.
        viewport.update(width, height, true);
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