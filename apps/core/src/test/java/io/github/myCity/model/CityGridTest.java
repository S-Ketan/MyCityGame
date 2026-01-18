package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CityGridTest {

    private CityGrid cityGrid;

    @BeforeEach
    void setUp() {
        cityGrid = new CityGrid(false);
    }

    @Test
    void testConstructor() {
        assertNotNull(cityGrid);
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                assertNotNull(cityGrid.getCell(i, j));
            }
        }
    }

    @Test
    void testInitializeRiver() {
        CityGrid cityGridWithRiver = new CityGrid(true);

        Cell riverCell = cityGridWithRiver.getCell(4, 5);
        assertNotNull(riverCell);
        assertTrue(riverCell.hasRiver(), "Cell at (4,5) should be part of the river.");
        assertEquals(TerrainType.MEADOW, riverCell.getTerrain(), "River cells should have MEADOW as their base terrain.");
        assertTrue(riverCell.riverRight);
        assertTrue(riverCell.riverTop);

        Cell cornerCell = cityGridWithRiver.getCell(3, 6);
        assertNotNull(cornerCell);
        assertTrue(cornerCell.hasRiver(), "Cell at (3,6) should be part of the river.");
        assertTrue(cornerCell.riverLeft);
        assertTrue(cornerCell.riverTop);
        assertTrue(cornerCell.riverBottom);

        Cell nonRiverCell = cityGridWithRiver.getCell(0, 0);
        assertNotNull(nonRiverCell);
        assertFalse(nonRiverCell.hasRiver(), "Cell at (0,0) should not be part of the river.");
        assertEquals(TerrainType.MEADOW, nonRiverCell.getTerrain());
    }

    @Test
    void testGetCell() {
        assertNotNull(cityGrid.getCell(0, 0));
        assertNotNull(cityGrid.getCell(10, 10));
        assertNull(cityGrid.getCell(-1, 0));
        assertNull(cityGrid.getCell(0, -1));
        assertNull(cityGrid.getCell(11, 0));
        assertNull(cityGrid.getCell(0, 11));
    }

    @Test
    void testPlaceTile() {
        boolean[][] shape = {{true, true}, {false, true}};
        Tile tile = new Tile(1, shape);
        Position pos = new Position(1, 1);

        cityGrid.placeTile(tile, pos);

        assertTrue(cityGrid.getCell(1, 1).isOccupied());
        assertTrue(cityGrid.getCell(2, 1).isOccupied());
        assertFalse(cityGrid.getCell(1, 2).isOccupied());
        assertTrue(cityGrid.getCell(2, 2).isOccupied());

        assertFalse(cityGrid.getCell(0, 0).isOccupied());
    }

    @Test
    void testCountTrees() {
        assertEquals(0, cityGrid.countTrees());
        cityGrid.getCell(2, 2).setTerrain(TerrainType.TREE);
        assertEquals(1, cityGrid.countTrees());
        cityGrid.getCell(2, 3).setTerrain(TerrainType.TREE);
        assertEquals(2, cityGrid.countTrees());
        cityGrid.getCell(2, 3).setOccupied(true);
        assertEquals(1, cityGrid.countTrees());
    }

    @Test
    void testCountRocks() {
        assertEquals(0, cityGrid.countRocks());
        cityGrid.getCell(2, 2).setTerrain(TerrainType.ROCK);
        assertEquals(-1, cityGrid.countRocks());
        cityGrid.getCell(2, 3).setTerrain(TerrainType.ROCK);
        assertEquals(-2, cityGrid.countRocks());
        cityGrid.getCell(2, 3).setOccupied(true);
        assertEquals(-1, cityGrid.countRocks());
    }

    @Test
    void testCountEmptyMeadows() {
        CityGrid grid = new CityGrid(false);
        assertEquals(-121, grid.countEmptyMeadows());

        grid.getCell(0, 0).setOccupied(true);
        grid.getCell(1, 1).setOccupied(true);
        assertEquals(-119, grid.countEmptyMeadows());

        grid.getCell(2, 2).setTerrain(TerrainType.TREE);
        grid.getCell(3, 3).setTerrain(TerrainType.ROCK);
        assertEquals(-117, grid.countEmptyMeadows());
    }

    @Test
    void testScoreCalculationWithRealBoard() {
        CityGrid grid = new CityGrid(true);

        int trees = grid.countTrees();
        int rocks = grid.countRocks();
        int emptyMeadows = grid.countEmptyMeadows();

        assertEquals(5, trees, "Should have 5 visible trees (+5 points)");
        assertEquals(-8, rocks, "Should have 8 visible rocks (-8 points)");

        int baseScore = 10 + trees + rocks + emptyMeadows;
        int scoreWith24Skips = baseScore - 24;
        assertTrue(scoreWith24Skips < 0, "Score with all skips should be negative before clamping");
    }

    @Test
    void testAddSticker() {
        Position pos = new Position(3, 3);
        Sticker sticker = new Sticker(StickerType.ADD_TREE, pos, "Add tree");

        assertNotEquals(TerrainType.TREE, cityGrid.getCell(3, 3).getTerrain());

        cityGrid.addSticker(sticker);

        assertEquals(1, cityGrid.getStickers().size());
        assertEquals(sticker, cityGrid.getStickers().get(0));
        assertEquals(TerrainType.TREE, cityGrid.getCell(3, 3).getTerrain());
        assertTrue(cityGrid.getCell(3, 3).getStickers().contains(sticker));
    }
}
