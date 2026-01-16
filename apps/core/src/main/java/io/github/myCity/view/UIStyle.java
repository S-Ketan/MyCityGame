package io.github.myCity.view;

import com.badlogic.gdx.graphics.Color;

/**
 * Shared palette for the UI. Matches the refined GameScreen styling so all screens stay consistent.
 */
public final class UIStyle {
    private UIStyle() {}

    public static final Color BACKGROUND = new Color(0.0549f, 0.0784f, 0.1059f, 1f);      // #0E141B
    public static final Color PANEL = new Color(0.1725f, 0.2431f, 0.3137f, 1f);            // #2C3E50
    public static final Color SECONDARY = new Color(0.2039f, 0.2863f, 0.3686f, 1f);        // #34495E
    public static final Color SECONDARY_SOFT = new Color(0.2275f, 0.3098f, 0.3882f, 1f);   // #3A4F63
    public static final Color DIVIDER = new Color(0.2902f, 0.3843f, 0.4784f, 1f);          // #4A627A
    public static final Color PRIMARY_TEXT = new Color(0.9255f, 0.9412f, 0.9451f, 1f);     // #ECF0F1
    public static final Color SECONDARY_TEXT = new Color(0.6667f, 0.7176f, 0.7686f, 1f);   // #AAB7C4
    public static final Color ACCENT = new Color(0.9529f, 0.6118f, 0.0706f, 1f);           // #F39C12
    public static final Color BUTTON = new Color(0.2275f, 0.4314f, 0.6471f, 1f);           // #3A6EA5
    public static final Color BUTTON_BORDER = DIVIDER;

    public static final Color POSITIVE = new Color(0.35f, 0.8f, 0.55f, 1f);
    public static final Color NEGATIVE = new Color(0.9f, 0.3f, 0.3f, 1f);
}
