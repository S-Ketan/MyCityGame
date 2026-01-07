package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StadtplanTest {

    private Stadtplan stadtplan;

    @BeforeEach
    void setUp() {
        stadtplan = new Stadtplan(false); // Use the new constructor for tests
    }

    @Test
    void testConstructor() {
        assertNotNull(stadtplan);
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                assertNotNull(stadtplan.getFeld(i, j));
            }
        }
    }

    @Test
    void testInitialisiereFluss() {
        Stadtplan stadtplanWithRiver = new Stadtplan(true); // Create a Stadtplan that initializes the river

        // Check a point on the river path (raster[5][4] has flussRechts=true, flussOben=true)
        // getFeld(x,y) returns raster[y][x], so getFeld(4,5) returns raster[5][4]
        Feld riverTile = stadtplanWithRiver.getFeld(4, 5);
        assertNotNull(riverTile);
        assertTrue(riverTile.hatFluss(), "Tile at (4,5) should be part of the river.");
        assertEquals(Geländetyp.WIESE, riverTile.getGelände(), "River tiles should have WIESE as their base terrain.");
        assertTrue(riverTile.flussRechts);
        assertTrue(riverTile.flussOben);

        // Check another river tile (raster[6][3] has flussLinks=true, flussOben=true, flussUnten=true)
        Feld cornerTile = stadtplanWithRiver.getFeld(3, 6);
        assertNotNull(cornerTile);
        assertTrue(cornerTile.hatFluss(), "Tile at (3,6) should be part of the river.");
        assertTrue(cornerTile.flussLinks);
        assertTrue(cornerTile.flussOben);
        assertTrue(cornerTile.flussUnten);

        // Check a non-river tile
        Feld nonRiverTile = stadtplanWithRiver.getFeld(0, 0);
        assertNotNull(nonRiverTile);
        assertFalse(nonRiverTile.hatFluss(), "Tile at (0,0) should not be part of the river.");
        assertEquals(Geländetyp.WIESE, nonRiverTile.getGelände());
    }

    @Test
    void testGetFeld() {
        assertNotNull(stadtplan.getFeld(0, 0));
        assertNotNull(stadtplan.getFeld(10, 10));
        assertNull(stadtplan.getFeld(-1, 0));
        assertNull(stadtplan.getFeld(0, -1));
        assertNull(stadtplan.getFeld(11, 0));
        assertNull(stadtplan.getFeld(0, 11));
    }

    @Test
    void testPlättchenPlatzieren() {
        boolean[][] form = {{true, true}, {false, true}};
        Plättchen plättchen = new Plättchen(1, form);
        Position pos = new Position(1, 1);

        stadtplan.plättchenPlatzieren(plättchen, pos);

        assertTrue(stadtplan.getFeld(1, 1).istBelegt());
        assertTrue(stadtplan.getFeld(2, 1).istBelegt());
        assertFalse(stadtplan.getFeld(1, 2).istBelegt()); // this part of the shape is false
        assertTrue(stadtplan.getFeld(2, 2).istBelegt());

        // Check a field outside the placed tile
        assertFalse(stadtplan.getFeld(0, 0).istBelegt());
    }

    @Test
    void testBäumeZählen() {
        assertEquals(0, stadtplan.bäumeZählen());
        stadtplan.getFeld(2,2).setGelände(Geländetyp.BAUM);
        // Each visible tree is worth +1 point
        assertEquals(1, stadtplan.bäumeZählen());
        stadtplan.getFeld(2,3).setGelände(Geländetyp.BAUM);
        assertEquals(2, stadtplan.bäumeZählen());
        // A covered tree should not be counted
        stadtplan.getFeld(2,3).setBelegt(true);
        assertEquals(1, stadtplan.bäumeZählen());
    }

    @Test
    void testFelsenZählen() {
        assertEquals(0, stadtplan.felsenZählen());
        stadtplan.getFeld(2,2).setGelände(Geländetyp.FELS);
        // Each visible rock is worth -1 point
        assertEquals(-1, stadtplan.felsenZählen());
        stadtplan.getFeld(2,3).setGelände(Geländetyp.FELS);
        assertEquals(-2, stadtplan.felsenZählen());
        // A covered rock should not be counted
        stadtplan.getFeld(2,3).setBelegt(true);
        assertEquals(-1, stadtplan.felsenZählen());
    }

    @Test
    void testLeereWiesenZählen() {
        // All 121 fields start as empty WIESE (without rivers)
        Stadtplan plan = new Stadtplan(false);
        assertEquals(-121, plan.leereWiesenZählen());

        // Cover some fields
        plan.getFeld(0,0).setBelegt(true);
        plan.getFeld(1,1).setBelegt(true);
        assertEquals(-119, plan.leereWiesenZählen());

        // Fields with other terrain types don't count
        plan.getFeld(2,2).setGelände(Geländetyp.BAUM);
        plan.getFeld(3,3).setGelände(Geländetyp.FELS);
        assertEquals(-117, plan.leereWiesenZählen());
    }

    @Test
    void testScoreCalculationWithRealBoard() {
        // Test with a real initialized board
        Stadtplan plan = new Stadtplan(true);

        int trees = plan.bäumeZählen();
        int rocks = plan.felsenZählen();
        int emptyWiesen = plan.leereWiesenZählen();

        // Board has 5 trees, 8 rocks
        assertEquals(5, trees, "Should have 5 visible trees (+5 points)");
        assertEquals(-8, rocks, "Should have 8 visible rocks (-8 points)");

        // Print for debugging
        System.out.println("Trees: " + trees);
        System.out.println("Rocks: " + rocks);
        System.out.println("Empty WIESE: " + emptyWiesen);

        // Calculate score without skips
        int baseScore = 10 + trees + rocks + emptyWiesen;
        System.out.println("Base score (10 + trees + rocks + emptyWiesen): " + baseScore);

        // If all 24 tiles skipped
        int scoreWith24Skips = baseScore - 24;
        System.out.println("Score with 24 skips: " + scoreWith24Skips);

        // The score should be very negative and clamp to 0
        assertTrue(scoreWith24Skips < 0, "Score with all skips should be negative before clamping");
    }

    @Test
    void testAddSticker() {
        Position pos = new Position(3, 3);
        Sticker sticker = new Sticker(StickerTyp.BAUM_HINZUFÜGEN, pos, "Add tree");
        
        // Ensure field is not a tree before adding sticker
        assertNotEquals(Geländetyp.BAUM, stadtplan.getFeld(3, 3).getGelände());

        stadtplan.addSticker(sticker);

        assertEquals(1, stadtplan.getSticker().size());
        assertEquals(sticker, stadtplan.getSticker().get(0));
        // Check effect of sticker
        assertEquals(Geländetyp.BAUM, stadtplan.getFeld(3, 3).getGelände());
        assertTrue(stadtplan.getFeld(3, 3).getStickers().contains(sticker));
    }
}
