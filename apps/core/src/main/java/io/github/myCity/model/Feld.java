package io.github.myCity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert ein einzelnes Feld auf dem Stadtplan.
 */
public class Feld {

    private Geländetyp gelände;
    private boolean belegt;
    public boolean flussOben;
    public boolean flussUnten;
    public boolean flussLinks;
    public boolean flussRechts;
    private BuildingColor color;
    private int tileId; // Tracks which tile (1-8) this field belongs to
    private final List<Sticker> stickers = new ArrayList<>();

    public Feld(Geländetyp gelände) {
        this.gelände = gelände;
        this.belegt = false;
        this.color = BuildingColor.NONE;
        this.tileId = 0;
    }

    public Geländetyp getGelände() {
        return gelände;
    }

    public void setGelände(Geländetyp gelände) {
        this.gelände = gelände;
    }

    public boolean istBelegt() {
        return belegt;
    }

    public void setBelegt(boolean belegt) {
        this.belegt = belegt;
    }

    public BuildingColor getColor() {
        return color;
    }

    public void setColor(BuildingColor color) {
        this.color = color;
    }

    public int getTileId() {
        return tileId;
    }

    public void setTileId(int tileId) {
        this.tileId = tileId;
    }

    public boolean hatFluss() {
        return flussOben || flussUnten || flussLinks || flussRechts;
    }

    public void addSticker(Sticker sticker) {
        this.stickers.add(sticker);
    }

    public List<Sticker> getStickers() {
        return stickers;
    }
}
