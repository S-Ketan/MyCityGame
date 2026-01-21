package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer");
    }

    @Test
    void testConstructorWithValidName() {
        assertEquals("TestPlayer", player.getName());
        assertEquals(10, player.getScore());
        assertNotNull(player.getCityGrid());
        assertEquals(0, player.getSkippedBuildings());
        assertEquals(0, player.getProgressPoints());
    }

    @Test
    void testConstructorWithNullNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Player(null));
    }

    @Test
    void testConstructorWithEmptyNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Player(""));
    }

    @Test
    void testConstructorWithWhitespaceNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Player("   "));
    }

    @Test
    void testGetName() {
        assertEquals("TestPlayer", player.getName());
    }

    @Test
    void testGetScore() {
        assertEquals(10, player.getScore());
    }

    @Test
    void testSetScoreWithinBounds() {
        player.setScore(25);
        assertEquals(25, player.getScore());
    }

    @Test
    void testSetScoreBelowMinimum() {
        player.setScore(-10);
        assertEquals(0, player.getScore());
    }

    @Test
    void testSetScoreAboveMaximum() {
        player.setScore(100);
        assertEquals(50, player.getScore());
    }

    @Test
    void testSetScoreAtMinimum() {
        player.setScore(0);
        assertEquals(0, player.getScore());
    }

    @Test
    void testSetScoreAtMaximum() {
        player.setScore(50);
        assertEquals(50, player.getScore());
    }

    @Test
    void testGetCityGrid() {
        assertNotNull(player.getCityGrid());
        assertInstanceOf(CityGrid.class, player.getCityGrid());
    }

    @Test
    void testCanPlaceTileWithNullTile() {
        Position position = new Position(5, 5);
        assertFalse(player.canPlaceTile(null, position));
    }

    @Test
    void testCanPlaceTileWithNullPosition() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        assertFalse(player.canPlaceTile(tile, null));
    }

    @Test
    void testCanPlaceTileOutOfBoundsNegativeX() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(-1, 5);
        assertFalse(player.canPlaceTile(tile, position));
    }

    @Test
    void testCanPlaceTileOutOfBoundsNegativeY() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(5, -1);
        assertFalse(player.canPlaceTile(tile, position));
    }

    @Test
    void testCanPlaceTileOutOfBoundsTooLargeX() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(11, 5);
        assertFalse(player.canPlaceTile(tile, position));
    }

    @Test
    void testCanPlaceTileOutOfBoundsTooLargeY() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(5, 11);
        assertFalse(player.canPlaceTile(tile, position));
    }

    @Test
    void testCanPlaceTileValidPosition() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(5, 5);
        assertTrue(player.canPlaceTile(tile, position));
    }

    @Test
    void testCanPlaceTileOnOccupiedCell() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);
        Position position = new Position(5, 5);

        player.placeTile(tile1, position);
        assertFalse(player.canPlaceTile(tile2, position));
    }

    @Test
    void testPlaceTile() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(5, 5);

        player.placeTile(tile, position);

        Cell cell = player.getCityGrid().getCell(5, 5);
        assertTrue(cell.isOccupied());
    }

    @Test
    void testRotateTileAndPlaceWithNullTile() {
        Position position = new Position(5, 5);
        player.rotateTileAndPlace(null, position);
        // Should not throw, just return
    }

    @Test
    void testRotateTileAndPlaceWithNullPosition() {
        boolean[][] shape = {{true, false}, {true, true}};
        Tile tile = new Tile(1, shape);
        player.rotateTileAndPlace(tile, null);
        // Should not throw, just return
    }

    @Test
    void testRotateTileAndPlace() {
        boolean[][] shape = {{true, false}, {true, true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(5, 5);

        player.rotateTileAndPlace(tile, position);

        assertEquals(90, tile.getRotation());
    }

    @Test
    void testSkipTile() {
        assertEquals(0, player.getSkippedBuildings());
        player.skipTile();
        assertEquals(1, player.getSkippedBuildings());
        player.skipTile();
        assertEquals(2, player.getSkippedBuildings());
    }

    @Test
    void testGetSkippedBuildings() {
        assertEquals(0, player.getSkippedBuildings());
    }

    @Test
    void testResetSkippedBuildings() {
        player.skipTile();
        player.skipTile();
        assertEquals(2, player.getSkippedBuildings());
        player.resetSkippedBuildings();
        assertEquals(0, player.getSkippedBuildings());
    }

    @Test
    void testGetProgressPoints() {
        assertEquals(0, player.getProgressPoints());
    }

    @Test
    void testSetProgressPointsWithinBounds() {
        player.setProgressPoints(50);
        assertEquals(50, player.getProgressPoints());
    }

    @Test
    void testSetProgressPointsBelowMinimum() {
        player.setProgressPoints(-10);
        assertEquals(0, player.getProgressPoints());
    }

    @Test
    void testSetProgressPointsAboveMaximum() {
        player.setProgressPoints(150);
        assertEquals(100, player.getProgressPoints());
    }

    @Test
    void testAddProgressPoints() {
        player.addProgressPoints(10);
        assertEquals(10, player.getProgressPoints());
        player.addProgressPoints(20);
        assertEquals(30, player.getProgressPoints());
    }

    @Test
    void testAddProgressPointsDoesNotExceedMaximum() {
        player.setProgressPoints(90);
        player.addProgressPoints(20);
        assertEquals(100, player.getProgressPoints());
    }

    @Test
    void testHasWonGameFalse() {
        player.setProgressPoints(50);
        assertFalse(player.hasWonGame());
    }

    @Test
    void testHasWonGameTrueAtExactly100() {
        player.setProgressPoints(100);
        assertTrue(player.hasWonGame());
    }

    @Test
    void testHasWonGameTrueAbove100() {
        player.setProgressPoints(150);
        assertTrue(player.hasWonGame());
    }

    @Test
    void testToString() {
        String result = player.toString();
        assertTrue(result.contains("TestPlayer"));
        assertTrue(result.contains("10"));
        assertTrue(result.contains("Player{"));
    }
}
