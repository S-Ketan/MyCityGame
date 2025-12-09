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

        // Rule 2: First tile must be on building site
        if (erstes) {
            if (!regelErstesGebäude(plättchen, stadtplan, pos)) {
                System.out.println("❌ Rule violation: First tile must touch river but not be on it");
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

        // Rule 5: Cannot build on rivers (with some exceptions)
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
     * Rule: First tile must TOUCH the river but NOT be placed ON the river.
     */
    private boolean regelErstesGebäude(Plättchen plättchen, Stadtplan stadtplan, Position pos) {
        boolean[][] form = plättchen.getForm();
        boolean touchesRiver = false;

        // Step 1: Check that NO part of the tile is ON the river
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;

                    Feld feld = stadtplan.getFeld(weltX, weltY);
                    if (feld != null && feld.getGelände() == Geländetyp.FLUSS) {
                        // Tile is ON the river - not allowed!
                        return false;
                    }
                }
            }
        }

        // Step 2: Check that AT LEAST ONE cell touches the river
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;

                    // Check all 4 orthogonal neighbors for river
                    if (hatFlussNachbar(stadtplan, weltX, weltY)) {
                        touchesRiver = true;
                        break;
                    }
                }
            }
            if (touchesRiver)
                break;
        }

        return touchesRiver; // Must touch river to be valid
    }

    /**
     * Helper method: Checks if a position has at least one river neighbor.
     */
    private boolean hatFlussNachbar(Stadtplan stadtplan, int x, int y) {
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
                if (feld != null && feld.getGelände() == Geländetyp.FLUSS) {
                    return true; // Found a river neighbor!
                }
            }
        }

        return false;
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
     * Rule: Cannot build on certain terrain types (rocks, mountains).
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

                        // Cannot build on rocks or mountains
                        if (terrain == Geländetyp.FELS || terrain == Geländetyp.GEBIRGE) {
                            return false;
                        }
                    }
                }
            }
        }

        return true; // No forbidden terrain
    }

    /**
     * Rule: Cannot build on rivers (unless allowed by specific episodes).
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
                        // Check if field is a river
                        if (feld.getGelände() == Geländetyp.FLUSS) {
                            return false; // Cannot build on river
                        }

                        // Also check if field has river borders (from Feld class)
                        if (feld.hatFluss()) {
                            // River passes through this field
                            // Depending on episode rules, this might be forbidden
                            // For now, we allow it (only pure FLUSS terrain is forbidden)
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
                return "First tile must touch the river but cannot be placed on it";
            }
            return null; // Valid!
        }

        if (!regelAnlegenAnGebäude(plättchen, stadtplan, pos)) {
            return "Tile must connect to at least one existing building";
        }

        if (!regelKeineBebauung(plättchen, stadtplan, pos)) {
            return "Cannot build on rocks (FELS) or mountains (GEBIRGE)";
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