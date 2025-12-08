package io.github.myCity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.myCity.Main;

import io.github.myCity.model.Feld;
import io.github.myCity.model.Geländetyp;
import io.github.myCity.model.Stadtplan;

public class GameScreen implements Screen {
    private final Main game;
    private static final int GRID_SIZE = 11;
    private static final float TILE_SIZE = 50;
    private static final float GRID_WORLD_SIZE = GRID_SIZE * TILE_SIZE;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Stadtplan stadtplan;

    public GameScreen(Main game) {
        this.game = game;
        this.stadtplan = new Stadtplan();
        camera = new OrthographicCamera();
        // We want the grid to be visible, so we make the viewport a bit larger than the grid
        viewport = new FitViewport(GRID_WORLD_SIZE + 100, GRID_WORLD_SIZE + 100, camera);
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera and set the projection matrix for the shape renderer
        camera.update();
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        drawGrid();
    }

    private void drawGrid() {
        float startX = (viewport.getWorldWidth() - GRID_WORLD_SIZE) / 2f;
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
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }
}