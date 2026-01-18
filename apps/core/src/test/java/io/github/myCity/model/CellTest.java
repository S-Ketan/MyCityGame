package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    private Cell cell;
    private TerrainType forest = TerrainType.FOREST;

    @BeforeEach
    void setUp() {
        cell = new Cell(forest);
    }

    @Test
    void testConstructor() {
        assertEquals(forest, cell.getTerrain());
        assertFalse(cell.isOccupied());
        assertFalse(cell.hasRiver());
        assertTrue(cell.getStickers().isEmpty());
    }

    @Test
    void testSetTerrain() {
        cell.setTerrain(TerrainType.MOUNTAIN);
        assertEquals(TerrainType.MOUNTAIN, cell.getTerrain());
    }

    @Test
    void testSetOccupied() {
        cell.setOccupied(true);
        assertTrue(cell.isOccupied());
        cell.setOccupied(false);
        assertFalse(cell.isOccupied());
    }

    @Test
    void testHasRiver() {
        assertFalse(cell.hasRiver());
        cell.riverTop = true;
        assertTrue(cell.hasRiver());
        cell.riverTop = false;
        cell.riverBottom = true;
        assertTrue(cell.hasRiver());
        cell.riverBottom = false;
        cell.riverLeft = true;
        assertTrue(cell.hasRiver());
        cell.riverLeft = false;
        cell.riverRight = true;
        assertTrue(cell.hasRiver());
    }

    @Test
    void testAddAndGetStickers() {
        Sticker sticker1 = new Sticker(StickerType.ADD_TREE, new Position(0, 0), "Effect 1");
        Sticker sticker2 = new Sticker(StickerType.BONUS_FIELD, new Position(1, 1), "Effect 2");
        cell.addSticker(sticker1);
        cell.addSticker(sticker2);

        List<Sticker> stickers = cell.getStickers();
        assertEquals(2, stickers.size());
        assertTrue(stickers.contains(sticker1));
        assertTrue(stickers.contains(sticker2));
    }
}
