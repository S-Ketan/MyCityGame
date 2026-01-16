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
import io.github.myCity.model.BuildingColor;
import io.github.myCity.model.Feld;
import io.github.myCity.model.Gebäudekarte;
import io.github.myCity.model.Geländetyp;
import io.github.myCity.model.Plättchen;
import io.github.myCity.model.Position;
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

    public GameScreen(Main game, String player1Name, String player2Name) {
        this.game = game;
        this.spiel = new Spiel();
        this.spiel.neuesSpielStarten(player1Name, player2Name);
        this.currentCard = spiel.getAktuellesKapitel().zieheKarte();

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
    public GameScreen(Main game, Spiel existingSpiel) {
        this.game = game;
        this.spiel = existingSpiel;
        this.currentCard = spiel.getAktuellesKapitel().zieheKarte();
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
                    dragOffset.set(worldPos.x - tilePosition.x, worldPos.y - tilePosition.y);
                    //Gdx.app.log("Input", "Started dragging tile. Offset: (" + dragOffset.x + ", " + dragOffset.y + ")");
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
        
        Plättchen plättchen = currentCard.getPlättchen();
        if (plättchen == null) return false;
        
        boolean[][] form = plättchen.getForm();
        
        // Check if any cell of the tile is clicked
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    float cellX = tilePosition.x + j * TILE_SIZE;
                    float cellY = tilePosition.y + i * TILE_SIZE;
                    
                    if (worldPos.x >= cellX && worldPos.x <= cellX + TILE_SIZE &&
                        worldPos.y >= cellY && worldPos.y <= cellY + TILE_SIZE) {
                        //Gdx.app.log("Game", "Tile clicked at cell (" + j + ", " + i + ")");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void tryPlaceTile(Vector2 worldPos) {
        if (currentCard == null) return;
        
        Plättchen plättchen = currentCard.getPlättchen();
        if (plättchen == null) return;
        
        // Convert tile position (top-left corner) to grid coordinates
        int gridX = Math.round((tilePosition.x - gridStartX) / TILE_SIZE);
        int gridY = Math.round((tilePosition.y - gridStartY) / TILE_SIZE);
        
        //Gdx.app.log("Game", "Tile position in world: (" + tilePosition.x + ", " + tilePosition.y + ")");
        //Gdx.app.log("Game", "Grid start: (" + gridStartX + ", " + gridStartY + ")");
        //Gdx.app.log("Game", "Trying to place tile at grid position: (" + gridX + ", " + gridY + ")");
        
        // Check if tile would be mostly outside the grid - if so, return to original position
        boolean[][] form = plättchen.getForm();
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
        boolean isValid = spiel.getAktuellesKapitel().istPlatzierungGültig(
            plättchen,
            spiel.getCurrentPlayer().getStadtplan(),
            pos,
            isFirstTileForPlayer
        );
        
        if (isValid) {
            // Place the tile
            spiel.getCurrentPlayer().plaettchenLegen(plättchen, pos);
            //Gdx.app.log("Game", "✓ Tile placed successfully at (" + gridX + ", " + gridY + ")");

            // Update score to reflect placement
            spiel.refreshScore(spiel.getAktuellerSpielerIndex());
            
            isFirstTileForPlayer = false;
            currentPlayerTileCount++;
            
            // Check if current player finished all 24 tiles
            if (currentPlayerTileCount >= 24) {
                handlePlayerFinished();
            } else {
                // Draw next tile for same player
                currentCard = spiel.getAktuellesKapitel().zieheKarte();
                resetTilePosition();
            }
        } else {
            // Invalid placement - return tile to original position
            resetTilePosition();
            //Gdx.app.log("Game", "✗ Invalid tile placement");
        }
    }



    private void handlePlayerFinished() {
        Spieler currentPlayer = spiel.getCurrentPlayer();
        int currentPlayerIndex = spiel.getAktuellerSpielerIndex();
        
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
            spiel.wechselSpieler();

            // Reset the deck to the original order for player 2 (same tiles as player 1)
            spiel.resetDeckForNextPlayer();
            currentCard = spiel.getAktuellesKapitel().zieheKarte();
            resetTilePosition();

            Gdx.app.log("Game", "Now it's " + spiel.getCurrentPlayer().getName() + "'s turn with the same deck!");
        } else {
            // Player 2 finished, chapter is complete
            Gdx.app.log("Game", "Both players finished! Chapter complete!");
            endChapter();
        }
    }

    private void endChapter() {
        int chapterNumber = spiel.getAktuellesKapitelNummer();
        Gdx.app.log("Game", "Chapter " + chapterNumber + " completed!");

        // Transition to Chapter End Screen
        game.setScreen(new ChapterEndScreen(game, spiel, chapterNumber));
    }

    private void skipTile() {
        if (currentCard == null) return;
        
        //Gdx.app.log("Game", spiel.getCurrentPlayer().getName() + " skipped tile #" + (currentPlayerTileCount + 1));
        
        // Apply skip penalty if needed (handled by game logic)
        spiel.getCurrentPlayer().plaettchenUeberspringen();
        
        currentPlayerTileCount++;
        
        if (currentPlayerTileCount >= 24) {
            handlePlayerFinished();
        } else {
            // Draw next card for the SAME player
            currentCard = spiel.getAktuellesKapitel().zieheKarte();
            
            if (currentCard != null) {
                resetTilePosition();
                //Gdx.app.log("Game", "Drew next tile for " + spiel.getCurrentPlayer().getName());
            } else {
                //Gdx.app.log("Game", "ERROR: No more cards in deck!");
            }
        }
    }

    private void rotateTile() {
        if (currentCard != null && currentCard.getPlättchen() != null) {
            currentCard.getPlättchen().drehen();
            //Gdx.app.log("Game", "Tile rotated");
        }
    }

    private void resetTilePosition() {
        float gridEndX = gridStartX + GRID_WORLD_SIZE;
        float panelX = gridEndX + 15;
        float panelWidth = viewport.getWorldWidth() - panelX - 10;
        
        // Position tile in the preview box
        float previewBoxX = panelX + 10;
        float previewBoxY = gridStartY + 140;
        float previewBoxWidth = panelWidth - 20;
        float previewBoxHeight = 200;
        
        if (currentCard != null && currentCard.getPlättchen() != null) {
            Plättchen plättchen = currentCard.getPlättchen();
            float plättchenWidth = plättchen.getBreite() * TILE_SIZE;
            float plättchenHeight = plättchen.getHöhe() * TILE_SIZE;
            
            // Center tile in preview box
            tilePosition.x = previewBoxX + (previewBoxWidth - plättchenWidth) / 2f;
            tilePosition.y = previewBoxY + (previewBoxHeight - plättchenHeight) / 2f;
        }
    }

    private void handleChapterEndDialogInput(Vector2 worldPos) {
        if (saveButton.contains(worldPos.x, worldPos.y)) {
            spiel.spielstandSpeichern();
            Gdx.app.log("Game", "Game saved!");
            startNextChapter();
        } else if (continueButton.contains(worldPos.x, worldPos.y)) {
            Gdx.app.log("Game", "Continuing without saving");
            startNextChapter();
        }
    }

    private void startNextChapter() {
        showChapterEndDialog = false;
        
        int nextChapter = spiel.getAktuellesKapitelNummer() + 1;
        if (nextChapter > 24) {
            // Game completed!
            spiel.endpunkteBerechnen();
            Gdx.app.log("Game", "All chapters completed! Returning to menu...");
            // TODO: Return to main menu
            return;
        }
        
        // Reset for next chapter
        currentPlayerTileCount = 0;
        isFirstTileForPlayer = true;
        spiel.resetEpisodeTracking();
        
        // Load next chapter
        spiel.kapitelLaden(nextChapter, true);
        currentCard = spiel.getAktuellesKapitel().zieheKarte();
        resetTilePosition();
        
        Gdx.app.log("Game", "Starting Chapter " + nextChapter);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw grid
        drawGrid(spiel.getCurrentPlayer().getStadtplan());

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
        Spieler currentPlayer = spiel.getCurrentPlayer();
        Spieler[] players = spiel.getSpieler();
        Spieler otherPlayer = players[0].equals(currentPlayer) ? players[1] : players[0];
        
        // Panel dimensions
        float panelX = gridStartX + GRID_WORLD_SIZE + 15;
        float panelY = gridStartY;
        float panelWidth = viewport.getWorldWidth() - panelX - 10;
        float panelHeight = GRID_WORLD_SIZE;
        
        // Draw main info panel background
        game.batch.end();
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(new Color(0.18f, 0.24f, 0.31f, 1f)); // Dark blue-gray background
        game.shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        game.shapeRenderer.end();
        
        // Draw panel border and accent line
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(new Color(0.3f, 0.5f, 0.7f, 1f)); // Blue accent line
        game.shapeRenderer.rect(panelX, panelY + panelHeight - 3, panelWidth, 3);
        game.shapeRenderer.end();
        
        game.batch.begin();
        
        float textX = panelX + 15;
        float textY = panelY + panelHeight - 20;
        
        // Title section
        game.font.setColor(new Color(0.9f, 0.9f, 0.9f, 1f));
        game.font.getData().setScale(1.1f);
        game.font.draw(game.batch, "My City - Multiplayer", textX, textY);
        textY -= 30;
        
        // Divider line
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(new Color(0.3f, 0.4f, 0.5f, 0.5f));
        game.shapeRenderer.rect(panelX + 10, textY - 5, panelWidth - 20, 1);
        game.shapeRenderer.end();
        game.batch.begin();
        textY -= 15;
        
        // Current Player section
        game.font.setColor(new Color(1f, 0.7f, 0.3f, 1f)); // Orange for player name
        game.font.getData().setScale(0.85f);
        game.font.draw(game.batch, "Current Player: " + currentPlayer.getName(), textX, textY);
        textY -= 28;
        
        // Episode and Deck info on same line
        game.font.setColor(new Color(0.85f, 0.85f, 0.85f, 1f));
        game.font.getData().setScale(0.8f);
        game.font.draw(game.batch, "Episode: " + spiel.getAktuellesKapitelNummer(), textX, textY);
        
        String deckText = "Deck: " + currentPlayerTileCount + "/24";
        glyphLayout.setText(game.font, deckText);
        game.font.draw(game.batch, deckText, panelX + panelWidth - 15 - glyphLayout.width, textY);
        textY -= 25;
        
        // Another divider
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(new Color(0.3f, 0.4f, 0.5f, 0.5f));
        game.shapeRenderer.rect(panelX + 10, textY - 5, panelWidth - 20, 1);
        game.shapeRenderer.end();
        game.batch.begin();
        textY -= 15;
        
        // Scores section
        game.font.setColor(new Color(0.9f, 0.9f, 0.9f, 1f));
        game.font.getData().setScale(0.9f);
        game.font.draw(game.batch, "Scores:", textX, textY);
        textY -= 22;
        
        // Player scores with progress bars
        for (int i = 0; i < 2; i++) {
            Spieler player = players[i];
            boolean isCurrent = player.equals(currentPlayer);
            
            // Player name and score
            game.font.setColor(isCurrent ? new Color(1f, 0.7f, 0.3f, 1f) : new Color(0.4f, 0.7f, 1f, 1f));
            game.font.getData().setScale(0.75f);
            String playerText = player.getName() + ": " + player.getPunktzahl() + " pts";
            game.font.draw(game.batch, playerText, textX, textY);
            textY -= 18;
            
            // Progress bar
            game.batch.end();
            float barWidth = panelWidth - 30;
            float barHeight = 8;
            float barX = panelX + 15;
            float barY = textY - 10;
            
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            // Background (slightly lighter so empty bars remain visible)
            game.shapeRenderer.setColor(new Color(0.15f, 0.2f, 0.26f, 1f));
            game.shapeRenderer.rect(barX, barY, barWidth, barHeight);

            // Fill (show a 2px sliver when non-zero to make it visible)
            float fillWidth = (player.getProgressPoints() / 100f) * barWidth;
            game.shapeRenderer.setColor(isCurrent ? new Color(1f, 0.7f, 0.3f, 1f) : new Color(0.4f, 0.7f, 1f, 1f));
            if (fillWidth > 0) {
                game.shapeRenderer.rect(barX, barY, Math.max(2f, fillWidth), barHeight);
            }
            game.shapeRenderer.end();

            // Border
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(new Color(0.55f, 0.65f, 0.75f, 1f));
            game.shapeRenderer.rect(barX, barY, barWidth, barHeight);
            game.shapeRenderer.end();
            
            // Progress text
            game.batch.begin();
            game.font.setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
            game.font.getData().setScale(0.65f);
            String progressText = player.getProgressPoints() + "/100";
            glyphLayout.setText(game.font, progressText);
            game.font.draw(game.batch, progressText, barX + barWidth - glyphLayout.width, barY + barHeight + 10);
            
            textY -= 20;
            game.batch.end();
            game.batch.begin();
        }
        
        textY -= 5;
        
        // Divider
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(new Color(0.3f, 0.4f, 0.5f, 0.5f));
        game.shapeRenderer.rect(panelX + 10, textY - 5, panelWidth - 20, 1);
        game.shapeRenderer.end();
        game.batch.begin();
        textY -= 15;
        
        // Piece Color section
        game.font.setColor(new Color(0.9f, 0.9f, 0.9f, 1f));
        game.font.getData().setScale(0.85f);
        game.font.draw(game.batch, "Piece Color:", textX, textY);
        
        if (currentCard != null && currentCard.getPlättchen() != null) {
            String colorName = currentCard.getPlättchen().getColor().name();
            Color pieceColor = getColorForBuildingColor(currentCard.getPlättchen().getColor());
            
            game.font.setColor(new Color(0.85f, 0.85f, 0.85f, 1f));
            game.font.getData().setScale(0.8f);
            game.font.draw(game.batch, colorName, textX, textY - 18);
            
            // Draw color swatch
            game.batch.end();
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(pieceColor);
            game.shapeRenderer.rect(textX + 75, textY - 28, 50, 20);
            game.shapeRenderer.end();
            
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
            game.shapeRenderer.rect(textX + 75, textY - 28, 50, 20);
            game.shapeRenderer.end();
            game.batch.begin();
        }
        textY -= 45;
        
        // Tile Preview label
        game.font.setColor(new Color(0.85f, 0.85f, 0.85f, 1f));
        game.font.getData().setScale(0.85f);
        game.font.draw(game.batch, "Tile Preview:", textX, textY);
        textY -= 10;
        
        // Draw tile preview box background
        game.batch.end();
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(new Color(0.12f, 0.16f, 0.21f, 1f));
        float previewBoxX = panelX + 10;
        float previewBoxY = panelY + 120;
        float previewBoxWidth = panelWidth - 20;
        float previewBoxHeight = 180;
        game.shapeRenderer.rect(previewBoxX, previewBoxY, previewBoxWidth, previewBoxHeight);
        
        // Preview box border
        game.shapeRenderer.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(new Color(0.3f, 0.5f, 0.7f, 1f));
        game.shapeRenderer.rect(previewBoxX, previewBoxY, previewBoxWidth, previewBoxHeight);
        game.shapeRenderer.end();
        
        // Draw help text in preview box if no card
        if (currentCard == null || currentCard.getPlättchen() == null) {
            game.batch.begin();
            game.font.setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
            game.font.getData().setScale(0.75f);
            String helpText = "Drag tile to board";
            glyphLayout.setText(game.font, helpText);
            game.font.draw(game.batch, helpText, 
                previewBoxX + (previewBoxWidth - glyphLayout.width) / 2f,
                previewBoxY + previewBoxHeight / 2f);
            game.batch.end();
        }
        
        // Draw buttons inside panel (below preview box)
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(new Color(0.3f, 0.5f, 0.7f, 1f));
        game.shapeRenderer.rect(rotateButton.x, rotateButton.y, rotateButton.width, rotateButton.height);
        game.shapeRenderer.rect(skipButton.x, skipButton.y, skipButton.width, skipButton.height);
        game.shapeRenderer.end();
        
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(new Color(0.5f, 0.7f, 0.9f, 1f));
        game.shapeRenderer.rect(rotateButton.x, rotateButton.y, rotateButton.width, rotateButton.height);
        game.shapeRenderer.rect(skipButton.x, skipButton.y, skipButton.width, skipButton.height);
        game.shapeRenderer.end();
        
        game.batch.begin();
        
        // Draw button labels
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(0.95f);
        
        // Rotate button label
        glyphLayout.setText(game.font, "Rotate [Q/E]");
        float rotateTextX = rotateButton.x + (rotateButton.width - glyphLayout.width) / 2f;
        float rotateTextY = rotateButton.y + (rotateButton.height + glyphLayout.height) / 2f;
        game.font.draw(game.batch, "Rotate [Q/E]", rotateTextX, rotateTextY);
        
        // Skip button label
        glyphLayout.setText(game.font, "Skip [P]");
        float skipTextX = skipButton.x + (skipButton.width - glyphLayout.width) / 2f;
        float skipTextY = skipButton.y + (skipButton.height + glyphLayout.height) / 2f;
        game.font.draw(game.batch, "Skip [P]", skipTextX, skipTextY);
    }

    private void drawChapterEndDialog() {
        game.font.setColor(Color.YELLOW);
        game.font.getData().setScale(1.5f);
        
        String title = "Chapter " + spiel.getAktuellesKapitelNummer() + " Complete!";
        glyphLayout.setText(game.font, title);
        float titleX = (viewport.getWorldWidth() - glyphLayout.width) / 2f;
        game.font.draw(game.batch, glyphLayout, titleX, viewport.getWorldHeight() / 2f + 150);
        
        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.WHITE);
        
        // Show both players' scores
        float y = viewport.getWorldHeight() / 2f + 80;
        for (int i = 0; i < 2; i++) {
            Spieler player = spiel.getSpieler()[i];
            String scoreText = player.getName() + ": " + player.getPunktzahl() + " points";
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
        game.shapeRenderer.setColor(0.2f, 0.6f, 0.2f, 1f);
        game.shapeRenderer.rect(saveButton.x, saveButton.y, saveButton.width, saveButton.height);
        game.shapeRenderer.setColor(0.2f, 0.2f, 0.6f, 1f);
        game.shapeRenderer.rect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
        game.shapeRenderer.end();
        
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(Color.WHITE);
        game.shapeRenderer.rect(saveButton.x, saveButton.y, saveButton.width, saveButton.height);
        game.shapeRenderer.rect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
        game.shapeRenderer.end();
    }

    private void drawCurrentTile() {
        if (currentCard == null || currentCard.getPlättchen() == null) return;
        
        Plättchen plättchen = currentCard.getPlättchen();
        boolean[][] form = plättchen.getForm();
        
        // Check if tile placement would be valid at current position
        boolean wouldBeValid = false;
        if (isDragging) {
            int gridX = (int)((tilePosition.x - gridStartX) / TILE_SIZE);
            int gridY = (int)((tilePosition.y - gridStartY) / TILE_SIZE);
            
            if (gridX >= 0 && gridX < GRID_SIZE && gridY >= 0 && gridY < GRID_SIZE) {
                Position pos = new Position(gridX, gridY);
                wouldBeValid = spiel.getAktuellesKapitel().istPlatzierungGültig(
                    plättchen,
                    spiel.getCurrentPlayer().getStadtplan(),
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
            tileColor = getColorForBuildingColor(plättchen.getColor());
        }
        
        game.shapeRenderer.setColor(tileColor);
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    // Draw from bottom to top (i=0 is bottom row)
                    game.shapeRenderer.rect(
                        tilePosition.x + j * TILE_SIZE, 
                        tilePosition.y + i * TILE_SIZE, 
                        TILE_SIZE, 
                        TILE_SIZE
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
                        tilePosition.x + j * TILE_SIZE, 
                        tilePosition.y + i * TILE_SIZE, 
                        TILE_SIZE, 
                        TILE_SIZE
                    );
                }
            }
        }
        game.shapeRenderer.end();
    }

    private void drawGrid(Stadtplan stadtplan) {
        gridStartY = (viewport.getWorldHeight() - GRID_WORLD_SIZE) / 2f;

        // Draw board frame background (outer border)
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(new Color(0.25f, 0.32f, 0.4f, 1f)); // Dark blue-gray frame
        float frameThickness = 15f;
        game.shapeRenderer.rect(gridStartX - frameThickness, gridStartY - frameThickness, 
                               GRID_WORLD_SIZE + frameThickness * 2, GRID_WORLD_SIZE + frameThickness * 2);
        game.shapeRenderer.end();
        
        // Draw base terrain and placed tiles
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Feld feld = stadtplan.getFeld(i, j);
                if (feld != null) {
                    // If the field is occupied (a tile was placed here), draw it differently
                    if (feld.istBelegt()) {
                        // Draw placed building tile
                        game.shapeRenderer.setColor(getColorForBuildingColor(feld.getColor()));
                    } else {
                        // Draw base terrain
                        game.shapeRenderer.setColor(getColorForGeländetyp(feld.getGelände()));
                    }
                    game.shapeRenderer.rect(gridStartX + i * TILE_SIZE, gridStartY + j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        game.shapeRenderer.end();

        // Draw grid borders and river edges
        float LINE_THICKNESS = 2.0f;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Feld feld = stadtplan.getFeld(i, j);
                if (feld == null) continue;

                float x = gridStartX + i * TILE_SIZE;
                float y = gridStartY + j * TILE_SIZE;

                game.shapeRenderer.setColor(feld.flussUnten ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x, y, TILE_SIZE, LINE_THICKNESS);

                game.shapeRenderer.setColor(feld.flussLinks ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x, y, LINE_THICKNESS, TILE_SIZE);

                game.shapeRenderer.setColor(feld.flussOben ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x, y + TILE_SIZE - LINE_THICKNESS, TILE_SIZE, LINE_THICKNESS);

                game.shapeRenderer.setColor(feld.flussRechts ? Color.BLUE : Color.BLACK);
                game.shapeRenderer.rect(x + TILE_SIZE - LINE_THICKNESS, y, LINE_THICKNESS, TILE_SIZE);
            }
        }
        game.shapeRenderer.end();

        // Draw stickers (Brunnen, trees, rocks added by sticker placement)
        drawStickers(stadtplan);
    }

    private void drawStickers(Stadtplan stadtplan) {
        for (io.github.myCity.model.Sticker sticker : stadtplan.getSticker()) {
            io.github.myCity.model.Position pos = sticker.getPos();
            float x = gridStartX + pos.getX() * TILE_SIZE;
            float y = gridStartY + pos.getY() * TILE_SIZE;

            float radius = TILE_SIZE / 3;
            switch (sticker.getTyp()) {
                case BRUNNEN:
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
                case BAUM_HINZUFÜGEN:
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
                case FELS_HINZUFÜGEN:
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

    private Color getColorForGeländetyp(Geländetyp typ) {
        switch (typ) {
            case WIESE:
                return new Color(43/255f, 84/255f, 56/255f, 1f);
            case BAUM:
                return new Color(25/255f, 102/255f, 38/255f,1f);
                // return Color.GREEN;
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

        float gridEndX = gridStartX + GRID_WORLD_SIZE;
        float panelX = gridEndX + 15;
        float panelWidth = viewport.getWorldWidth() - panelX - 10;
        float buttonWidth = 120;
        float buttonHeight = 40;
        
        // Position buttons inside panel, below preview box
        float buttonY = gridStartY + 80;
        float buttonSpacing = 10;
        float totalButtonWidth = (buttonWidth * 2) + buttonSpacing;
        float buttonStartX = panelX + (panelWidth - totalButtonWidth) / 2f;
        
        // Position rotate button
        rotateButton.set(buttonStartX, buttonY, buttonWidth, buttonHeight);
        
        // Position skip button
        skipButton.set(buttonStartX + buttonWidth + buttonSpacing, buttonY, buttonWidth, buttonHeight);
        
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