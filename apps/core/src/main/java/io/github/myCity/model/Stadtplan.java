package io.github.myCity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert den persönlichen Stadtplan eines Spielers.
 */
public class Stadtplan {

    private final Feld[][] raster;
    private final List<Sticker> sticker;

    public Stadtplan() {
        this.raster = new Feld[11][11];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                raster[i][j] = new Feld(Geländetyp.WIESE); // Standard-Gelände
            }
        }
        this.sticker = new ArrayList<>();
        initialisiereFluss();
    }

    /**
     * Initialisiert den Fluss auf dem Stadtplan.
     * Dies ist eine Beispielimplementierung. Die genaue Position des Flusses
     * sollte gemäss den Spielregeln erfolgen.
     */
    private void initialisiereFluss() {
        // Beispiel: Ein vertikaler Fluss in der Mitte
        for (int i = 0; i < 11; i++) {
            raster[i][5].setGelände(Geländetyp.FLUSS);
            raster[i][5].flussLinks = false;
            raster[i][5].flussRechts = false;

            if (i > 0) {
                 raster[i][5].flussOben = true;
                 raster[i-1][5].flussUnten = true;
            }
        }
    }


    /**
     * Platziert ein Plättchen auf dem Stadtplan an der gegebenen Position.
     * Annahme: Die Platzierungsregeln wurden bereits geprüft.
     *
     * @param plättchen Das zu platzierende Plättchen.
     * @param pos       Die Position der oberen linken Ecke des Plättchens.
     */
    public void plättchenPlatzieren(Plättchen plättchen, Position pos) {
        boolean[][] form = plättchen.getForm();
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;
                    if (weltX >= 0 && weltX < 11 && weltY >= 0 && weltY < 11) {
                        getFeld(weltX, weltY).setBelegt(true);
                    }
                }
            }
        }
    }

    public Feld getFeld(int x, int y) {
        if (x >= 0 && x < 11 && y >= 0 && y < 11) {
            return raster[y][x];
        }
        return null;
    }

    public int bäumeZählen() {
        int anzahl = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (raster[i][j].getGelände() == Geländetyp.BAUM && !raster[i][j].istBelegt()) {
                    anzahl+=2;
                }
            }
        }
        return anzahl;
    }

    public int felsenZählen() {
        int anzahl = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (raster[i][j].getGelände() == Geländetyp.FELS && !raster[i][j].istBelegt()) {
                    anzahl-=2;
                }
            }
        }
        return anzahl;
    }

    public void addSticker(Sticker newSticker) {
        this.sticker.add(newSticker);
        // Optional: Logik, um den Sticker-Effekt direkt anzuwenden
        Position pos = newSticker.getPos();
        Feld feld = getFeld(pos.getX(), pos.getY());
        if (feld != null) {
            feld.addSticker(newSticker);
            switch (newSticker.getTyp()) {
                case FELS_HINZUFÜGEN:
                    feld.setGelände(Geländetyp.FELS);
                    break;
                case BAUM_HINZUFÜGEN:
                    feld.setGelände(Geländetyp.BAUM);
                    break;
                // Andere Sticker-Typen hier behandeln
            }
        }
    }

    public List<Sticker> getSticker() {
        return sticker;
    }
}
