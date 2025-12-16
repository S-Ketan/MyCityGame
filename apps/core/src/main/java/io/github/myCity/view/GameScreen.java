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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.LEFT) return false;
                
                Vector2 worldPos = screenToWorld(screenX, screenY);
                
                Gdx.app.log("Input", "Touch down at screen (" + screenX + ", " + screenY + ") = world (" + worldPos.x + ", " + worldPos.y + ")");
                
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
                    Gdx.app.log("Input", "Started dragging tile. Offset: (" + dragOffset.x + ", " + dragOffset.y + ")");
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
                    Gdx.app.log("Input", "Touch up at world (" + worldPos.x + ", " + worldPos.y + ")");
                    Gdx.app.log("Input", "Tile position: (" + tilePosition.x + ", " + tilePosition.y + ")");
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
                        Gdx.app.log("Game", "Tile clicked at cell (" + j + ", " + i + ")");
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
        
        Gdx.app.log("Game", "Tile position in world: (" + tilePosition.x + ", " + tilePosition.y + ")");
        Gdx.app.log("Game", "Grid start: (" + gridStartX + ", " + gridStartY + ")");
        Gdx.app.log("Game", "Trying to place tile at grid position: (" + gridX + ", " + gridY + ")");
        
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
            Gdx.app.log("Game", "Tile mostly out of bounds (" + validCells + "/" + totalCells + " cells valid)");
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
            Gdx.app.log("Game", "✓ Tile placed successfully at (" + gridX + ", " + gridY + ")");
            
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
            Gdx.app.log("Game", "✗ Invalid tile placement");
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
            
            // Switch to player 2 BEFORE loading the deck
            spiel.wechselSpieler();
            
            // Now reload the SAME chapter for player 2
            int currentChapter = spiel.getAktuellesKapitelNummer();
            spiel.kapitelLaden(currentChapter);
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
        Gdx.app.log("Game", "Chapter " + spiel.getAktuellesKapitelNummer() + " completed!");
        
        // Calculate bonuses for both players
        for (int i = 0; i < 2; i++) {
            Spieler player = spiel.getSpieler()[i];
            int bonus = spiel.getAktuellesKapitel().berechneBonus(player.getStadtplan());
            player.setPunktzahl(player.getPunktzahl() + bonus);
            
            Gdx.app.log("Game", player.getName() + " bonus: " + bonus + 
                       ", Total score: " + player.getPunktzahl());
        }
        
        // Show chapter end dialog
        showChapterEndDialog = true;
    }

    private void skipTile() {
        if (currentCard == null) return;
        
        Gdx.app.log("Game", spiel.getCurrentPlayer().getName() + " skipped tile #" + (currentPlayerTileCount + 1));
        
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
                Gdx.app.log("Game", "Drew next tile for " + spiel.getCurrentPlayer().getName());
            } else {
                Gdx.app.log("Game", "ERROR: No more cards in deck!");
            }
        }
    }

    private void rotateTile() {
        if (currentCard != null && currentCard.getPlättchen() != null) {
            currentCard.getPlättchen().drehen();
            Gdx.app.log("Game", "Tile rotated");
        }
    }

    private void resetTilePosition() {
        float gridEndX = gridStartX + GRID_WORLD_SIZE;
        float rightAreaWidth = viewport.getWorldWidth() - gridEndX;
        
        if (currentCard != null && currentCard.getPlättchen() != null) {
            Plättchen plättchen = currentCard.getPlättchen();
            float plättchenWidth = plättchen.getBreite() * TILE_SIZE;
            tilePosition.x = gridEndX + (rightAreaWidth - plättchenWidth) * 0.75f;
            tilePosition.y = (viewport.getWorldHeight() - (plättchen.getHöhe() * TILE_SIZE)) / 2f;
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
        spiel.kapitelLaden(nextChapter);
        currentCard = spiel.getAktuellesKapitel().zieheKarte();
        resetTilePosition();
        
        Gdx.app.log("Game", "Starting Chapter " + nextChapter);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw grid
        drawGrid(spiel.getCurrentPlayer().getStadtplan());
        
        // Draw current tile (either at original position or being dragged)
        drawCurrentTile();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        
        if (showChapterEndDialog) {
            drawChapterEndDialog();
        } else {
            drawGameUI();
        }
        
        game.batch.end();

        // Draw buttons
        if (showChapterEndDialog) {
            drawChapterEndButtons();
        } else {
            drawGameButtons();
        }
    }

    private void drawGameUI() {
        Spieler currentPlayer = spiel.getCurrentPlayer();
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.0f);

        float rightX = viewport.getWorldWidth() - 20;
        float currentY = viewport.getWorldHeight() - 20;

        // Player info
        String playerText = "Player: " + currentPlayer.getName();
        glyphLayout.setText(game.font, playerText);
        game.font.draw(game.batch, glyphLayout, rightX - glyphLayout.width, currentY);
        currentY -= 30;

        // Score
        String scoreText = "Score: " + currentPlayer.getPunktzahl();
        glyphLayout.setText(game.font, scoreText);
        game.font.draw(game.batch, glyphLayout, rightX - glyphLayout.width, currentY);
        currentY -= 30;

        // Chapter
        String chapterText = "Chapter: " + spiel.getAktuellesKapitelNummer();
        glyphLayout.setText(game.font, chapterText);
        game.font.draw(game.batch, glyphLayout, rightX - glyphLayout.width, currentY);
        currentY -= 30;

        // Tile count
        String tileText = "Tiles: " + currentPlayerTileCount + "/24";
        glyphLayout.setText(game.font, tileText);
        game.font.draw(game.batch, glyphLayout, rightX - glyphLayout.width, currentY);

        // Button labels
        drawButtonLabel("Rotate", rotateButton);
        drawButtonLabel("Skip", skipButton);
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
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(Color.WHITE);
        game.shapeRenderer.rect(rotateButton.x, rotateButton.y, rotateButton.width, rotateButton.height);
        game.shapeRenderer.rect(skipButton.x, skipButton.y, skipButton.width, skipButton.height);
        game.shapeRenderer.end();
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
        
        // Draw tile with color indicating validity
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        Color tileColor = isDragging ? 
            (wouldBeValid ? new Color(0.3f, 0.8f, 0.3f, 0.8f) : new Color(0.8f, 0.3f, 0.3f, 0.8f)) : 
            Color.ORANGE;
        
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
        
        // Debug coordinates removed for normal play; re-enable if needed
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

        float gridEndX = gridStartX + GRID_WORLD_SIZE;
        float rightAreaWidth = viewport.getWorldWidth() - gridEndX;
        float buttonWidth = 140;
        float buttonHeight = 40;
        
        // Position rotate button
        float rotateX = gridEndX + (rightAreaWidth - buttonWidth) * 0.5f;
        float rotateY = 90;
        rotateButton.set(rotateX, rotateY, buttonWidth, buttonHeight);
        
        // Position skip button
        float skipX = gridEndX + (rightAreaWidth - buttonWidth) * 0.5f;
        float skipY = 30;
        skipButton.set(skipX, skipY, buttonWidth, buttonHeight);
        
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