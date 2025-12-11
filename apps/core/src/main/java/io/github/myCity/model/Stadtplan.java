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
        initialisiereGebirgeUndWald();
        initialisiereBäumeUndFelsen();
    }

    private void initialisiereGebirgeUndWald() {
        // Beispiel für Gebirge
        raster[4][0].setGelände(Geländetyp.GEBIRGE);
        raster[5][0].setGelände(Geländetyp.GEBIRGE);
        raster[6][0].setGelände(Geländetyp.GEBIRGE);
        raster[6][1].setGelände(Geländetyp.GEBIRGE);
        raster[7][0].setGelände(Geländetyp.GEBIRGE);
        raster[8][0].setGelände(Geländetyp.GEBIRGE);

        // Beispiel für Wald
        raster[8][10].setGelände(Geländetyp.WALD);
        raster[9][10].setGelände(Geländetyp.WALD);
        raster[0][10].setGelände(Geländetyp.WALD);
        raster[1][10].setGelände(Geländetyp.WALD);
        raster[2][10].setGelände(Geländetyp.WALD);
        raster[3][10].setGelände(Geländetyp.WALD);
        raster[4][10].setGelände(Geländetyp.WALD);
        raster[5][10].setGelände(Geländetyp.WALD);
        raster[6][10].setGelände(Geländetyp.WALD);
        raster[0][9].setGelände(Geländetyp.WALD);
        raster[1][9].setGelände(Geländetyp.WALD);
        raster[2][9].setGelände(Geländetyp.WALD);
        raster[3][9].setGelände(Geländetyp.WALD);
        raster[4][9].setGelände(Geländetyp.WALD);
        raster[5][9].setGelände(Geländetyp.WALD);
        raster[6][9].setGelände(Geländetyp.WALD);
        raster[0][8].setGelände(Geländetyp.WALD);
        raster[1][8].setGelände(Geländetyp.WALD);
        raster[4][8].setGelände(Geländetyp.WALD);
        raster[0][7].setGelände(Geländetyp.WALD);
    }

    private void initialisiereBäumeUndFelsen() {
        //Bäume
        raster[3][2].setGelände(Geländetyp.BAUM);
        raster[10][2].setGelände(Geländetyp.BAUM);
        raster[2][6].setGelände(Geländetyp.BAUM);
        raster[7][6].setGelände(Geländetyp.BAUM);
        raster[9][8].setGelände(Geländetyp.BAUM);


        //Felsen
        raster[1][0].setGelände(Geländetyp.FELS);
        raster[5][1].setGelände(Geländetyp.FELS);
        raster[9][0].setGelände(Geländetyp.FELS);
        raster[0][6].setGelände(Geländetyp.FELS);
        raster[2][8].setGelände(Geländetyp.FELS);
        raster[5][8].setGelände(Geländetyp.FELS);
        raster[7][10].setGelände(Geländetyp.FELS);
        raster[10][10].setGelände(Geländetyp.FELS);
    }

    /**
     * Initialisiert den Fluss auf dem Stadtplan gemäß den exakten Koordinaten.
     * Der Fluss verläuft entlang der RÄNDER der Felder, nicht in den Feldern selbst.
     */
    private void initialisiereFluss() {
        // Fluss-Koordinaten genau wie vorgegeben
        raster[0][2].flussLinks = true;
        raster[0][2].flussRechts = false;
        raster[0][2].flussOben = true;
        raster[0][2].flussUnten = false;

        raster[1][2].flussLinks = false;
        raster[1][2].flussRechts = true;
        raster[1][2].flussOben = false;
        raster[1][2].flussUnten = true;

        raster[1][3].flussLinks = true;
        raster[1][3].flussRechts = false;
        raster[1][3].flussOben = true;
        raster[1][3].flussUnten = false;

        raster[2][3].flussLinks = false;
        raster[2][3].flussRechts = true;
        raster[2][3].flussOben = false;
        raster[2][3].flussUnten = true;

        raster[2][4].flussLinks = true;
        raster[2][4].flussRechts = false;
        raster[2][4].flussOben = true;
        raster[2][4].flussUnten = false;

        raster[3][4].flussLinks = false;
        raster[3][4].flussRechts = true;
        raster[3][4].flussOben = false;
        raster[3][4].flussUnten = true;

        raster[4][4].flussLinks = false;
        raster[4][4].flussRechts = true;
        raster[4][4].flussOben = false;
        raster[4][4].flussUnten = false;

        raster[5][4].flussLinks = false;
        raster[5][4].flussRechts = true;
        raster[5][4].flussOben = true;
        raster[5][4].flussUnten = false;

        raster[5][3].flussLinks = false;
        raster[5][3].flussRechts = false;
        raster[5][3].flussOben = true;
        raster[5][3].flussUnten = false;

        raster[6][3].flussLinks = true;
        raster[6][3].flussRechts = false;
        raster[6][3].flussOben = true;
        raster[6][3].flussUnten = true;

        raster[7][3].flussLinks = false;
        raster[7][3].flussRechts = true;
        raster[7][3].flussOben = false;
        raster[7][3].flussUnten = true;

        raster[8][3].flussLinks = false;
        raster[8][3].flussRechts = true;
        raster[8][3].flussOben = false;
        raster[8][3].flussUnten = false;

        raster[8][4].flussLinks = true;
        raster[8][4].flussRechts = false;
        raster[8][4].flussOben = true;
        raster[8][4].flussUnten = false;

        raster[9][4].flussLinks = false;
        raster[9][4].flussRechts = true;
        raster[9][4].flussOben = false;
        raster[9][4].flussUnten = true;

        raster[10][4].flussLinks = false;
        raster[10][4].flussRechts = true;
        raster[10][4].flussOben = false;
        raster[10][4].flussUnten = false;
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
        BuildingColor color = plättchen.getColor();
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;
                    if (weltX >= 0 && weltX < 11 && weltY >= 0 && weltY < 11) {
                        Feld feld = getFeld(weltX, weltY);
                        feld.setBelegt(true);
                        feld.setColor(color); 
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
                    anzahl += 2;
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
                    anzahl -= 2;
                }
            }
        }
        return anzahl;
    }

    public void addSticker(Sticker newSticker) {
        this.sticker.add(newSticker);
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
            }
        }
    }

    public List<Sticker> getSticker() {
        return sticker;
    }
}