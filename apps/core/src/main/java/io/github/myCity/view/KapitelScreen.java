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

public class KapitelScreen implements Screen {
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Vector3 touchPoint;

    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    private final Rectangle backButton;
    private final Rectangle[] kapitelButtons;

    public KapitelScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        touchPoint = new Vector3();
        
        float kapitelButtonWidth = 100;
        float kapitelButtonX = (VIRTUAL_WIDTH - kapitelButtonWidth) / 2;
        float kapitelStartY = VIRTUAL_HEIGHT / 2 + 100; // A bit above center

        kapitelButtons = new Rectangle[8];
        for (int i = 0; i < 8; i++) {
            kapitelButtons[i] = new Rectangle(kapitelButtonX, kapitelStartY - (i + 1) * 30 - 15, kapitelButtonWidth, 20);
        }
        backButton = new Rectangle(kapitelButtonX, kapitelStartY - 9 * 30 - 15, kapitelButtonWidth, 20);
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        renderKapitel();
        handleKapitelInput();
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
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPoint);

            for (int i = 0; i < 8; i++) {
                 if(kapitelButtons[i].contains(touchPoint.x, touchPoint.y)) {
                     Gdx.app.log("Kapitel", "Kapitel " + (i + 1) + " clicked");
                     // Here you would start the selected chapter
                 }
            }
             if (backButton.contains(touchPoint.x, touchPoint.y)) {
                 game.setScreen(new MenuScreen(game));
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