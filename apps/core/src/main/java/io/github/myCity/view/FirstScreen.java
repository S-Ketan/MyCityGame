package io.github.myCity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import io.github.myCity.Main;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private final Main game;
    private static final int GRID_SIZE = 11;
    private static final float TILE_SIZE = 50;
    private static final float GRID_TOTAL_WIDTH = GRID_SIZE * TILE_SIZE;
    private static final float GRID_TOTAL_HEIGHT = GRID_SIZE * TILE_SIZE;

    private Rectangle newGameButton;
    private Rectangle kapitelButton;
    private Rectangle loadButton;
    private Rectangle quitButton;
    private Rectangle backButton;
    private Rectangle[] kapitelButtons;

    private enum ScreenState {
        MENU,
        KAPITEL
    }

    private ScreenState currentState = ScreenState.MENU;

    public FirstScreen(Main game) {
        this.game = game;

        float buttonWidth = 200;
        float buttonHeight = 40;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonX = (screenWidth - buttonWidth) / 2;

        newGameButton = new Rectangle(buttonX, screenHeight / 2 + 75, buttonWidth, buttonHeight);
        kapitelButton = new Rectangle(buttonX, screenHeight / 2 + 25, buttonWidth, buttonHeight);
        loadButton = new Rectangle(buttonX, screenHeight / 2 - 25, buttonWidth, buttonHeight);
        quitButton = new Rectangle(buttonX, screenHeight / 2 - 75, buttonWidth, buttonHeight);
        
        float kapitelButtonX = screenWidth / 2 - 50;
        float kapitelStartY = screenHeight / 2 + 100;
        kapitelButtons = new Rectangle[8];
        for (int i = 0; i < 8; i++) {
            kapitelButtons[i] = new Rectangle(kapitelButtonX, kapitelStartY - (i + 1) * 30 - 15, 100, 20);
        }
        backButton = new Rectangle(kapitelButtonX, kapitelStartY - 9 * 30 - 15, 100, 20);
    }

    @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawGrid();

        if (currentState == ScreenState.MENU) {
            renderMenu();
            handleMenuInput();
        } else if (currentState == ScreenState.KAPITEL) {
            renderKapitel();
            handleKapitelInput();
        }
    }

    private void drawGrid() {
        float startX = (Gdx.graphics.getWidth() - GRID_TOTAL_WIDTH) / 2;
        float startY = (Gdx.graphics.getHeight() - GRID_TOTAL_HEIGHT) / 2;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(Color.GREEN);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                game.shapeRenderer.rect(startX + i * TILE_SIZE, startY + j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        game.shapeRenderer.end();

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(Color.BLACK);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                game.shapeRenderer.rect(startX + i * TILE_SIZE, startY + j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        game.shapeRenderer.end();
    }

    private void renderMenu() {
        game.batch.begin();
        game.font.getData().setScale(1.5f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "New Game", newGameButton.x + 30, newGameButton.y + 30);
        game.font.draw(game.batch, "Kapitel", kapitelButton.x + 30, kapitelButton.y + 30);
        game.font.draw(game.batch, "Load", loadButton.x + 30, loadButton.y + 30);
        game.font.draw(game.batch, "Quit", quitButton.x + 30, quitButton.y + 30);
        game.batch.end();
    }

    private void handleMenuInput() {
        if (Gdx.input.isButtonJustPressed(0)) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (newGameButton.contains(touchX, touchY)) {
                Gdx.app.log("Menu", "New Game clicked");
                // Here you would start a new game
            } else if (kapitelButton.contains(touchX, touchY)) {
                Gdx.app.log("Menu", "Kapitel clicked");
                currentState = ScreenState.KAPITEL;
            } else if (loadButton.contains(touchX, touchY)) {
                Gdx.app.log("Menu", "Load clicked");
                // Here you would load a game
            } else if (quitButton.contains(touchX, touchY)) {
                Gdx.app.log("Menu", "Quit clicked");
                Gdx.app.exit();
            }
        }
    }

    private void renderKapitel() {
        game.batch.begin();
        game.font.getData().setScale(1.5f);
        game.font.setColor(Color.WHITE);
        
        for (int i = 0; i < 8; i++) {
            game.font.draw(game.batch, "Kapitel " + (i + 1), kapitelButtons[i].x, kapitelButtons[i].y + 15);
        }
        game.font.draw(game.batch, "Back", backButton.x, backButton.y + 15);
        game.batch.end();
    }

    private void handleKapitelInput() {
         if (Gdx.input.isButtonJustPressed(0)) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int i = 0; i < 8; i++) {
                 if(kapitelButtons[i].contains(touchX, touchY)) {
                     Gdx.app.log("Kapitel", "Kapitel " + (i + 1) + " clicked");
                     // Here you would start the selected chapter
                 }
            }
             if (backButton.contains(touchX, touchY)) {
                 currentState = ScreenState.MENU;
             }
         }
    }


    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        
        float buttonWidth = 200;
        float buttonHeight = 40;
        float buttonX = (width - buttonWidth) / 2;

        newGameButton.set(buttonX, height / 2 + 75, buttonWidth, buttonHeight);
        kapitelButton.set(buttonX, height / 2 + 25, buttonWidth, buttonHeight);
        loadButton.set(buttonX, height / 2 - 25, buttonWidth, buttonHeight);
        quitButton.set(buttonX, height / 2 - 75, buttonWidth, buttonHeight);

        float kapitelButtonX = width / 2 - 50;
        float kapitelStartY = height / 2 + 100;
        for (int i = 0; i < 8; i++) {
            kapitelButtons[i].set(kapitelButtonX, kapitelStartY - (i + 1) * 30 - 15, 100, 20);
        }
        backButton.set(kapitelButtonX, kapitelStartY - 9 * 30 - 15, 100, 20);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}
