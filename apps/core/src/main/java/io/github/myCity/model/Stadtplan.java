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
        this(true); // Calls the new constructor to maintain default behavior
    }

    public Stadtplan(boolean initializeDefaultTerrain) {
        this.raster = new Feld[11][11];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                raster[i][j] = new Feld(Geländetyp.WIESE); // Standard-Gelände
            }
        }
        this.sticker = new ArrayList<>();
        if (initializeDefaultTerrain) {
            initialisiereFluss();
            initialisiereGebirgeUndWald();
            initialisiereBäumeUndFelsen();
        }
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
        // Raster-Koordinaten: raster[y][x]
        raster[0][2].flussLinks = true;
        raster[0][2].flussRechts = false;
        raster[0][2].flussOben = true;
        raster[0][2].flussUnten = false;

        raster[0][1].flussLinks = false;
        raster[0][1].flussRechts = true;
        raster[0][1].flussOben = false;
        raster[0][1].flussUnten = false;

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

        raster[3][5].flussLinks = true;
        raster[3][5].flussRechts = false;
        raster[3][5].flussOben = false;
        raster[3][5].flussUnten = false;

        raster[4][4].flussLinks = false;
        raster[4][4].flussRechts = true;
        raster[4][4].flussOben = false;
        raster[4][4].flussUnten = false;

        raster[4][5].flussLinks = true;
        raster[4][5].flussRechts = false;
        raster[4][5].flussOben = false;
        raster[4][5].flussUnten = false;

        raster[5][4].flussLinks = false;
        raster[5][4].flussRechts = true;
        raster[5][4].flussOben = true;
        raster[5][4].flussUnten = false;

        raster[5][5].flussLinks = true;
        raster[5][5].flussRechts = false;
        raster[5][5].flussOben = false;
        raster[5][5].flussUnten = false;

        raster[5][3].flussLinks = false;
        raster[5][3].flussRechts = false;
        raster[5][3].flussOben = true;
        raster[5][3].flussUnten = false;

        raster[6][3].flussLinks = true;
        raster[6][3].flussRechts = false;
        raster[6][3].flussOben = true;
        raster[6][3].flussUnten = true;

        raster[6][2].flussLinks = false;
        raster[6][2].flussRechts = true;
        raster[6][2].flussOben = false;
        raster[6][2].flussUnten = false;

        raster[7][3].flussLinks = false;
        raster[7][3].flussRechts = true;
        raster[7][3].flussOben = false;
        raster[7][3].flussUnten = true;

        raster[7][4].flussLinks = true;
        raster[7][4].flussRechts = false;
        raster[7][4].flussOben = false;
        raster[7][4].flussUnten = false;

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

        raster[9][5].flussLinks = true;
        raster[9][5].flussRechts = false;
        raster[9][5].flussOben = false;
        raster[9][5].flussUnten = false;

        raster[10][4].flussLinks = false;
        raster[10][4].flussRechts = true;
        raster[10][4].flussOben = false;
        raster[10][4].flussUnten = false;

        raster[10][5].flussLinks = true;
        raster[10][5].flussRechts = false;
        raster[10][5].flussOben = false;
        raster[10][5].flussUnten = false;
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
        int tileId = plättchen.getId();
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;
                    if (weltX >= 0 && weltX < 11 && weltY >= 0 && weltY < 11) {
                        Feld feld = getFeld(weltX, weltY);
                        feld.setBelegt(true);
                        feld.setColor(color);
                        feld.setTileId(tileId);
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

    /**
     * Counts visible trees (uncovered BAUM fields).
     * Each visible tree = +1 point.
     */
    public int bäumeZählen() {
        int anzahl = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (raster[i][j].getGelände() == Geländetyp.BAUM && !raster[i][j].istBelegt()) {
                    anzahl += 1;
                }
            }
        }
        return anzahl;
    }

    /**
     * Counts visible rocks (uncovered FELS fields).
     * Each visible rock = -1 point.
     */
    public int felsenZählen() {
        int anzahl = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (raster[i][j].getGelände() == Geländetyp.FELS && !raster[i][j].istBelegt()) {
                    anzahl -= 1;
                }
            }
        }
        return anzahl;
    }

    /**
     * Counts empty light green spaces (uncovered WIESE fields that are buildable).
     * Each empty light green space = -1 point.
     * Excludes fields with rivers as they cannot be built on.
     */
    public int leereWiesenZählen() {
        int anzahl = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                Feld feld = raster[i][j];
                if (feld.getGelände() == Geländetyp.WIESE && !feld.istBelegt() && !feld.hatFluss()) {
                    anzahl -= 1;
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
                case STRUKTURSTICKER:
                case BONUSFELD:
                    // No terrain change for these sticker types
                    break;
            }
        }
    }

    public List<Sticker> getSticker() {
        return sticker;
    }

    /**
     * Counts how many fields are currently occupied by placed tiles.
     */
    public int countBelegteFelder() {
        int count = 0;
        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 11; x++) {
                Feld feld = raster[y][x];
                if (feld != null && feld.istBelegt()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Clears all placed buildings from the board while keeping stickers.
     * Used between episodes in Chapter 1.
     */
    public void clearBuildings() {
        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 11; x++) {
                Feld feld = raster[y][x];
                if (feld != null) {
                    feld.setBelegt(false);
                    feld.setColor(BuildingColor.NONE);
                    feld.setTileId(0);
                }
            }
        }
    }

    /**
     * Episode 2 Rule: Calculates bonus points for connected same-color tiles.
     * For each color (Blue, Yellow, Red), finds the largest connected group.
     * Counts TILES (Plättchen), not fields. Each tile counts as 1 point.
     * Returns the SUM of the largest connected group for each color.
     * Maximum possible: 8 + 8 + 8 = 24 points (if all tiles of each color are connected).
     */
    public int berechneVerbundeneFarbenBonus() {
        int totalBonus = 0;

        // Check for each color
        BuildingColor[] colors = {BuildingColor.BLUE, BuildingColor.YELLOW, BuildingColor.RED};

        for (BuildingColor color : colors) {
            boolean[][] visited = new boolean[11][11];
            int maxConnectedForColor = 0;

            for (int y = 0; y < 11; y++) {
                for (int x = 0; x < 11; x++) {
                    Feld feld = raster[y][x];
                    // Only count fields that are occupied, have the matching color, have a valid tileId, and haven't been visited
                    if (feld != null && feld.istBelegt() && feld.getColor() == color && feld.getTileId() > 0 && !visited[y][x]) {
                        // Count unique tiles in this connected group
                        java.util.Set<Integer> uniqueTileIds = new java.util.HashSet<>();
                        countConnectedTiles(x, y, color, visited, uniqueTileIds);
                        // Remove tileId 0 if somehow added (safety check)
                        uniqueTileIds.remove(0);
                        int tileCount = uniqueTileIds.size();
                        if (tileCount > maxConnectedForColor) {
                            maxConnectedForColor = tileCount;
                        }
                    }
                }
            }

            // Add the largest connected group for this color to total
            totalBonus += maxConnectedForColor;
        }

        return totalBonus;
    }

    /**
     * Helper method for flood-fill counting of connected same-color tiles.
     * Collects unique tile IDs in the connected group.
     * Only counts tiles with valid tileId > 0.
     */
    private void countConnectedTiles(int x, int y, BuildingColor color, boolean[][] visited, java.util.Set<Integer> uniqueTileIds) {
        if (x < 0 || x >= 11 || y < 0 || y >= 11) {
            return;
        }
        if (visited[y][x]) {
            return;
        }

        Feld feld = raster[y][x];
        // Only count fields that are occupied, have matching color, and have valid tileId
        if (feld == null || !feld.istBelegt() || feld.getColor() != color || feld.getTileId() <= 0) {
            return;
        }

        visited[y][x] = true;
        uniqueTileIds.add(feld.getTileId());

        // Check all 4 neighbors (up, down, left, right)
        countConnectedTiles(x + 1, y, color, visited, uniqueTileIds);
        countConnectedTiles(x - 1, y, color, visited, uniqueTileIds);
        countConnectedTiles(x, y + 1, color, visited, uniqueTileIds);
        countConnectedTiles(x, y - 1, color, visited, uniqueTileIds);
    }

    /**
     * Episode 3 Rule: Checks if all 4 corners around a Brunnen are covered with tiles.
     * Returns 4 if all corners are covered, 0 otherwise.
     */
    public int berechneBrunnenBonus() {
        for (Sticker s : sticker) {
            if (s.getTyp() == StickerTyp.BRUNNEN) {
                Position pos = s.getPos();
                int x = pos.getX();
                int y = pos.getY();

                // Check all 4 diagonal corners around the Brunnen
                boolean allCornersOccupied = true;

                // Top-left, Top-right, Bottom-left, Bottom-right
                int[][] corners = {
                    {x - 1, y - 1}, {x + 1, y - 1},
                    {x - 1, y + 1}, {x + 1, y + 1}
                };

                for (int[] corner : corners) {
                    int cx = corner[0];
                    int cy = corner[1];

                    if (cx < 0 || cx >= 11 || cy < 0 || cy >= 11) {
                        allCornersOccupied = false;
                        break;
                    }

                    Feld feld = raster[cy][cx];
                    if (feld == null || !feld.istBelegt()) {
                        allCornersOccupied = false;
                        break;
                    }
                }

                if (allCornersOccupied) {
                    return 4;
                }
            }
        }
        return 0;
    }

    /**
     * Checks if this Stadtplan has a Brunnen sticker.
     */
    public boolean hasBrunnen() {
        for (Sticker s : sticker) {
            if (s.getTyp() == StickerTyp.BRUNNEN) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the position of the Brunnen sticker, if any.
     */
    public Position getBrunnenPosition() {
        for (Sticker s : sticker) {
            if (s.getTyp() == StickerTyp.BRUNNEN) {
                return s.getPos();
            }
        }
        return null;
    }

    /**
     * Counts empty WIESE (light green) spaces for each row from top to bottom.
     * Used for tiebreaker rule: player with fewer empty spaces in top row wins.
     * If tied, check second row, etc.
     * @return array of 11 integers, each representing empty WIESE count for that row (index 0 = top row)
     */
    public int[] countEmptyWiesePerRow() {
        int[] emptyPerRow = new int[11];
        // Row 10 (y=10) is the top row visually, row 0 (y=0) is at the bottom
        for (int y = 10; y >= 0; y--) {
            int emptyCount = 0;
            for (int x = 0; x < 11; x++) {
                Feld feld = raster[y][x];
                if (feld != null && feld.getGelände() == Geländetyp.WIESE && !feld.istBelegt() && !feld.hatFluss()) {
                    emptyCount++;
                }
            }
            // Map y=10 to index 0 (top row), y=0 to index 10 (bottom row)
            emptyPerRow[10 - y] = emptyCount;
        }
        return emptyPerRow;
    }
}