package io.github.myCity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.myCity.Main;

public class MenuScreen implements Screen {
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Vector3 touchPoint;

    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    private final Rectangle newGameButton;
    private final Rectangle kapitelButton;
    private final Rectangle loadButton;
    private final Rectangle quitButton;

    public MenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        touchPoint = new Vector3();

        float buttonWidth = 200;
        float buttonHeight = 40;
        float buttonX = (VIRTUAL_WIDTH - buttonWidth) / 2;
        float centerY = VIRTUAL_HEIGHT / 2;

        newGameButton = new Rectangle(buttonX, centerY + 75, buttonWidth, buttonHeight);
        kapitelButton = new Rectangle(buttonX, centerY + 25, buttonWidth, buttonHeight);
        loadButton = new Rectangle(buttonX, centerY - 25, buttonWidth, buttonHeight);
        quitButton = new Rectangle(buttonX, centerY - 75, buttonWidth, buttonHeight);
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        renderMenu();
        handleMenuInput();
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
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPoint);

            if (newGameButton.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log("Menu", "New Game clicked");
                game.setScreen(new GameScreen(game));
            } else if (kapitelButton.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log("Menu", "Kapitel clicked");
                game.setScreen(new KapitelScreen(game));
            } else if (loadButton.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log("Menu", "Load clicked");
                // Here you would load a game
            } else if (quitButton.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log("Menu", "Quit clicked");
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
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