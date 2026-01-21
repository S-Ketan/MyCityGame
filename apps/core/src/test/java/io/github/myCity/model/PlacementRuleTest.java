package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlacementRuleTest {

    private PlacementRule placementRule;
    private CityGrid cityGrid;

    @BeforeEach
    void setUp() {
        placementRule = new PlacementRule();
        cityGrid = new CityGrid();
    }

    @Test
    void testValidatePlacementWithNullTile() {
        Position position = new Position(5, 5);
        assertFalse(placementRule.validatePlacement(null, cityGrid, position, true));
    }

    @Test
    void testValidatePlacementWithNullCityGrid() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(5, 5);
        assertFalse(placementRule.validatePlacement(tile, null, position, true));
    }

    @Test
    void testValidatePlacementWithNullPosition() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, null, true));
    }

    @Test
    void testValidatePlacementOutOfBoundsNegativeX() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(-1, 5);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, position, true));
    }

    @Test
    void testValidatePlacementOutOfBoundsNegativeY() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(5, -1);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, position, true));
    }

    @Test
    void testValidatePlacementOutOfBoundsTooLargeX() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(11, 5);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, position, true));
    }

    @Test
    void testValidatePlacementOutOfBoundsTooLargeY() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(5, 11);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, position, true));
    }

    @Test
    void testValidatePlacementTileExceedsBounds() {
        boolean[][] shape = {{true, true, true}};
        Tile tile = new Tile(1, shape);
        Position position = new Position(9, 5);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, position, true));
    }

    @Test
    void testFirstTileMustBeAdjacentToRiver() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        // River cells are at positions like (2,0), (1,0), (2,1), (3,1), etc.
        Position positionOnRiver = new Position(2, 0);
        assertTrue(placementRule.validatePlacement(tile, cityGrid, positionOnRiver, true));
    }

    @Test
    void testFirstTileNotAdjacentToRiverFails() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position positionNotOnRiver = new Position(10, 10);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, positionNotOnRiver, true));
    }

    @Test
    void testSubsequentTileMustBeAdjacentToBuilding() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);

        Position firstPosition = new Position(2, 0);
        cityGrid.placeTile(tile1, firstPosition);

        Position adjacentPosition = new Position(3, 0);
        assertTrue(placementRule.validatePlacement(tile2, cityGrid, adjacentPosition, false));
    }

    @Test
    void testSubsequentTileNotAdjacentToBuildingFails() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);

        Position firstPosition = new Position(2, 0);
        cityGrid.placeTile(tile1, firstPosition);

        Position farPosition = new Position(10, 7);
        assertFalse(placementRule.validatePlacement(tile2, cityGrid, farPosition, false));
    }

    @Test
    void testCannotBuildOnMountain() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position mountainPosition = new Position(0, 4);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, mountainPosition, true));
    }

    @Test
    void testCannotBuildOnForest() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position forestPosition = new Position(10, 8);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, forestPosition, true));
    }

    @Test
    void testCannotPlaceOnOccupiedCell() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);

        Position position = new Position(2, 0);
        cityGrid.placeTile(tile1, position);

        assertFalse(placementRule.validatePlacement(tile2, cityGrid, position, false));
    }

    @Test
    void testCannotBuildOnRestrictedArea() {
        CityGrid gridWithRestricted = new CityGrid(false);
        Sticker restrictedSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "restricted area");
        gridWithRestricted.addSticker(restrictedSticker);

        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position restrictedPosition = new Position(5, 5);

        assertFalse(placementRule.validatePlacement(tile, gridWithRestricted, restrictedPosition, true));
    }

    @Test
    void testCannotBuildOnForbiddenArea() {
        CityGrid gridWithForbidden = new CityGrid(false);
        Sticker forbiddenSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "forbidden zone");
        gridWithForbidden.addSticker(forbiddenSticker);

        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position forbiddenPosition = new Position(5, 5);

        assertFalse(placementRule.validatePlacement(tile, gridWithForbidden, forbiddenPosition, true));
    }

    @Test
    void testValidPlacementOnMeadow() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        // Position (2, 0) is a meadow cell with river
        Position meadowPositionOnRiver = new Position(2, 0);
        assertTrue(placementRule.validatePlacement(tile, cityGrid, meadowPositionOnRiver, true));
    }

    @Test
    void testValidateWithErrorMessageNullParameters() {
        assertEquals("Invalid parameters", placementRule.validateWithErrorMessage(null, cityGrid, new Position(0, 0), true));
        assertEquals("Invalid parameters", placementRule.validateWithErrorMessage(new Tile(1, new boolean[][]{{true}}), null, new Position(0, 0), true));
        assertEquals("Invalid parameters", placementRule.validateWithErrorMessage(new Tile(1, new boolean[][]{{true}}), cityGrid, null, true));
    }

    @Test
    void testValidateWithErrorMessageOutOfBounds() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position outOfBounds = new Position(-1, 5);
        assertEquals("Tile is out of bounds or overlaps existing buildings", placementRule.validateWithErrorMessage(tile, cityGrid, outOfBounds, true));
    }

    @Test
    void testValidateWithErrorMessageFirstTileNotOnRiver() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position notOnRiver = new Position(10, 10);
        assertEquals("First tile must be adjacent to the river", placementRule.validateWithErrorMessage(tile, cityGrid, notOnRiver, true));
    }

    @Test
    void testValidateWithErrorMessageValidFirstTile() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position onRiver = new Position(2, 0);
        assertNull(placementRule.validateWithErrorMessage(tile, cityGrid, onRiver, true));
    }

    @Test
    void testValidateWithErrorMessageNotAdjacentToBuilding() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);

        Position firstPosition = new Position(2, 0);
        cityGrid.placeTile(tile1, firstPosition);

        Position farPosition = new Position(10, 7);
        assertEquals("Tile must connect to at least one existing building", placementRule.validateWithErrorMessage(tile2, cityGrid, farPosition, false));
    }

    @Test
    void testValidateWithErrorMessageCannotBuildOnMountain() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);

        // Place first tile adjacent to mountain at (0, 4)
        // Position (0, 3) is adjacent to the mountain at (0, 4)
        Position firstPosition = new Position(0, 3);
        cityGrid.placeTile(tile1, firstPosition);

        // Mountain is at position (0, 4) - grid[4][0]
        Position mountainPosition = new Position(0, 4);
        assertEquals("Cannot build on mountains or forest", placementRule.validateWithErrorMessage(tile2, cityGrid, mountainPosition, false));
    }

    @Test
    void testValidateWithErrorMessageRestrictedArea() {
        CityGrid gridWithRestricted = new CityGrid(false);

        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Position pos1 = new Position(4, 5);
        gridWithRestricted.placeTile(tile1, pos1);

        Sticker restrictedSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "restricted area");
        gridWithRestricted.addSticker(restrictedSticker);

        Tile tile2 = new Tile(2, shape);
        Position restrictedPosition = new Position(5, 5);

        assertEquals("Cannot build in restricted area", placementRule.validateWithErrorMessage(tile2, gridWithRestricted, restrictedPosition, false));
    }

    @Test
    void testIsValidMethod() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position validPosition = new Position(2, 0);

        assertTrue(placementRule.isValid(tile, cityGrid, validPosition, true));
    }

    @Test
    void testIsValidMethodReturnsFalse() {
        boolean[][] shape = {{true}};
        Tile tile = new Tile(1, shape);
        Position invalidPosition = new Position(-1, 5);

        assertFalse(placementRule.isValid(tile, cityGrid, invalidPosition, true));
    }

    @Test
    void testLargeTilePlacement() {
        // Use a grid without default terrain to avoid river crossing issues
        CityGrid emptyGrid = new CityGrid(false);
        // Add a river on the left edge of a cell (outside the tile footprint)
        emptyGrid.getCell(5, 5).riverLeft = true;

        boolean[][] shape = {
            {true, true},
            {true, false}
        };
        Tile tile = new Tile(1, shape);
        // Position (5, 5): tile occupies (5,5), (6,5), (5,6)
        // River is on left edge of (5,5) which doesn't cross between occupied cells
        Position position = new Position(5, 5);
        assertTrue(placementRule.validatePlacement(tile, emptyGrid, position, true));
    }

    @Test
    void testMultipleCellTilePartiallyOutOfBounds() {
        boolean[][] shape = {
            {true, true, true}
        };
        Tile tile = new Tile(1, shape);
        Position position = new Position(9, 2);
        assertFalse(placementRule.validatePlacement(tile, cityGrid, position, true));
    }

    @Test
    void testAdjacentTileOrthogonallyConnected() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);

        Position firstPosition = new Position(2, 0);
        cityGrid.placeTile(tile1, firstPosition);

        Position rightAdjacent = new Position(3, 0);
        assertTrue(placementRule.validatePlacement(tile2, cityGrid, rightAdjacent, false));
    }

    @Test
    void testDiagonalNotConsideredAdjacent() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);

        CityGrid emptyGrid = new CityGrid(false);
        Position firstPosition = new Position(5, 5);
        emptyGrid.placeTile(tile1, firstPosition);

        Position diagonalPosition = new Position(6, 6);
        assertFalse(placementRule.validatePlacement(tile2, emptyGrid, diagonalPosition, false));
    }

    @Test
    void testCanBuildOnTreeTerrain() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);

        Position firstPosition = new Position(2, 2);
        cityGrid.placeTile(tile1, firstPosition);

        Position treePosition = new Position(3, 2);
        assertTrue(placementRule.validatePlacement(tile2, cityGrid, treePosition, false));
    }

    @Test
    void testCanBuildOnRockTerrain() {
        boolean[][] shape = {{true}};
        Tile tile1 = new Tile(1, shape);
        Tile tile2 = new Tile(2, shape);

        Position firstPosition = new Position(0, 0);
        cityGrid.placeTile(tile1, firstPosition);

        Position rockPosition = new Position(1, 0);
        assertTrue(placementRule.validatePlacement(tile2, cityGrid, rockPosition, false));
    }
}
