package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BuildingCardTest {

    @Test
    void testConstructorAndGetTile() {
        boolean[][] shape = {{true, false}, {false, true}};
        Tile tile = new Tile(1, shape);
        BuildingCard buildingCard = new BuildingCard(tile);
        assertEquals(tile, buildingCard.getTile());
    }
}
