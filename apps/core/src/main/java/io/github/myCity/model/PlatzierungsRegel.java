package io.github.myCity.model;

public class PlatzierungsRegel {

    // ========== MAIN VALIDATION METHOD ==========

    public boolean prüfenPlättchen(Plättchen plättchen, Stadtplan stadtplan,
            Position pos, boolean erstes) {
        // Null checks
        if (plättchen == null || stadtplan == null || pos == null) {
            System.out.println("❌ Rule violation: Null parameter");
            return false;
        }

        // Rule 1: Check boundaries and overlaps
        if (!istInGrenzen(plättchen, stadtplan, pos)) {
            System.out.println("❌ Rule violation: Out of bounds or overlapping");
            return false;
        }

        // Rule 2: First tile must be adjacent to river (using edge-based river system)
        if (erstes) {
            if (!regelErstesGebäude(plättchen, stadtplan, pos)) {
                System.out.println("❌ Rule violation: First tile must be adjacent to river");
                return false;
            }
            // First tile doesn't need adjacency check
            return true;
        }

        // Rule 3: Must connect to existing buildings
        if (!regelAnlegenAnGebäude(plättchen, stadtplan, pos)) {
            System.out.println("❌ Rule violation: Tile must connect to existing buildings");
            return false;
        }

        // Rule 4: Cannot build on forbidden terrain (rocks, mountains)
        if (!regelKeineBebauung(plättchen, stadtplan, pos)) {
            System.out.println("❌ Rule violation: Cannot build on this terrain");
            return false;
        }

        // Rule 5: Cannot build on rivers (FLUSS terrain type)
        if (!regelNichtAufFluss(plättchen, stadtplan, pos)) {
            System.out.println("❌ Rule violation: Cannot build on river");
            return false;
        }

        // Rule 6: Boundary/marked area restrictions
        if (!regelNichtAufMarkiertenBereich(plättchen, stadtplan, pos)) {
            System.out.println("❌ Rule violation: Cannot build in marked area");
            return false;
        }

        // All rules passed!
        return true;
    }

    // ========== RULE IMPLEMENTATIONS ==========

    private boolean istInGrenzen(Plättchen plättchen, Stadtplan stadtplan, Position pos) {
        boolean[][] form = plättchen.getForm();

        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;

                    // Check if out of bounds (11x11 grid = 0 to 10)
                    if (weltX < 0 || weltX >= 11 || weltY < 0 || weltY >= 11) {
                        return false;
                    }

                    // Check if field is already occupied
                    Feld feld = stadtplan.getFeld(weltX, weltY);
                    if (feld == null || feld.istBelegt()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Rule: First tile must be ADJACENT to the river.
     * The river flows BETWEEN cells (marked by blue edges).
     * A field with river edges (flussOben, etc.) means the river flows ALONG that edge,
     * but the field itself is buildable!
     * The first tile just needs to have at least one cell that has a river edge.
     */
    private boolean regelErstesGebäude(Plättchen plättchen, Stadtplan stadtplan, Position pos) {
        boolean[][] form = plättchen.getForm();
        boolean touchesRiver = false;

        System.out.println("🔍 Checking first tile placement at position (" + pos.getX() + ", " + pos.getY() + ")");

        // Check each cell of the tile
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;

                    Feld feld = stadtplan.getFeld(weltX, weltY);
                    if (feld == null) {
                        System.out.println("  ⚠️ Field at (" + weltX + ", " + weltY + ") is null!");
                        continue;
                    }

                    System.out.println("  📍 Checking field (" + weltX + ", " + weltY + ")");
                    System.out.println("     Terrain: " + feld.getGelände());
                    System.out.println("     River edges: Top=" + feld.flussOben + ", Bottom=" + feld.flussUnten + 
                                     ", Left=" + feld.flussLinks + ", Right=" + feld.flussRechts);

                    // If this field has any river edge, it touches the river!
                    if (feld.hatFluss()) {
                        touchesRiver = true;
                        System.out.println("  ✓ This field touches the river!");
                        break;
                    }
                }
            }
            if (touchesRiver) break;
        }

        if (!touchesRiver) {
            System.out.println("❌ First tile must touch the river (have at least one river edge)!");
        } else {
            System.out.println("✓ First tile touches the river - placement allowed!");
        }

        return touchesRiver;
    }

    /**
     * Rule: Tile must connect to at least one existing building (orthogonally).
     */
    private boolean regelAnlegenAnGebäude(Plättchen plättchen, Stadtplan stadtplan, Position pos) {
        boolean[][] form = plättchen.getForm();

        // Check each cell of the tile
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;

                    // Check all 4 orthogonal neighbors
                    if (hatBelegtenNachbar(stadtplan, weltX, weltY)) {
                        return true; // Found adjacency!
                    }
                }
            }
        }

        return false; // No adjacency found
    }

    /**
     * Helper method: Checks if a position has at least one occupied orthogonal
     * neighbor.
     */
    private boolean hatBelegtenNachbar(Stadtplan stadtplan, int x, int y) {
        // Check 4 directions: up, down, left, right
        int[][] offsets = {
                { 0, -1 }, // up
                { 0, 1 }, // down
                { -1, 0 }, // left
                { 1, 0 } // right
        };

        for (int[] offset : offsets) {
            int nx = x + offset[0];
            int ny = y + offset[1];

            // Check bounds
            if (nx >= 0 && nx < 11 && ny >= 0 && ny < 11) {
                Feld feld = stadtplan.getFeld(nx, ny);
                if (feld != null && feld.istBelegt()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Rule: Cannot build on certain terrain types (mountains, forest, and river terrain).
     * Note: FELS (rocks) and BAUM (trees) are ALLOWED - they affect scoring but not placement.
     */
    private boolean regelKeineBebauung(Plättchen plättchen, Stadtplan stadtplan, Position pos) {
        boolean[][] form = plättchen.getForm();

        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;

                    Feld feld = stadtplan.getFeld(weltX, weltY);
                    if (feld != null) {
                        Geländetyp terrain = feld.getGelände();

                        System.out.println("  📍 Field (" + weltX + ", " + weltY + ") has terrain: " + terrain);

                        // Cannot build on mountains (GEBIRGE) or forest (WALD)
                        // FELS and BAUM are allowed but affect scoring:
                        // - Building on FELS = lose points at end
                        // - NOT building on BAUM = gain points at end
                        if (terrain == Geländetyp.GEBIRGE || terrain == Geländetyp.WALD) {
                            System.out.println("  ❌ Cannot build on " + terrain + " at (" + weltX + ", " + weltY + ")");
                            return false;
                        }
                        
                        if (terrain == Geländetyp.FELS || terrain == Geländetyp.BAUM) {
                            System.out.println("  ⚠️ Building on " + terrain + " at (" + weltX + ", " + weltY + ") - will affect score");
                        }
                    }
                }
            }
        }

        return true; // No forbidden terrain
    }

    /**
     * Rule: Cannot build on rivers (only FLUSS terrain type).
     * Note: River EDGES (flussOben, etc.) do NOT prevent building!
     * The river flows BETWEEN cells, not ON the cells.
     * Only cells with actual FLUSS terrain type cannot be built on.
     */
    private boolean regelNichtAufFluss(Plättchen plättchen, Stadtplan stadtplan, Position pos) {
        boolean[][] form = plättchen.getForm();

        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;

                    Feld feld = stadtplan.getFeld(weltX, weltY);
                    if (feld != null) {
                        // Check if field is a river (terrain type FLUSS)
                        // This would be a field that IS the river itself
                        if (feld.getGelände() == Geländetyp.FLUSS) {
                            System.out.println("  ❌ Cannot build on FLUSS terrain at (" + weltX + ", " + weltY + ")");
                            return false;
                        }
                        
                        // River edges are OK! The river flows along the edge, not on the field itself
                        if (feld.hatFluss()) {
                            System.out.println("  ℹ️ Field (" + weltX + ", " + weltY + ") has river edge - building allowed!");
                        }
                    }
                }
            }
        }

        return true; // No river violations
    }

    /**
     * Rule: Cannot build in marked boundary areas or restricted zones.
     */
    private boolean regelNichtAufMarkiertenBereich(Plättchen plättchen, Stadtplan stadtplan, Position pos) {
        boolean[][] form = plättchen.getForm();

        // Check for restricted zones marked by specific stickers
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;

                    Feld feld = stadtplan.getFeld(weltX, weltY);
                    if (feld != null) {
                        // Check if field has restriction stickers
                        for (Sticker sticker : feld.getStickers()) {
                            // Example: Some stickers might mark "no building" zones
                            if (sticker.getEffektBeschreibung().contains("restricted") ||
                                    sticker.getEffektBeschreibung().contains("forbidden")) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        // No boundary violations
        return true;
    }

    // ========== ADVANCED VALIDATION (Optional) ==========

    /**
     * Validates a placement with detailed error reporting.
     * Useful for GUI to show which specific rule was violated.
     */
    public String prüfenMitFehlerMeldung(Plättchen plättchen, Stadtplan stadtplan,
            Position pos, boolean erstes) {
        if (plättchen == null || stadtplan == null || pos == null) {
            return "Invalid parameters";
        }

        if (!istInGrenzen(plättchen, stadtplan, pos)) {
            return "Tile is out of bounds or overlaps existing buildings";
        }

        if (erstes) {
            if (!regelErstesGebäude(plättchen, stadtplan, pos)) {
                return "First tile must be adjacent to the river";
            }
            return null; // Valid!
        }

        if (!regelAnlegenAnGebäude(plättchen, stadtplan, pos)) {
            return "Tile must connect to at least one existing building";
        }

        if (!regelKeineBebauung(plättchen, stadtplan, pos)) {
            return "Cannot build on mountains (GEBIRGE) or forest (WALD)";
        }

        if (!regelNichtAufFluss(plättchen, stadtplan, pos)) {
            return "Cannot build on rivers (FLUSS)";
        }

        if (!regelNichtAufMarkiertenBereich(plättchen, stadtplan, pos)) {
            return "Cannot build in restricted area";
        }

        return null; // All rules passed!
    }

    /**
     * Quick validation without detailed output.
     */
    public boolean istGültig(Plättchen plättchen, Stadtplan stadtplan,
            Position pos, boolean erstes) {
        return prüfenPlättchen(plättchen, stadtplan, pos, erstes);
    }
}