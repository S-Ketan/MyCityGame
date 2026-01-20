package io.github.myCity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.myCity.Main;
import io.github.myCity.model.BuildingCard;
import io.github.myCity.model.BuildingColor;
import io.github.myCity.model.Cell;
import io.github.myCity.model.CityGrid;
import io.github.myCity.model.Game;
import io.github.myCity.model.Player;
import io.github.myCity.model.Position;
import io.github.myCity.model.StickerType;
import io.github.myCity.model.TerrainType;
import io.github.myCity.model.Tile;

public class GameScreen implements Screen {
    private final Main game;
    private static final int GRID_SIZE = 11;
    private static final float TILE_SIZE = 50;
    private static final float GRID_WORLD_SIZE = GRID_SIZE * TILE_SIZE;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Game gameController;
    private BuildingCard currentCard;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final Rectangle skipButton;
    private final Rectangle rotateButton;
    
    // Drag & Drop state
    private boolean isDragging = false;
    private Vector2 dragOffset = new Vector2();
    private Vector2 tilePosition = new Vector2();
    private boolean isFirstTileForPlayer = true;
    private int currentPlayerTileCount = 0;
    
    // Grid positioning
    private float gridStartX = 50;
    private float gridStartY;
    
    // Chapter end dialog
    private boolean showChapterEndDialog = false;
    private Rectangle saveButton;
    private Rectangle continueButton;
    private final Rectangle previewBox = new Rectangle();

    public GameScreen(Main game, String player1Name, String player2Name) {
        this(game, player1Name, player2Name, 1);
    }

    public GameScreen(Main game, String player1Name, String player2Name, int startEpisode) {
        this.game = game;
        this.gameController = new Game();
        this.gameController.startNewGame(player1Name, player2Name);
        if (startEpisode > 1) {
            this.gameController.loadEpisode(startEpisode);
        }
        this.currentCard = gameController.getCurrentEpisode().drawCard();

        camera = new OrthographicCamera();
        viewport = new FitViewport(GRID_WORLD_SIZE + 300, GRID_WORLD_SIZE + 100, camera);

        skipButton = new Rectangle(0, 0, 140, 40);
        rotateButton = new Rectangle(0, 0, 140, 40);
        saveButton = new Rectangle(0, 0, 180, 50);
        continueButton = new Rectangle(0, 0, 180, 50);

        // Initialize tile position (right side of screen)
        resetTilePosition();
    }

    /**
     * Constructor for continuing an existing game (e.g., after chapter end screen).
     */
    public GameScreen(Main game, Game existingGame) {
        this.game = game;
        this.gameController = existingGame;
        this.currentCard = gameController.getCurrentEpisode().drawCard();
        this.currentPlayerTileCount = 0;
        this.isFirstTileForPlayer = true;

        camera = new OrthographicCamera();
        viewport = new FitViewport(GRID_WORLD_SIZE + 300, GRID_WORLD_SIZE + 100, camera);

        skipButton = new Rectangle(0, 0, 140, 40);
        rotateButton = new Rectangle(0, 0, 140, 40);
        saveButton = new Rectangle(0, 0, 180, 50);
        continueButton = new Rectangle(0, 0, 180, 50);

        // Initialize tile position (right side of screen)
        resetTilePosition();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (showChapterEndDialog) return false;

                if (keycode == Input.Keys.Q || keycode == Input.Keys.E) {
                    rotateTile();
                    return true;
                }
                if (keycode == Input.Keys.P) {
                    skipTile();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.LEFT) return false;
                
                Vector2 worldPos = screenToWorld(screenX, screenY);
                
                //Gdx.app.log("Input", "Touch down at screen (" + screenX + ", " + screenY + ") = world (" + worldPos.x + ", " + worldPos.y + ")");
                
                // Check if chapter end dialog is shown
                if (showChapterEndDialog) {
                    handleChapterEndDialogInput(worldPos);
                    return true;
                }
                
                // Check rotate button
                if (rotateButton.contains(worldPos.x, worldPos.y)) {
                    rotateTile();
                    return true;
                }
                
                // Check skip button
                if (skipButton.contains(worldPos.x, worldPos.y)) {
                    skipTile();
                    return true;
                }
                
                // Check if clicking on the tile
                if (isTileClicked(worldPos)) {
                    isDragging = true;
                    // When starting to drag, center the full-size tile on the click position
                    if (currentCard != null && currentCard.getTile() != null) {
                        Tile tile = currentCard.getTile();
                        float fullWidth = tile.getWidth() * TILE_SIZE;
                        float fullHeight = tile.getHeight() * TILE_SIZE;
                        tilePosition.set(worldPos.x - fullWidth / 2f, worldPos.y - fullHeight / 2f);
                    }
                    dragOffset.set(worldPos.x - tilePosition.x, worldPos.y - tilePosition.y);
                    return true;
                }
                
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (isDragging && currentCard != null) {
                    Vector2 worldPos = screenToWorld(screenX, screenY);
                    tilePosition.set(worldPos.x - dragOffset.x, worldPos.y - dragOffset.y);
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.LEFT) return false;
                
                if (isDragging) {
                    isDragging = false;
                    Vector2 worldPos = screenToWorld(screenX, screenY);
                    //Gdx.app.log("Input", "Touch up at world (" + worldPos.x + ", " + worldPos.y + ")");
                    //Gdx.app.log("Input", "Tile position: (" + tilePosition.x + ", " + tilePosition.y + ")");
                    tryPlaceTile(worldPos);
                    return true;
                }
                return false;
            }
        });
    }

    private Vector2 screenToWorld(int screenX, int screenY) {
        // Use viewport to properly convert screen coordinates to world coordinates
        Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));
        return worldCoords;
    }

    private boolean isTileClicked(Vector2 worldPos) {
        if (currentCard == null) return false;

        Tile tile = currentCard.getTile();
        if (tile == null) return false;

        boolean[][] form = tile.getShape();

        // Use scaled size when checking clicks in preview box
        float checkTileSize = isDragging ? TILE_SIZE : TILE_SIZE * previewTileScale;

        // Check if any cell of the tile is clicked
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    float cellX = tilePosition.x + j * checkTileSize;
                    float cellY = tilePosition.y + i * checkTileSize;

                    if (worldPos.x >= cellX && worldPos.x <= cellX + checkTileSize &&
                        worldPos.y >= cellY && worldPos.y <= cellY + checkTileSize) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void tryPlaceTile(Vector2 worldPos) {
        if (currentCard == null) return;
        
        Tile tile = currentCard.getTile();
        if (tile == null) return;
        
        // Convert tile position (top-left corner) to grid coordinates
        int gridX = Math.round((tilePosition.x - gridStartX) / TILE_SIZE);
        int gridY = Math.round((tilePosition.y - gridStartY) / TILE_SIZE);
        
        //Gdx.app.log("Game", "Tile position in world: (" + tilePosition.x + ", " + tilePosition.y + ")");
        //Gdx.app.log("Game", "Grid start: (" + gridStartX + ", " + gridStartY + ")");
        //Gdx.app.log("Game", "Trying to place tile at grid position: (" + gridX + ", " + gridY + ")");
        
        // Check if tile would be mostly outside the grid - if so, return to original position
        boolean[][] form = tile.getShape();
        int validCells = 0;
        int totalCells = 0;
        
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    totalCells++;
                    int cellGridX = gridX + j;
                    int cellGridY = gridY + i;
                    if (cellGridX >= 0 && cellGridX < GRID_SIZE && cellGridY >= 0 && cellGridY < GRID_SIZE) {
                        validCells++;
                    }
                }
            }
        }
        
        // If more than half the cells are outside, don't try to place
        if (validCells < totalCells / 2) {
            resetTilePosition();
            //Gdx.app.log("Game", "Tile mostly out of bounds (" + validCells + "/" + totalCells + " cells valid)");
            return;
        }
        
        Position pos = new Position(gridX, gridY);
        
        // Check if placement is valid using game rules
        boolean isValid = gameController.getCurrentEpisode().isPlacementValid(
            tile,
            gameController.getCurrentPlayer().getCityGrid(),
            pos,
            isFirstTileForPlayer
        );
        
        if (isValid) {
            // Place the tile
            gameController.getCurrentPlayer().placeTile(tile, pos);
            //Gdx.app.log("Game", "✓ Tile placed successfully at (" + gridX + ", " + gridY + ")");

            // Update score to reflect placement
            gameController.refreshScore(gameController.getCurrentPlayerIndex());
            
            isFirstTileForPlayer = false;
            currentPlayerTileCount++;
            
            // Check if current player finished all 24 tiles
            if (currentPlayerTileCount >= 24) {
                handlePlayerFinished();
            } else {
                // Draw next tile for same player
                currentCard = gameController.getCurrentEpisode().drawCard();
                resetTilePosition();
            }
        } else {
            // Invalid placement - return tile to original position
            resetTilePosition();
            //Gdx.app.log("Game", "✗ Invalid tile placement");
        }
    }



    private void handlePlayerFinished() {
        Player currentPlayer = gameController.getCurrentPlayer();
        int currentPlayerIndex = gameController.getCurrentPlayerIndex();
        
        Gdx.app.log("Game", currentPlayer.getName() + " (Player " + (currentPlayerIndex + 1) + ") finished all 24 tiles!");
        
        // Check if this is player 1 finishing (index 0)
        if (currentPlayerIndex == 0) {
            // Player 1 finished, now it's Player 2's turn
            Gdx.app.log("Game", "Switching to Player 2...");

            // Reset deck WITHOUT loading a new chapter
            // We need to reset the deck to have the same 24 tiles for player 2
            currentPlayerTileCount = 0;
            isFirstTileForPlayer = true;

            // Switch to player 2 BEFORE resetting the deck
            gameController.switchPlayer();

            // Reset the deck to the original order for player 2 (same tiles as player 1)
            gameController.resetDeckForNextPlayer();
            currentCard = gameController.getCurrentEpisode().drawCard();
            resetTilePosition();

            Gdx.app.log("Game", "Now it's " + gameController.getCurrentPlayer().getName() + "'s turn with the same deck!");
        } else {
            // Player 2 finished, chapter is complete
            Gdx.app.log("Game", "Both players finished! Chapter complete!");
            endChapter();
        }
    }

    private void endChapter() {
        int chapterNumber = gameController.getCurrentEpisodeNumber();
        Gdx.app.log("Game", "Chapter " + chapterNumber + " completed!");

        // Transition to Chapter End Screen
        game.setScreen(new ChapterEndScreen(game, gameController, chapterNumber));
    }

    private void skipTile() {
        if (currentCard == null) return;
        
        //Gdx.app.log("Game", gameController.getCurrentPlayer().getName() + " skipped tile #" + (currentPlayerTileCount + 1));
        
        // Apply skip penalty if needed (handled by game logic)
        gameController.getCurrentPlayer().skipTile();
        
        currentPlayerTileCount++;
        
        if (currentPlayerTileCount >= 24) {
            handlePlayerFinished();
        } else {
            // Draw next card for the SAME player
            currentCard = gameController.getCurrentEpisode().drawCard();
            
            if (currentCard != null) {
                resetTilePosition();
                //Gdx.app.log("Game", "Drew next tile for " + gameController.getCurrentPlayer().getName());
            } else {
                //Gdx.app.log("Game", "ERROR: No more cards in deck!");
            }
        }
    }

    private void rotateTile() {
        if (currentCard != null && currentCard.getTile() != null) {
            currentCard.getTile().rotate();
            //Gdx.app.log("Game", "Tile rotated");
            if (!isDragging) {
                resetTilePosition();
            }
        }
    }

    private float previewTileScale = 1.0f;

    private void resetTilePosition() {
        Rectangle box = getPreviewBox();
        if (currentCard != null && currentCard.getTile() != null) {
            Tile tile = currentCard.getTile();
            float tileWidth = tile.getWidth() * TILE_SIZE;
            float tileHeight = tile.getHeight() * TILE_SIZE;

            // Calculate scale to fit tile within preview box with padding
            float maxWidth = box.width - 20;
            float maxHeight = box.height - 20;
            float scaleX = maxWidth / tileWidth;
            float scaleY = maxHeight / tileHeight;
            previewTileScale = Math.min(1.0f, Math.min(scaleX, scaleY));

            // Center tile in preview box using scaled dimensions
            float scaledWidth = tileWidth * previewTileScale;
            float scaledHeight = tileHeight * previewTileScale;
            tilePosition.x = box.x + (box.width - scaledWidth) / 2f;
            tilePosition.y = box.y + (box.height - scaledHeight) / 2f;
        }
    }

    private void handleChapterEndDialogInput(Vector2 worldPos) {
        if (saveButton.contains(worldPos.x, worldPos.y)) {
            gameController.saveGameState();
            Gdx.app.log("Game", "Game saved!");
            startNextChapter();
        } else if (continueButton.contains(worldPos.x, worldPos.y)) {
            Gdx.app.log("Game", "Continuing without saving");
            startNextChapter();
        }
    }

    private void startNextChapter() {
        showChapterEndDialog = false;
        
        int nextChapter = gameController.getCurrentEpisodeNumber() + 1;
        if (nextChapter > 24) {
            // Game completed!
            gameController.calculateFinalScores();
            Gdx.app.log("Game", "All chapters completed! Returning to menu...");
            // TODO: Return to main menu
            return;
        }
        
        // Reset for next chapter
        currentPlayerTileCount = 0;
        isFirstTileForPlayer = true;
        gameController.resetEpisodeTracking();
        
        // Load next chapter
        gameController.loadEpisode(nextChapter, true);
        currentCard = gameController.getCurrentEpisode().drawCard();
        resetTilePosition();
        
        Gdx.app.log("Game", "Starting Chapter " + nextChapter);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(UIStyle.BACKGROUND.r, UIStyle.BACKGROUND.g, UIStyle.BACKGROUND.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw grid
        drawGrid(gameController.getCurrentPlayer().getCityGrid());

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (showChapterEndDialog) {
            drawChapterEndDialog();
        } else {
            drawGameUI();
        }

        game.batch.end();

        // Draw current tile AFTER UI panel (either at original position or being dragged)
        drawCurrentTile();

        // Draw buttons
        if (showChapterEndDialog) {
            drawChapterEndButtons();
        } else {
            drawGameButtons();
        }
    }

    private void drawGameUI() {
        Player currentPlayer = gameController.getCurrentPlayer();
        Player[] players = gameController.getPlayers();

        // Panel dimensions - increased separation from game board
        float panelX = gridStartX + GRID_WORLD_SIZE + 25;
        float panelBottomPadding = 15;
        float panelY = gridStartY - panelBottomPadding; // Extend panel down to fit buttons
        float panelWidth = viewport.getWorldWidth() - panelX - 8;
        float panelHeight = GRID_WORLD_SIZE + panelBottomPadding; // Taller panel
        float padding = 18;
        float textX = panelX + padding + 3;

        // Draw main info panel background (extended to fit buttons)
        game.batch.end();
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(UIStyle.PANEL);
        UIStyle.drawRoundedRect(game.shapeRenderer, panelX, panelY, panelWidth, panelHeight, UIStyle.CORNER_RADIUS);
        game.shapeRenderer.end();

        // === BOTTOM SECTION (fixed positions from bottom) ===

        // Buttons at bottom
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(UIStyle.BUTTON);
        UIStyle.drawRoundedRect(game.shapeRenderer, rotateButton.x, rotateButton.y, rotateButton.width, rotateButton.height, UIStyle.BUTTON_CORNER_RADIUS);
        UIStyle.drawRoundedRect(game.shapeRenderer, skipButton.x, skipButton.y, skipButton.width, skipButton.height, UIStyle.BUTTON_CORNER_RADIUS);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(UIStyle.BUTTON_BORDER);
        UIStyle.drawRoundedRectBorder(game.shapeRenderer, rotateButton.x, rotateButton.y, rotateButton.width, rotateButton.height, UIStyle.BUTTON_CORNER_RADIUS);
        UIStyle.drawRoundedRectBorder(game.shapeRenderer, skipButton.x, skipButton.y, skipButton.width, skipButton.height, UIStyle.BUTTON_CORNER_RADIUS);
        game.shapeRenderer.end();

        // Tile Preview box (above buttons)
        Rectangle preview = getPreviewBox();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(UIStyle.SECONDARY_SOFT);
        UIStyle.drawRoundedRect(game.shapeRenderer, preview.x, preview.y, preview.width, preview.height, UIStyle.BUTTON_CORNER_RADIUS);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(UIStyle.DIVIDER);
        UIStyle.drawRoundedRectBorder(game.shapeRenderer, preview.x, preview.y, preview.width, preview.height, UIStyle.BUTTON_CORNER_RADIUS);
        game.shapeRenderer.end();

        // === TOP SECTION (flows down from top) ===
        game.batch.begin();
        float textY = panelY + panelHeight - 25;

        // Title
        game.font.setColor(UIStyle.PRIMARY_TEXT);
        game.font.getData().setScale(1.15f);
        game.font.draw(game.batch, "My City", textX, textY);
        textY -= 30;

        // Divider
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(UIStyle.DIVIDER);
        UIStyle.drawRoundedRect(game.shapeRenderer, panelX + padding, textY, panelWidth - padding * 2, 2, 1);
        game.shapeRenderer.end();
        game.batch.begin();
        textY -= 18;

        // Current Player with accent dot
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(UIStyle.ACCENT);
        game.shapeRenderer.circle(textX - 8f, textY - 5f, 4f);
        game.shapeRenderer.end();
        game.batch.begin();

        game.font.setColor(UIStyle.PRIMARY_TEXT);
        game.font.getData().setScale(0.9f);
        game.font.draw(game.batch, currentPlayer.getName(), textX, textY);
        textY -= 22;

        // Episode and Tile count
        game.font.setColor(UIStyle.SECONDARY_TEXT);
        game.font.getData().setScale(0.8f);
        game.font.draw(game.batch, "Episode " + gameController.getCurrentEpisodeNumber(), textX, textY);
        String deckText = "Tile " + (currentPlayerTileCount + 1) + "/24";
        glyphLayout.setText(game.font, deckText);
        game.font.draw(game.batch, deckText, panelX + panelWidth - padding - glyphLayout.width, textY);
        textY -= 22;

        // Divider
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(UIStyle.DIVIDER);
        UIStyle.drawRoundedRect(game.shapeRenderer, panelX + padding, textY, panelWidth - padding * 2, 2, 1);
        game.shapeRenderer.end();
        game.batch.begin();
        textY -= 18;

        // Progress section header
        game.font.setColor(UIStyle.PRIMARY_TEXT);
        game.font.getData().setScale(0.85f);
        game.font.draw(game.batch, "Progress", textX, textY);
        textY -= 22;

        // Player progress bars
        for (int i = 0; i < 2; i++) {
            Player player = players[i];
            boolean isCurrent = player.equals(currentPlayer);

            // Player dot
            game.batch.end();
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(isCurrent ? UIStyle.ACCENT : UIStyle.BUTTON);
            game.shapeRenderer.circle(textX - 8f, textY - 5f, 3.5f);
            game.shapeRenderer.end();
            game.batch.begin();

            // Player name and progress percentage on same line
            game.font.setColor(isCurrent ? UIStyle.PRIMARY_TEXT : UIStyle.SECONDARY_TEXT);
            game.font.getData().setScale(0.75f);
            game.font.draw(game.batch, player.getName(), textX, textY);

            // Progress percentage aligned right (instead of score)
            String progressText = player.getProgressPoints() + "/100";
            glyphLayout.setText(game.font, progressText);
            game.font.draw(game.batch, progressText, panelX + panelWidth - padding - glyphLayout.width, textY);
            textY -= 18;

            // Progress bar
            game.batch.end();
            float barWidth = panelWidth - padding * 2;
            float barHeight = 8;
            float barX = panelX + padding;
            float barY = textY - 2;

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(UIStyle.SECONDARY);
            UIStyle.drawRoundedRect(game.shapeRenderer, barX, barY, barWidth, barHeight, 4);
            float fillWidth = (player.getProgressPoints() / 100f) * barWidth;
            if (fillWidth > 8) {
                game.shapeRenderer.setColor(isCurrent ? UIStyle.ACCENT : UIStyle.BUTTON);
                UIStyle.drawRoundedRect(game.shapeRenderer, barX, barY, fillWidth, barHeight, 4);
            }
            game.shapeRenderer.end();
            game.batch.begin();

            textY -= 18;
        }

        // Divider before tile section
        textY -= 5;
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(UIStyle.DIVIDER);
        UIStyle.drawRoundedRect(game.shapeRenderer, panelX + padding, textY, panelWidth - padding * 2, 2, 1);
        game.shapeRenderer.end();
        game.batch.begin();
        textY -= 18;

        // Current Tile section
        game.font.setColor(UIStyle.PRIMARY_TEXT);
        game.font.getData().setScale(0.85f);
        game.font.draw(game.batch, "Current Tile", textX, textY);
        textY -= 20;

        // Piece color info
        if (currentCard != null && currentCard.getTile() != null) {
            Color pieceColor = getColorForBuildingColor(currentCard.getTile().getColor());
            String colorName = currentCard.getTile().getColor().name();

            game.font.setColor(UIStyle.SECONDARY_TEXT);
            game.font.getData().setScale(0.75f);
            game.font.draw(game.batch, "Color: " + colorName, textX, textY);

            // Color swatch
            game.batch.end();
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(pieceColor);
            UIStyle.drawRoundedRect(game.shapeRenderer, textX + 90, textY - 12, 40, 14, 4);
            game.shapeRenderer.end();
            game.batch.begin();
        }

        // Tile Preview label (above the preview box)
        game.font.setColor(UIStyle.SECONDARY_TEXT);
        game.font.getData().setScale(0.7f);
        game.font.draw(game.batch, "Drag tile to place", preview.x, preview.y + preview.height + 14);

        // Button labels
        game.font.setColor(UIStyle.PRIMARY_TEXT);
        game.font.getData().setScale(0.85f);
        glyphLayout.setText(game.font, "Rotate [Q]");
        game.font.draw(game.batch, "Rotate [Q]",
            rotateButton.x + (rotateButton.width - glyphLayout.width) / 2f,
            rotateButton.y + (rotateButton.height + glyphLayout.height) / 2f);
        glyphLayout.setText(game.font, "Skip [P]");
        game.font.draw(game.batch, "Skip [P]",
            skipButton.x + (skipButton.width - glyphLayout.width) / 2f,
            skipButton.y + (skipButton.height + glyphLayout.height) / 2f);
    }

    private void drawChapterEndDialog() {
        game.font.setColor(Color.YELLOW);
        game.font.getData().setScale(1.5f);
        
        String title = "Chapter " + gameController.getCurrentEpisodeNumber() + " Complete!";
        glyphLayout.setText(game.font, title);
        float titleX = (viewport.getWorldWidth() - glyphLayout.width) / 2f;
        game.font.draw(game.batch, glyphLayout, titleX, viewport.getWorldHeight() / 2f + 150);
        
        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.WHITE);
        
        // Show both players' scores
        float y = viewport.getWorldHeight() / 2f + 80;
        for (int i = 0; i < 2; i++) {
            Player player = gameController.getPlayers()[i];
            String scoreText = player.getName() + ": " + player.getScore() + " points";
            glyphLayout.setText(game.font, scoreText);
            float x = (viewport.getWorldWidth() - glyphLayout.width) / 2f;
            game.font.draw(game.batch, glyphLayout, x, y);
            y -= 40;
        }
        
        String question = "Save game progress?";
        glyphLayout.setText(game.font, question);
        float qX = (viewport.getWorldWidth() - glyphLayout.width) / 2f;
        game.font.draw(game.batch, glyphLayout, qX, viewport.getWorldHeight() / 2f - 40);
        
        drawButtonLabel("Save & Continue", saveButton);
        drawButtonLabel("Continue", continueButton);
    }

    private void drawButtonLabel(String text, Rectangle button) {
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.0f);
        glyphLayout.setText(game.font, text);
        float textX = button.x + (button.width - glyphLayout.width) / 2f;
        float textY = button.y + (button.height + glyphLayout.height) / 2f;
        game.font.draw(game.batch, glyphLayout, textX, textY);
    }

    private void drawGameButtons() {
        // Buttons are now drawn inside drawGameUI() method
    }

    private void drawChapterEndButtons() {
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(UIStyle.POSITIVE);
        UIStyle.drawRoundedRect(game.shapeRenderer, saveButton.x, saveButton.y, saveButton.width, saveButton.height, UIStyle.BUTTON_CORNER_RADIUS);
        game.shapeRenderer.setColor(UIStyle.BUTTON);
        UIStyle.drawRoundedRect(game.shapeRenderer, continueButton.x, continueButton.y, continueButton.width, continueButton.height, UIStyle.BUTTON_CORNER_RADIUS);
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(UIStyle.PRIMARY_TEXT);
        UIStyle.drawRoundedRectBorder(game.shapeRenderer, saveButton.x, saveButton.y, saveButton.width, saveButton.height, UIStyle.BUTTON_CORNER_RADIUS);
        UIStyle.drawRoundedRectBorder(game.shapeRenderer, continueButton.x, continueButton.y, continueButton.width, continueButton.height, UIStyle.BUTTON_CORNER_RADIUS);
        game.shapeRenderer.end();
    }

    private Rectangle getPreviewBox() {
        float gridEndX = gridStartX + GRID_WORLD_SIZE;
        float panelX = gridEndX + 25;
        float panelWidth = viewport.getWorldWidth() - panelX - 8;
        float padding = 18;

        // Preview box positioned above buttons with proper spacing
        float buttonTopY = rotateButton.y + rotateButton.height;
        float previewHeight = 125;
        float previewY = buttonTopY + 15;

        previewBox.set(panelX + padding, previewY, panelWidth - (padding * 2), previewHeight);
        return previewBox;
    }

    private void drawCurrentTile() {
        if (currentCard == null || currentCard.getTile() == null) return;

        Tile tile = currentCard.getTile();
        boolean[][] form = tile.getShape();

        // Use full size when dragging, scaled size when in preview box
        float drawTileSize = isDragging ? TILE_SIZE : TILE_SIZE * previewTileScale;

        // Check if tile placement would be valid at current position
        boolean wouldBeValid = false;
        if (isDragging) {
            int gridX = (int)((tilePosition.x - gridStartX) / TILE_SIZE);
            int gridY = (int)((tilePosition.y - gridStartY) / TILE_SIZE);

            if (gridX >= 0 && gridX < GRID_SIZE && gridY >= 0 && gridY < GRID_SIZE) {
                Position pos = new Position(gridX, gridY);
                wouldBeValid = gameController.getCurrentEpisode().isPlacementValid(
                    tile,
                    gameController.getCurrentPlayer().getCityGrid(),
                    pos,
                    isFirstTileForPlayer
                );
            }
        }

        // Draw tile with color indicating validity when dragging, or actual tile color when not
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Color tileColor;
        if (isDragging) {
            tileColor = wouldBeValid ? new Color(0.3f, 0.8f, 0.3f, 0.8f) : new Color(0.8f, 0.3f, 0.3f, 0.8f);
        } else {
            // Show actual tile color
            tileColor = getColorForBuildingColor(tile.getColor());
        }

        game.shapeRenderer.setColor(tileColor);
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    // Draw from bottom to top (i=0 is bottom row)
                    game.shapeRenderer.rect(
                        tilePosition.x + j * drawTileSize,
                        tilePosition.y + i * drawTileSize,
                        drawTileSize,
                        drawTileSize
                    );
                }
            }
        }
        game.shapeRenderer.end();

        // Draw borders
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(Color.BLACK);
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    game.shapeRenderer.rect(
                        tilePosition.x + j * drawTileSize,
                        tilePosition.y + i * drawTileSize,
                        drawTileSize,
                        drawTileSize
                    );
                }
            }
        }
        game.shapeRenderer.end();
    }

    private void drawGrid(CityGrid cityGrid) {
        gridStartY = (viewport.getWorldHeight() - GRID_WORLD_SIZE) / 2f;

        // Draw board frame background (outer border) with warm color
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(UIStyle.PANEL);
        float frameThickness = 15f;
        game.shapeRenderer.rect(gridStartX - frameThickness, gridStartY - frameThickness,
                               GRID_WORLD_SIZE + frameThickness * 2, GRID_WORLD_SIZE + frameThickness * 2);
        game.shapeRenderer.end();
        
        // Draw base terrain and placed tiles
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell cell = cityGrid.getCell(i, j);
                if (cell != null) {
                    // If the field is occupied (a tile was placed here), draw it differently
                    if (cell.isOccupied()) {
                        // Draw placed building tile
                        game.shapeRenderer.setColor(getColorForBuildingColor(cell.getColor()));
                    } else {
                        // Draw base terrain
                        game.shapeRenderer.setColor(getColorForTerrainType(cell.getTerrain()));
                    }
                    game.shapeRenderer.rect(gridStartX + i * TILE_SIZE, gridStartY + j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        game.shapeRenderer.end();

        // Overlay round markers for trees and rocks on unoccupied terrain to match sticker visuals
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float markerRadius = TILE_SIZE * 0.35f;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell cell = cityGrid.getCell(i, j);
                if (cell == null || cell.isOccupied()) continue;

                float centerX = gridStartX + i * TILE_SIZE + TILE_SIZE / 2f;
                float centerY = gridStartY + j * TILE_SIZE + TILE_SIZE / 2f;

                if (cell.getTerrain() == TerrainType.TREE) {
                    game.shapeRenderer.setColor(new Color(0.15f, 0.55f, 0.25f, 1f));
                    game.shapeRenderer.circle(centerX, centerY, markerRadius);
                } else if (cell.getTerrain() == TerrainType.ROCK) {
                    game.shapeRenderer.setColor(new Color(0.6f, 0.6f, 0.65f, 1f));
                    game.shapeRenderer.circle(centerX, centerY, markerRadius);
                }
            }
        }
        game.shapeRenderer.end();

        // Draw grid borders and river edges
        float LINE_THICKNESS = 2.0f;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell cell = cityGrid.getCell(i, j);
                if (cell == null) continue;

                float x = gridStartX + i * TILE_SIZE;
                float y = gridStartY + j * TILE_SIZE;

                game.shapeRenderer.setColor(cell.riverBottom ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x, y, TILE_SIZE, LINE_THICKNESS);

                game.shapeRenderer.setColor(cell.riverLeft ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x, y, LINE_THICKNESS, TILE_SIZE);

                game.shapeRenderer.setColor(cell.riverTop ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x, y + TILE_SIZE - LINE_THICKNESS, TILE_SIZE, LINE_THICKNESS);

                game.shapeRenderer.setColor(cell.riverRight ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x + TILE_SIZE - LINE_THICKNESS, y, LINE_THICKNESS, TILE_SIZE);
            }
        }
        game.shapeRenderer.end();

        // Draw stickers (Brunnen, trees, rocks added by sticker placement)
        drawStickers(cityGrid);
    }

    private void drawStickers(CityGrid cityGrid) {
        for (io.github.myCity.model.Sticker sticker : cityGrid.getStickers()) {
            io.github.myCity.model.Position pos = sticker.getPosition();
            float x = gridStartX + pos.getX() * TILE_SIZE;
            float y = gridStartY + pos.getY() * TILE_SIZE;

            float radius = TILE_SIZE / 3;
            switch (sticker.getType()) {
                case WELL:
                    // Cyan/blue well
                    game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    game.shapeRenderer.setColor(new Color(0.2f, 0.7f, 0.9f, 1f));
                    game.shapeRenderer.circle(x + TILE_SIZE / 2, y + TILE_SIZE / 2, radius);
                    game.shapeRenderer.end();

                    game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                    game.shapeRenderer.setColor(Color.WHITE);
                    game.shapeRenderer.circle(x + TILE_SIZE / 2, y + TILE_SIZE / 2, radius);
                    game.shapeRenderer.end();
                    break;
                case ADD_TREE:
                    // Deep green tree marker
                    game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    game.shapeRenderer.setColor(new Color(0.15f, 0.55f, 0.25f, 1f));
                    game.shapeRenderer.circle(x + TILE_SIZE / 2, y + TILE_SIZE / 2, radius);
                    game.shapeRenderer.end();

                    game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                    game.shapeRenderer.setColor(new Color(0.05f, 0.25f, 0.1f, 1f));
                    game.shapeRenderer.circle(x + TILE_SIZE / 2, y + TILE_SIZE / 2, radius);
                    game.shapeRenderer.end();
                    break;
                case ADD_ROCK:
                    // Rock marker in gray/stone
                    game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    game.shapeRenderer.setColor(new Color(0.45f, 0.45f, 0.5f, 1f));
                    game.shapeRenderer.circle(x + TILE_SIZE / 2, y + TILE_SIZE / 2, radius);
                    game.shapeRenderer.end();

                    game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                    game.shapeRenderer.setColor(new Color(0.75f, 0.75f, 0.8f, 1f));
                    game.shapeRenderer.circle(x + TILE_SIZE / 2, y + TILE_SIZE / 2, radius);
                    game.shapeRenderer.end();
                    break;
                default:
                    break;
            }
        }
    }

    private Color getColorForBuildingColor(BuildingColor buildingColor) {
        switch (buildingColor) {
            case RED:
                return new Color(0.8f, 0.2f, 0.2f, 1f);
            case BLUE:
                return new Color(0.2f, 0.4f, 0.8f, 1f);
            case YELLOW:
                return new Color(0.9f, 0.9f, 0.2f, 1f);
            case GREEN:
                return new Color(0.3f, 0.7f, 0.3f, 1f);
            case PURPLE:
                return new Color(0.6f, 0.2f, 0.8f, 1f);
            case ORANGE:
                return new Color(0.9f, 0.5f, 0.1f, 1f);
            case NONE:
            default:
                return new Color(0.7f, 0.7f, 0.7f, 1f); // Gray for buildings without color
        }
    }

    private Color getColorForTerrainType(TerrainType terrainType) {
        switch (terrainType) {
            case MEADOW:
                return new Color(43/255f, 84/255f, 56/255f, 1f);
            case TREE:
                return new Color(25/255f, 102/255f, 38/255f, 1f);
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

        // Calculate gridStartY here to ensure it's set before positioning UI elements
        gridStartY = (viewport.getWorldHeight() - GRID_WORLD_SIZE) / 2f;

        float gridEndX = gridStartX + GRID_WORLD_SIZE;
        float panelX = gridEndX + 25;
        float panelWidth = viewport.getWorldWidth() - panelX - 8;
        float padding = 18;
        float buttonHeight = 38;
        float buttonSpacing = 10;

        // Full-width buttons stacked vertically
        float buttonWidth = panelWidth - (padding * 2);

        // Position buttons at bottom of panel, stacked vertically
        float panelBottomPadding = 15;
        float buttonY = gridStartY - panelBottomPadding + padding;
        float buttonStartX = panelX + padding;

        // Skip button at very bottom
        skipButton.set(buttonStartX, buttonY, buttonWidth, buttonHeight);
        // Rotate button above skip button
        rotateButton.set(buttonStartX, buttonY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight);

        // Position chapter end dialog buttons
        float centerX = viewport.getWorldWidth() / 2f;
        float dialogButtonWidth = 180;
        float dialogButtonHeight = 50;

        saveButton.set(centerX - dialogButtonWidth - 20, viewport.getWorldHeight() / 2f - 120,
                      dialogButtonWidth, dialogButtonHeight);
        continueButton.set(centerX + 20, viewport.getWorldHeight() / 2f - 120,
                         dialogButtonWidth, dialogButtonHeight);

        resetTilePosition();
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