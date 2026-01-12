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
import io.github.myCity.model.BuildingColor;
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
 * Screen for placing stickers after episodes 1-3.
 * Winner places a ROCK sticker (penalty), loser places a TREE sticker (bonus).
 */
public class StickerPlacementScreen implements Screen {
    private final Main game;
    private final Spiel spiel;
    private final int completedEpisode;
    private final int winnerIndex;
    private final int loserIndex;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private static final int GRID_SIZE = 11;
    private static final float TILE_SIZE = 40;
    private static final float GRID_WORLD_SIZE = GRID_SIZE * TILE_SIZE;

    // Valid sticker positions for Chapter 1
    private static final Set<Position> VALID_STICKER_POSITIONS = new HashSet<>();
    static {
        // Row x=5
        VALID_STICKER_POSITIONS.add(new Position(5, 3));
        VALID_STICKER_POSITIONS.add(new Position(5, 4));
        VALID_STICKER_POSITIONS.add(new Position(5, 5));
        VALID_STICKER_POSITIONS.add(new Position(5, 6));
        VALID_STICKER_POSITIONS.add(new Position(5, 7));
        VALID_STICKER_POSITIONS.add(new Position(5, 8));
        VALID_STICKER_POSITIONS.add(new Position(5, 9));
        VALID_STICKER_POSITIONS.add(new Position(5, 10));
        // Row x=6
        VALID_STICKER_POSITIONS.add(new Position(6, 3));
        VALID_STICKER_POSITIONS.add(new Position(6, 4));
        VALID_STICKER_POSITIONS.add(new Position(6, 5));
        VALID_STICKER_POSITIONS.add(new Position(6, 6));
        // Note: (6,7) is NOT included
        VALID_STICKER_POSITIONS.add(new Position(6, 8));
        VALID_STICKER_POSITIONS.add(new Position(6, 9));
        VALID_STICKER_POSITIONS.add(new Position(6, 10));
        // Row x=7
        VALID_STICKER_POSITIONS.add(new Position(7, 3));
        VALID_STICKER_POSITIONS.add(new Position(7, 4));
        VALID_STICKER_POSITIONS.add(new Position(7, 5));
        VALID_STICKER_POSITIONS.add(new Position(7, 6));
        VALID_STICKER_POSITIONS.add(new Position(7, 7));
        VALID_STICKER_POSITIONS.add(new Position(7, 8));
        VALID_STICKER_POSITIONS.add(new Position(7, 9));
        VALID_STICKER_POSITIONS.add(new Position(7, 10));
        // Row x=8
        VALID_STICKER_POSITIONS.add(new Position(8, 6));
        VALID_STICKER_POSITIONS.add(new Position(8, 7));
        VALID_STICKER_POSITIONS.add(new Position(8, 8));
        // Note: (8,9) is NOT included
        VALID_STICKER_POSITIONS.add(new Position(8, 10));
    }

    // Current placement state
    private boolean winnerPlaced = false;
    private boolean loserPlaced = false;
    private int currentPlacingPlayer; // Index of player currently placing
    private StickerTyp currentStickerType;
    private String currentPlayerRole; // "winner" or "loser"

    // Grid positions on screen
    private float gridStartX;
    private float gridStartY;

    public StickerPlacementScreen(Main game, Spiel spiel, int completedEpisode, int winnerIndex) {
        this.game = game;
        this.spiel = spiel;
        this.completedEpisode = completedEpisode;
        this.winnerIndex = winnerIndex;
        this.loserIndex = (winnerIndex == 0) ? 1 : 0;

        camera = new OrthographicCamera();
        viewport = new FitViewport(GRID_WORLD_SIZE + 350, GRID_WORLD_SIZE + 150, camera);

        // Winner places first (ROCK sticker - penalty)
        currentPlacingPlayer = winnerIndex;
        currentStickerType = StickerTyp.FELS_HINZUFÜGEN;
        currentPlayerRole = "winner";
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 worldPos = viewport.unproject(new Vector2(screenX, screenY));

                // Convert to grid position
                int gridX = (int) ((worldPos.x - gridStartX) / TILE_SIZE);
                int gridY = (int) ((worldPos.y - gridStartY) / TILE_SIZE);

                Position clickedPos = new Position(gridX, gridY);

                // Check if valid position
                if (isValidStickerPosition(clickedPos)) {
                    placeSticker(clickedPos);
                    return true;
                }

                return false;
            }
        });
    }

    private boolean isValidStickerPosition(Position pos) {
        // Check if position is in valid set
        for (Position validPos : VALID_STICKER_POSITIONS) {
            if (validPos.getX() == pos.getX() && validPos.getY() == pos.getY()) {
                // Check if position doesn't already have a sticker
                Stadtplan stadtplan = spiel.getSpieler()[currentPlacingPlayer].getStadtplan();
                Feld feld = stadtplan.getFeld(pos.getX(), pos.getY());
                if (feld != null) {
                    // Check terrain - only allow on WIESE
                    if (feld.getGelände() != Geländetyp.WIESE) {
                        return false;
                    }
                    // Check for existing stickers
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

    private void placeSticker(Position pos) {
        Spieler player = spiel.getSpieler()[currentPlacingPlayer];
        String description = currentStickerType == StickerTyp.FELS_HINZUFÜGEN ? "rock_chapter1" : "tree_chapter1";
        Sticker sticker = new Sticker(currentStickerType, pos, description);
        player.getStadtplan().addSticker(sticker);

        Gdx.app.log("StickerPlacement", player.getName() + " placed " +
                   (currentStickerType == StickerTyp.FELS_HINZUFÜGEN ? "ROCK" : "TREE") +
                   " sticker at (" + pos.getX() + ", " + pos.getY() + ")");

        if (!winnerPlaced) {
            // Winner just placed, now loser's turn
            winnerPlaced = true;
            currentPlacingPlayer = loserIndex;
            currentStickerType = StickerTyp.BAUM_HINZUFÜGEN;
            currentPlayerRole = "loser";
        } else {
            // Loser placed, proceed to next episode
            loserPlaced = true;
            proceedToNextEpisode();
        }
    }

    private void proceedToNextEpisode() {
        // Clear both players' boards (but stickers remain)
        for (Spieler player : spiel.getSpieler()) {
            player.getStadtplan().clearBuildings();
            player.resetÜbersprungeneGebäude();
        }

        int nextEpisode = completedEpisode + 1;

        if (nextEpisode > 3) {
            // Chapter 1 complete - go to chapter end summary
            Gdx.app.log("StickerPlacement", "Chapter 1 complete! Moving to Chapter 2...");
            // Load episode 4 and continue
            spiel.resetEpisodeTracking();
            spiel.kapitelLaden(4, true);
            game.setScreen(new GameScreen(game, spiel));
        } else if (nextEpisode == 3) {
            // Before Episode 3, each player places a Brunnen
            Gdx.app.log("StickerPlacement", "Going to Brunnen placement before Episode 3");
            game.setScreen(new BrunnenPlacementScreen(game, spiel));
        } else {
            // Continue to next episode in chapter 1
            Gdx.app.log("StickerPlacement", "Loading Episode " + nextEpisode);
            spiel.resetEpisodeTracking();
            spiel.kapitelLaden(nextEpisode, true);
            game.setScreen(new GameScreen(game, spiel));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.12f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw the current player's grid
        drawGrid();

        // Draw UI text
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        drawUI();
        game.batch.end();
    }

    private void drawGrid() {
        gridStartX = 50;
        gridStartY = (viewport.getWorldHeight() - GRID_WORLD_SIZE) / 2f;

        Stadtplan stadtplan = spiel.getSpieler()[currentPlacingPlayer].getStadtplan();

        // Draw base terrain
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

        // Highlight valid sticker positions
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color highlightColor = currentStickerType == StickerTyp.FELS_HINZUFÜGEN ?
                              new Color(0.5f, 0.5f, 0.5f, 0.5f) : new Color(0.2f, 0.8f, 0.2f, 0.5f);

        for (Position pos : VALID_STICKER_POSITIONS) {
            if (isValidStickerPosition(pos)) {
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
        Spieler currentPlayer = spiel.getSpieler()[currentPlacingPlayer];
        String stickerName = currentStickerType == StickerTyp.FELS_HINZUFÜGEN ? "ROCK" : "TREE";

        game.font.setColor(Color.GOLD);
        game.font.getData().setScale(1.5f);

        String title = "Episode " + completedEpisode + " Complete - Sticker Placement";
        glyphLayout.setText(game.font, title);
        game.font.draw(game.batch, title, (viewport.getWorldWidth() - glyphLayout.width) / 2f,
                      viewport.getWorldHeight() - 20);

        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.WHITE);

        // Instructions on the right side
        float rightX = gridStartX + GRID_WORLD_SIZE + 30;
        float currentY = viewport.getWorldHeight() - 80;

        String playerInfo = currentPlayer.getName();
        if (currentPlayerRole.equals("winner")) {
            playerInfo += " (Winner)";
            game.font.setColor(Color.YELLOW);
        } else {
            playerInfo += " (Runner-up)";
            game.font.setColor(Color.CYAN);
        }
        game.font.draw(game.batch, playerInfo, rightX, currentY);
        currentY -= 30;

        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "Place a " + stickerName + " sticker", rightX, currentY);
        currentY -= 25;

        game.font.draw(game.batch, "on a highlighted field.", rightX, currentY);
        currentY -= 40;

        // Explain the rule
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.getData().setScale(0.9f);
        if (currentStickerType == StickerTyp.FELS_HINZUFÜGEN) {
            game.font.draw(game.batch, "Rock = -1 point if visible", rightX, currentY);
        } else {
            game.font.draw(game.batch, "Tree = +1 point if visible", rightX, currentY);
        }
        currentY -= 50;

        // Show scores
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.0f);
        game.font.draw(game.batch, "Current Scores:", rightX, currentY);
        currentY -= 25;

        for (int i = 0; i < 2; i++) {
            Spieler p = spiel.getSpieler()[i];
            String scoreText = p.getName() + ": " + p.getPunktzahl();
            game.font.draw(game.batch, scoreText, rightX, currentY);
            currentY -= 20;
        }
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
