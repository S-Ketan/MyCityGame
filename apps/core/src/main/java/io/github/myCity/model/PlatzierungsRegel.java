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
     * Rule: First tile must be ADJACENT to the river (using edge-based river system).
     * The river is represented by edges (flussOben, flussUnten, flussLinks, flussRechts).
     * The first tile must touch a river edge but NOT be placed ON a river field.
     */
    private boolean regelErstesGebäude(Plättchen plättchen, Stadtplan stadtplan, Position pos) {
        boolean[][] form = plättchen.getForm();
        boolean adjacentToRiver = false;

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
                    System.out.println("     River edges: Top=" + feld.flussOben + ", Bottom=" + feld.flussUnten + 
                                     ", Left=" + feld.flussLinks + ", Right=" + feld.flussRechts);

                    // First tile CANNOT be on a river field itself
                    if (feld.hatFluss()) {
                        System.out.println("  ❌ First tile cannot be ON river field at (" + weltX + ", " + weltY + ")");
                        return false;
                    }

                    // Check if any neighbor has a river edge (= this cell is adjacent to river)
                    if (hatNachbarMitFluss(stadtplan, weltX, weltY)) {
                        adjacentToRiver = true;
                        System.out.println("  ✓ Found adjacent river at field (" + weltX + ", " + weltY + ")");
                    }
                }
            }
        }

        if (!adjacentToRiver) {
            System.out.println("❌ First tile must be adjacent to river!");
        }

        return adjacentToRiver;
    }
    
    /**
     * Helper: Checks if a position has at least one neighbor that is a river field.
     */
    private boolean hatNachbarMitFluss(Stadtplan stadtplan, int x, int y) {
        // Check all 4 orthogonal neighbors
        int[][] offsets = {
            {0, -1},  // bottom
            {0, 1},   // top
            {-1, 0},  // left
            {1, 0}    // right
        };

        for (int[] offset : offsets) {
            int nx = x + offset[0];
            int ny = y + offset[1];

            if (nx >= 0 && nx < 11 && ny >= 0 && ny < 11) {
                Feld nachbar = stadtplan.getFeld(nx, ny);
                if (nachbar != null && nachbar.hatFluss()) {
                    System.out.println("    → Neighbor (" + nx + ", " + ny + ") is a river field");
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Helper method: Checks if a position has a river edge on any of its borders.
     * This includes checking neighboring cells for river edges that border this cell.
     */
    private boolean hatFlussAnGrenze(Stadtplan stadtplan, int x, int y) {
        Feld feld = stadtplan.getFeld(x, y);
        if (feld != null && feld.hatFluss()) {
            System.out.println("    → Field itself has river edge");
            return true;
        }

        // Check neighbors for river edges pointing towards this cell
        // Check top neighbor (y+1) for flussUnten
        Feld topNeighbor = stadtplan.getFeld(x, y + 1);
        if (topNeighbor != null && topNeighbor.flussUnten) {
            System.out.println("    → Top neighbor (" + x + ", " + (y+1) + ") has river on bottom edge");
            return true;
        }

        // Check bottom neighbor (y-1) for flussOben
        Feld bottomNeighbor = stadtplan.getFeld(x, y - 1);
        if (bottomNeighbor != null && bottomNeighbor.flussOben) {
            System.out.println("    → Bottom neighbor (" + x + ", " + (y-1) + ") has river on top edge");
            return true;
        }

        // Check left neighbor (x-1) for flussRechts
        Feld leftNeighbor = stadtplan.getFeld(x - 1, y);
        if (leftNeighbor != null && leftNeighbor.flussRechts) {
            System.out.println("    → Left neighbor (" + (x-1) + ", " + y + ") has river on right edge");
            return true;
        }

        // Check right neighbor (x+1) for flussLinks
        Feld rightNeighbor = stadtplan.getFeld(x + 1, y);
        if (rightNeighbor != null && rightNeighbor.flussLinks) {
            System.out.println("    → Right neighbor (" + (x+1) + ", " + y + ") has river on left edge");
            return true;
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
     * Rule: Cannot build on rivers.
     * In this game, rivers are represented by EDGES (flussOben, flussUnten, etc.).
     * Fields that have ANY river edge cannot be built on (they are "river fields").
     * Exception: First tile must be ADJACENT to river, so it can touch river edges.
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
                        if (feld.getGelände() == Geländetyp.FLUSS) {
                            System.out.println("  ❌ Cannot build on FLUSS terrain at (" + weltX + ", " + weltY + ")");
                            return false;
                        }
                        
                        // Check if field has river edges - these fields are "river fields"
                        // and cannot be built on
                        if (feld.hatFluss()) {
                            System.out.println("  ❌ Cannot build on river field at (" + weltX + ", " + weltY + ") - has river edges");
                            return false;
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