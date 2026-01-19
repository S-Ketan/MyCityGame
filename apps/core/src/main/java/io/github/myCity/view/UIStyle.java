package io.github.myCity.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * Shared palette for the UI. Matches the refined GameScreen styling so all screens stay consistent.
 */
public final class UIStyle {
    private UIStyle() {}

    public static final Color BACKGROUND = new Color(0.08f, 0.07f, 0.06f, 1f);             // Dark warm gray
    public static final Color PANEL = new Color(0.18f, 0.16f, 0.14f, 1f);                  // Warm dark brown
    public static final Color SECONDARY = new Color(0.24f, 0.21f, 0.18f, 1f);              // Warm medium brown
    public static final Color SECONDARY_SOFT = new Color(0.28f, 0.25f, 0.22f, 1f);         // Warm soft brown
    public static final Color DIVIDER = new Color(0.38f, 0.34f, 0.30f, 1f);                // Warm divider
    public static final Color PRIMARY_TEXT = new Color(0.9255f, 0.9412f, 0.9451f, 1f);     // #ECF0F1
    public static final Color SECONDARY_TEXT = new Color(0.6667f, 0.7176f, 0.7686f, 1f);   // #AAB7C4
    public static final Color ACCENT = new Color(0.9529f, 0.6118f, 0.0706f, 1f);           // #F39C12
    public static final Color BUTTON = new Color(0.55f, 0.42f, 0.28f, 1f);                 // Warm bronze/amber
    public static final Color BUTTON_HOVER = new Color(0.65f, 0.52f, 0.38f, 1f);           // Lighter bronze for hover
    public static final Color BUTTON_BORDER = new Color(0.45f, 0.35f, 0.22f, 1f);          // Darker bronze border

    public static final Color POSITIVE = new Color(0.35f, 0.8f, 0.55f, 1f);
    public static final Color NEGATIVE = new Color(0.9f, 0.3f, 0.3f, 1f);

    /** Default corner radius for macOS-style rounded rectangles */
    public static final float CORNER_RADIUS = 12f;
    public static final float BUTTON_CORNER_RADIUS = 10f;
    public static final float SMALL_CORNER_RADIUS = 6f;

    /**
     * Draws a filled rounded rectangle with the specified corner radius.
     * Must be called within a ShapeRenderer.begin(ShapeType.Filled) block.
     */
    public static void drawRoundedRect(ShapeRenderer sr, float x, float y, float width, float height, float radius) {
        // Clamp radius to half the minimum dimension
        radius = Math.min(radius, Math.min(width, height) / 2f);

        // Draw the 4 corner circles
        sr.circle(x + radius, y + radius, radius);                           // Bottom-left
        sr.circle(x + width - radius, y + radius, radius);                   // Bottom-right
        sr.circle(x + width - radius, y + height - radius, radius);          // Top-right
        sr.circle(x + radius, y + height - radius, radius);                  // Top-left

        // Draw the 3 rectangles to fill the center and edges
        sr.rect(x + radius, y, width - 2 * radius, height);                  // Center vertical
        sr.rect(x, y + radius, radius, height - 2 * radius);                 // Left edge
        sr.rect(x + width - radius, y + radius, radius, height - 2 * radius);// Right edge
    }

    /**
     * Draws a rounded rectangle border (outline only).
     * Must be called within a ShapeRenderer.begin(ShapeType.Line) block.
     */
    public static void drawRoundedRectBorder(ShapeRenderer sr, float x, float y, float width, float height, float radius) {
        // Clamp radius to half the minimum dimension
        radius = Math.min(radius, Math.min(width, height) / 2f);

        // Draw the 4 corner arcs
        sr.arc(x + radius, y + radius, radius, 180, 90);                     // Bottom-left
        sr.arc(x + width - radius, y + radius, radius, 270, 90);             // Bottom-right
        sr.arc(x + width - radius, y + height - radius, radius, 0, 90);      // Top-right
        sr.arc(x + radius, y + height - radius, radius, 90, 90);             // Top-left

        // Draw the 4 edge lines
        sr.line(x + radius, y, x + width - radius, y);                       // Bottom
        sr.line(x + width, y + radius, x + width, y + height - radius);      // Right
        sr.line(x + radius, y + height, x + width - radius, y + height);     // Top
        sr.line(x, y + radius, x, y + height - radius);                      // Left
    }

    /**
     * Creates a NinePatchDrawable with rounded corners for Scene2D buttons.
     * The caller is responsible for disposing the returned drawable's texture.
     */
    public static NinePatchDrawable createRoundedDrawable(Color color, int radius) {
        int size = radius * 2 + 4;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);

        // Draw filled rounded rectangle
        // Center rectangle
        pixmap.fillRectangle(radius, 0, size - radius * 2, size);
        // Left and right strips
        pixmap.fillRectangle(0, radius, radius, size - radius * 2);
        pixmap.fillRectangle(size - radius, radius, radius, size - radius * 2);

        // Draw corner circles
        pixmap.fillCircle(radius, radius, radius);                    // Bottom-left
        pixmap.fillCircle(size - radius - 1, radius, radius);         // Bottom-right
        pixmap.fillCircle(radius, size - radius - 1, radius);         // Top-left
        pixmap.fillCircle(size - radius - 1, size - radius - 1, radius); // Top-right

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        NinePatch ninePatch = new NinePatch(texture, radius + 1, radius + 1, radius + 1, radius + 1);
        return new NinePatchDrawable(ninePatch);
    }
}
