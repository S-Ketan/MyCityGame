package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StadtplanTest {

    private Stadtplan stadtplan;

    @BeforeEach
    void setUp() {
        stadtplan = new Stadtplan();
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
        // Check a point on the vertical part of the L-shaped river
        Feld verticalRiverTile = stadtplan.getFeld(5, 6);
        assertNotNull(verticalRiverTile);
        assertTrue(verticalRiverTile.hatFluss(), "Tile at (5,6) should be part of the river.");
        assertEquals(Geländetyp.WIESE, verticalRiverTile.getGelände(), "River tiles should have WIESE as their base terrain.");
        assertTrue(verticalRiverTile.flussOben);
        assertTrue(verticalRiverTile.flussUnten);

        // Check the corner tile
        Feld cornerTile = stadtplan.getFeld(5, 4);
        assertNotNull(cornerTile);
        assertTrue(cornerTile.hatFluss(), "Tile at (5,4) should be the corner of the river.");
        assertTrue(cornerTile.flussUnten);
        assertTrue(cornerTile.flussLinks);


        // Check a non-river tile
        Feld nonRiverTile = stadtplan.getFeld(0, 0);
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
        // Each tree is worth 2 points
        assertEquals(2, stadtplan.bäumeZählen());
        stadtplan.getFeld(2,3).setGelände(Geländetyp.BAUM);
        assertEquals(4, stadtplan.bäumeZählen());
        // A covered tree should not be counted
        stadtplan.getFeld(2,3).setBelegt(true);
        assertEquals(2, stadtplan.bäumeZählen());
    }

    @Test
    void testFelsenZählen() {
        assertEquals(0, stadtplan.felsenZählen());
        stadtplan.getFeld(2,2).setGelände(Geländetyp.FELS);
        // Each rock is worth -2 points
        assertEquals(-2, stadtplan.felsenZählen());
        stadtplan.getFeld(2,3).setGelände(Geländetyp.FELS);
        assertEquals(-4, stadtplan.felsenZählen());
        // A covered rock should not be counted
        stadtplan.getFeld(2,3).setBelegt(true);
        assertEquals(-2, stadtplan.felsenZählen());
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
