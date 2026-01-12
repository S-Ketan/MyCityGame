package io.github.myCity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.myCity.Main;
import io.github.myCity.model.Feld;
import io.github.myCity.model.Geländetyp;
import io.github.myCity.model.Position;
import io.github.myCity.model.Spiel;
import io.github.myCity.model.Spieler;
import io.github.myCity.model.Stadtplan;
import io.github.myCity.model.Sticker;
import io.github.myCity.model.StickerTyp;

import java.util.HashSet;
import java.util.Set;

/**
 * Screen for placing Brunnen (well/fountain) stickers before Episode 3.
 * Each player places one Brunnen on their board.
 */
public class BrunnenPlacementScreen implements Screen {
    private final Main game;
    private final Spiel spiel;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private static final int GRID_SIZE = 11;
    private static final float TILE_SIZE = 40;
    private static final float GRID_WORLD_SIZE = GRID_SIZE * TILE_SIZE;

    // Valid Brunnen positions (same as sticker positions)
    private static final Set<Position> VALID_BRUNNEN_POSITIONS = new HashSet<>();
    static {
        // Row x=5
        VALID_BRUNNEN_POSITIONS.add(new Position(5, 3));
        VALID_BRUNNEN_POSITIONS.add(new Position(5, 4));
        VALID_BRUNNEN_POSITIONS.add(new Position(5, 5));
        VALID_BRUNNEN_POSITIONS.add(new Position(5, 6));
        VALID_BRUNNEN_POSITIONS.add(new Position(5, 7));
        VALID_BRUNNEN_POSITIONS.add(new Position(5, 8));
        VALID_BRUNNEN_POSITIONS.add(new Position(5, 9));
        VALID_BRUNNEN_POSITIONS.add(new Position(5, 10));
        // Row x=6
        VALID_BRUNNEN_POSITIONS.add(new Position(6, 3));
        VALID_BRUNNEN_POSITIONS.add(new Position(6, 4));
        VALID_BRUNNEN_POSITIONS.add(new Position(6, 5));
        VALID_BRUNNEN_POSITIONS.add(new Position(6, 6));
        VALID_BRUNNEN_POSITIONS.add(new Position(6, 8));
        VALID_BRUNNEN_POSITIONS.add(new Position(6, 9));
        VALID_BRUNNEN_POSITIONS.add(new Position(6, 10));
        // Row x=7
        VALID_BRUNNEN_POSITIONS.add(new Position(7, 3));
        VALID_BRUNNEN_POSITIONS.add(new Position(7, 4));
        VALID_BRUNNEN_POSITIONS.add(new Position(7, 5));
        VALID_BRUNNEN_POSITIONS.add(new Position(7, 6));
        VALID_BRUNNEN_POSITIONS.add(new Position(7, 7));
        VALID_BRUNNEN_POSITIONS.add(new Position(7, 8));
        VALID_BRUNNEN_POSITIONS.add(new Position(7, 9));
        VALID_BRUNNEN_POSITIONS.add(new Position(7, 10));
        // Row x=8
        VALID_BRUNNEN_POSITIONS.add(new Position(8, 6));
        VALID_BRUNNEN_POSITIONS.add(new Position(8, 7));
        VALID_BRUNNEN_POSITIONS.add(new Position(8, 8));
        VALID_BRUNNEN_POSITIONS.add(new Position(8, 10));
    }

    private int currentPlayerIndex = 0;
    private boolean player1Placed = false;
    private boolean player2Placed = false;

    private float gridStartX;
    private float gridStartY;

    public BrunnenPlacementScreen(Main game, Spiel spiel) {
        this.game = game;
        this.spiel = spiel;

        camera = new OrthographicCamera();
        viewport = new FitViewport(GRID_WORLD_SIZE + 350, GRID_WORLD_SIZE + 150, camera);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 worldPos = viewport.unproject(new Vector2(screenX, screenY));

                int gridX = (int) ((worldPos.x - gridStartX) / TILE_SIZE);
                int gridY = (int) ((worldPos.y - gridStartY) / TILE_SIZE);

                Position clickedPos = new Position(gridX, gridY);

                if (isValidBrunnenPosition(clickedPos)) {
                    placeBrunnen(clickedPos);
                    return true;
                }

                return false;
            }
        });
    }

    private boolean isValidBrunnenPosition(Position pos) {
        for (Position validPos : VALID_BRUNNEN_POSITIONS) {
            if (validPos.getX() == pos.getX() && validPos.getY() == pos.getY()) {
                Stadtplan stadtplan = spiel.getSpieler()[currentPlayerIndex].getStadtplan();
                Feld feld = stadtplan.getFeld(pos.getX(), pos.getY());
                if (feld != null) {
                    if (feld.getGelände() != Geländetyp.WIESE) {
                        return false;
                    }
                    for (Sticker s : stadtplan.getSticker()) {
                        if (s.getPos().getX() == pos.getX() && s.getPos().getY() == pos.getY()) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void placeBrunnen(Position pos) {
        Spieler player = spiel.getSpieler()[currentPlayerIndex];
        Sticker brunnen = new Sticker(StickerTyp.BRUNNEN, pos, "brunnen_episode3");
        player.getStadtplan().addSticker(brunnen);

        Gdx.app.log("BrunnenPlacement", player.getName() + " placed BRUNNEN at (" +
                   pos.getX() + ", " + pos.getY() + ")");

        if (currentPlayerIndex == 0) {
            player1Placed = true;
            currentPlayerIndex = 1;
        } else {
            player2Placed = true;
            proceedToEpisode3();
        }
    }

    private void proceedToEpisode3() {
        Gdx.app.log("BrunnenPlacement", "Both players placed Brunnen. Starting Episode 3...");
        spiel.resetEpisodeTracking();
        spiel.kapitelLaden(3, true);
        game.setScreen(new GameScreen(game, spiel));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.12f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        drawGrid();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        drawUI();
        game.batch.end();
    }

    private void drawGrid() {
        gridStartX = 50;
        gridStartY = (viewport.getWorldHeight() - GRID_WORLD_SIZE) / 2f;

        Stadtplan stadtplan = spiel.getSpieler()[currentPlayerIndex].getStadtplan();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                Feld feld = stadtplan.getFeld(x, y);
                if (feld != null) {
                    game.shapeRenderer.setColor(getColorForGeländetyp(feld.getGelände()));
                    game.shapeRenderer.rect(gridStartX + x * TILE_SIZE, gridStartY + y * TILE_SIZE,
                                           TILE_SIZE, TILE_SIZE);
                }
            }
        }
        game.shapeRenderer.end();

        // Highlight valid Brunnen positions
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color highlightColor = new Color(0.2f, 0.5f, 0.9f, 0.5f); // Blue highlight

        for (Position pos : VALID_BRUNNEN_POSITIONS) {
            if (isValidBrunnenPosition(pos)) {
                game.shapeRenderer.setColor(highlightColor);
                game.shapeRenderer.rect(gridStartX + pos.getX() * TILE_SIZE + 2,
                                       gridStartY + pos.getY() * TILE_SIZE + 2,
                                       TILE_SIZE - 4, TILE_SIZE - 4);
            }
        }
        game.shapeRenderer.end();

        // Draw grid lines
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(Color.BLACK);
        for (int x = 0; x <= GRID_SIZE; x++) {
            for (int y = 0; y <= GRID_SIZE; y++) {
                if (x < GRID_SIZE && y < GRID_SIZE) {
                    game.shapeRenderer.rect(gridStartX + x * TILE_SIZE, gridStartY + y * TILE_SIZE,
                                           TILE_SIZE, TILE_SIZE);
                }
            }
        }
        game.shapeRenderer.end();
    }

    private void drawUI() {
        Spieler currentPlayer = spiel.getSpieler()[currentPlayerIndex];

        game.font.setColor(Color.GOLD);
        game.font.getData().setScale(1.5f);

        String title = "Episode 3 - Place Brunnen (Well)";
        glyphLayout.setText(game.font, title);
        game.font.draw(game.batch, title, (viewport.getWorldWidth() - glyphLayout.width) / 2f,
                      viewport.getWorldHeight() - 20);

        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.WHITE);

        float rightX = gridStartX + GRID_WORLD_SIZE + 30;
        float currentY = viewport.getWorldHeight() - 80;

        game.font.setColor(Color.CYAN);
        game.font.draw(game.batch, currentPlayer.getName(), rightX, currentY);
        currentY -= 30;

        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "Place your BRUNNEN", rightX, currentY);
        currentY -= 25;
        game.font.draw(game.batch, "on a highlighted field.", rightX, currentY);
        currentY -= 40;

        game.font.setColor(Color.LIGHT_GRAY);
        game.font.getData().setScale(0.9f);
        game.font.draw(game.batch, "Brunnen Rule:", rightX, currentY);
        currentY -= 20;
        game.font.draw(game.batch, "Cover all 4 corners", rightX, currentY);
        currentY -= 20;
        game.font.draw(game.batch, "= +4 bonus points!", rightX, currentY);

        game.font.getData().setScale(1.0f);
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
    public void dispose() {}
}
