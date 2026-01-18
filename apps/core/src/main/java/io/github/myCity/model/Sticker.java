package io.github.myCity.model;

/**
 * Represents a sticker that can be placed on or earned for the city grid.
 */
public class Sticker {

    private final StickerType type;
    private final Position position;
    private final String effectDescription;

    public Sticker(StickerType type, Position position, String effectDescription) {
        this.type = type;
        this.position = position;
        this.effectDescription = effectDescription;
    }

    public StickerType getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public String getEffectDescription() {
        return effectDescription;
    }
}
