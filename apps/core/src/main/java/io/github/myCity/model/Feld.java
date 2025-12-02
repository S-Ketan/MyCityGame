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
    private final List<Sticker> stickers = new ArrayList<>();

    public Feld(Geländetyp gelände) {
        this.gelände = gelände;
        this.belegt = false;
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
