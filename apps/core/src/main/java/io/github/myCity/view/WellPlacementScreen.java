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
import io.github.myCity.model.Cell;
import io.github.myCity.model.TerrainType;
import io.github.myCity.model.Position;
import io.github.myCity.model.Game;
import io.github.myCity.model.Player;
import io.github.myCity.model.CityGrid;
import io.github.myCity.model.Sticker;
import io.github.myCity.model.StickerType;
import io.github.myCity.view.UIStyle;

import java.util.HashSet;
import java.util.Set;

/**
 * Screen for placing Well stickers before Episode 3.
 * Each player places one Well on their board.
 */
public class WellPlacementScreen implements Screen {
    private final Main game;
    private final Game gameController;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private static final int GRID_SIZE = 11;
    private static final float TILE_SIZE = 40;
    private static final float GRID_WORLD_SIZE = GRID_SIZE * TILE_SIZE;

    // Valid Well positions (same as sticker positions)
    private static final Set<Position> VALID_WELL_POSITIONS = new HashSet<>();
    static {
        // Row x=5
        VALID_WELL_POSITIONS.add(new Position(5, 3));
        VALID_WELL_POSITIONS.add(new Position(5, 4));
        VALID_WELL_POSITIONS.add(new Position(5, 5));
        VALID_WELL_POSITIONS.add(new Position(5, 6));
        VALID_WELL_POSITIONS.add(new Position(5, 7));
        VALID_WELL_POSITIONS.add(new Position(5, 8));
        VALID_WELL_POSITIONS.add(new Position(5, 9));
        VALID_WELL_POSITIONS.add(new Position(5, 10));
        // Row x=6
        VALID_WELL_POSITIONS.add(new Position(6, 3));
        VALID_WELL_POSITIONS.add(new Position(6, 4));
        VALID_WELL_POSITIONS.add(new Position(6, 5));
        VALID_WELL_POSITIONS.add(new Position(6, 6));
        VALID_WELL_POSITIONS.add(new Position(6, 8));
        VALID_WELL_POSITIONS.add(new Position(6, 9));
        VALID_WELL_POSITIONS.add(new Position(6, 10));
        // Row x=7
        VALID_WELL_POSITIONS.add(new Position(7, 3));
        VALID_WELL_POSITIONS.add(new Position(7, 4));
        VALID_WELL_POSITIONS.add(new Position(7, 5));
        VALID_WELL_POSITIONS.add(new Position(7, 6));
        VALID_WELL_POSITIONS.add(new Position(7, 7));
        VALID_WELL_POSITIONS.add(new Position(7, 8));
        VALID_WELL_POSITIONS.add(new Position(7, 9));
        VALID_WELL_POSITIONS.add(new Position(7, 10));
        // Row x=8
        VALID_WELL_POSITIONS.add(new Position(8, 6));
        VALID_WELL_POSITIONS.add(new Position(8, 7));
        VALID_WELL_POSITIONS.add(new Position(8, 8));
        VALID_WELL_POSITIONS.add(new Position(8, 10));
    }

    private int currentPlayerIndex = 0;
    private boolean player1Placed = false;
    private boolean player2Placed = false;

    private float gridStartX;
    private float gridStartY;

    public WellPlacementScreen(Main game, Game gameController) {
        this.game = game;
        this.gameController = gameController;

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

                if (isValidWellPosition(clickedPos)) {
                    placeWell(clickedPos);
                    return true;
                }

                return false;
            }
        });
    }

    private boolean isValidWellPosition(Position pos) {
        for (Position validPos : VALID_WELL_POSITIONS) {
            if (validPos.getX() == pos.getX() && validPos.getY() == pos.getY()) {
                CityGrid cityGrid = gameController.getPlayers()[currentPlayerIndex].getCityGrid();
                Cell cell = cityGrid.getCell(pos.getX(), pos.getY());
                if (cell != null) {
                    if (cell.getTerrain() != TerrainType.MEADOW) {
                        return false;
                    }
                    // Cannot place Well next to a river
                    if (isAdjacentToRiver(cityGrid, pos.getX(), pos.getY())) {
                        return false;
                    }
                    for (Sticker s : cityGrid.getStickers()) {
                        if (s.getPosition().getX() == pos.getX() && s.getPosition().getY() == pos.getY()) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the given position is adjacent to a river.
     * A field is adjacent to a river if it or any of its neighbors have river edges.
     */
    private boolean isAdjacentToRiver(CityGrid cityGrid, int x, int y) {
        Cell cell = cityGrid.getCell(x, y);
        if (cell != null && cell.hasRiver()) {
            return true;
        }

        // Check all 4 neighbors for river edges
        Cell top = cityGrid.getCell(x, y + 1);
        Cell bottom = cityGrid.getCell(x, y - 1);
        Cell left = cityGrid.getCell(x - 1, y);
        Cell right = cityGrid.getCell(x + 1, y);

        if (top != null && top.hasRiver()) return true;
        if (bottom != null && bottom.hasRiver()) return true;
        if (left != null && left.hasRiver()) return true;
        if (right != null && right.hasRiver()) return true;

        return false;
    }

    private void placeWell(Position pos) {
        Player player = gameController.getPlayers()[currentPlayerIndex];
        Sticker well = new Sticker(StickerType.WELL, pos, "well_episode3");
        player.getCityGrid().addSticker(well);

        Gdx.app.log("WellPlacement", player.getName() + " placed WELL at (" +
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
        Gdx.app.log("WellPlacement", "Both players placed Well. Starting Episode 3...");
        gameController.resetEpisodeTracking();
        gameController.loadEpisode(3, true);
        game.setScreen(new GameScreen(game, gameController));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(UIStyle.BACKGROUND.r, UIStyle.BACKGROUND.g, UIStyle.BACKGROUND.b, 1f);
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

        CityGrid cityGrid = gameController.getPlayers()[currentPlayerIndex].getCityGrid();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                Cell cell = cityGrid.getCell(x, y);
                if (cell != null) {
                    game.shapeRenderer.setColor(getColorForTerrainType(cell.getTerrain()));
                    game.shapeRenderer.rect(gridStartX + x * TILE_SIZE, gridStartY + y * TILE_SIZE,
                                           TILE_SIZE, TILE_SIZE);
                }
            }
        }
        game.shapeRenderer.end();

        // Highlight valid Well positions
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color highlightColor = new Color(UIStyle.ACCENT.r, UIStyle.ACCENT.g, UIStyle.ACCENT.b, 0.28f);

        for (Position pos : VALID_WELL_POSITIONS) {
            if (isValidWellPosition(pos)) {
                game.shapeRenderer.setColor(highlightColor);
                game.shapeRenderer.rect(gridStartX + pos.getX() * TILE_SIZE + 2,
                                       gridStartY + pos.getY() * TILE_SIZE + 2,
                                       TILE_SIZE - 4, TILE_SIZE - 4);
            }
        }
        game.shapeRenderer.end();

        // Draw grid lines
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(UIStyle.DIVIDER);
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
        Player currentPlayer = gameController.getPlayers()[currentPlayerIndex];

        game.font.setColor(UIStyle.PRIMARY_TEXT);
        game.font.getData().setScale(1.5f);

        String title = "Episode 3 - Place Well";
        glyphLayout.setText(game.font, title);
        game.font.draw(game.batch, title, (viewport.getWorldWidth() - glyphLayout.width) / 2f,
                      viewport.getWorldHeight() - 20);

        game.font.getData().setScale(1.0f);
        game.font.setColor(UIStyle.PRIMARY_TEXT);

        float rightX = gridStartX + GRID_WORLD_SIZE + 30;
        float currentY = viewport.getWorldHeight() - 80;

        game.font.setColor(UIStyle.PRIMARY_TEXT);
        game.font.draw(game.batch, currentPlayer.getName(), rightX, currentY);
        currentY -= 30;

        game.font.setColor(UIStyle.PRIMARY_TEXT);
        game.font.draw(game.batch, "Place your WELL", rightX, currentY);
        currentY -= 25;
        game.font.draw(game.batch, "on a highlighted field.", rightX, currentY);
        currentY -= 40;

        game.font.setColor(UIStyle.SECONDARY_TEXT);
        game.font.getData().setScale(0.9f);
        game.font.draw(game.batch, "Well Rule:", rightX, currentY);
        currentY -= 20;
        game.font.draw(game.batch, "Cover all 4 corners", rightX, currentY);
        currentY -= 20;
        game.font.draw(game.batch, "= +4 bonus points!", rightX, currentY);

        game.font.getData().setScale(1.0f);
    }

    private Color getColorForTerrainType(TerrainType type) {
        switch (type) {
            case MEADOW:
                return Color.GREEN;
            case TREE:
                return Color.FOREST;
            case ROCK:
                return Color.GRAY;
            case MOUNTAIN:
                return Color.DARK_GRAY;
            case FOREST:
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
