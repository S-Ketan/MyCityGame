package io.github.myCity.model;

/**
 * Repräsentiert einen Sticker, der auf dem Stadtplan platziert oder verdient werden kann.
 */
public class Sticker {

    private final StickerTyp typ;
    private final Position pos;
    private final String effektBeschreibung;

    public Sticker(StickerTyp typ, Position pos, String effektBeschreibung) {
        this.typ = typ;
        this.pos = pos;
        this.effektBeschreibung = effektBeschreibung;
    }

    public StickerTyp getTyp() {
        return typ;
    }

    public Position getPos() {
        return pos;
    }

    public String getEffektBeschreibung() {
        return effektBeschreibung;
    }
}
