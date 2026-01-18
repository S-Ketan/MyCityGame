package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void testConstructorAndGetters() {
        boolean[][] shape = {{true, true}, {false, true}};
        Tile tile = new Tile(1, shape);

        assertEquals(1, tile.getId());
        assertArrayEquals(shape, tile.getShape());
        assertEquals(0, tile.getRotation());
    }

    @Test
    void testRotate() {
        boolean[][] initialShape = {
            {true, false},
            {true, true}
        };
        Tile tile = new Tile(1, initialShape);

        // 1st rotation (90 degrees)
        tile.rotate();
        boolean[][] shape90 = {
            {true, true},
            {true, false}
        };
        assertEquals(90, tile.getRotation());
        assertArrayEquals(shape90, tile.getShape());

        // 2nd rotation (180 degrees)
        tile.rotate();
        boolean[][] shape180 = {
            {true, true},
            {false, true}
        };
        assertEquals(180, tile.getRotation());
        assertArrayEquals(shape180, tile.getShape());

        // 3rd rotation (270 degrees)
        tile.rotate();
        boolean[][] shape270 = {
            {false, true},
            {true, true}
        };
        assertEquals(270, tile.getRotation());
        assertArrayEquals(shape270, tile.getShape());

        // 4th rotation (360 degrees -> back to initial)
        tile.rotate();
        assertEquals(0, tile.getRotation());
        assertArrayEquals(initialShape, tile.getShape());
    }

    @Test
    void testRotateEmpty() {
        boolean[][] shape = {};
        Tile tile = new Tile(1, shape);
        tile.rotate();
        assertArrayEquals(shape, tile.getShape());
        assertEquals(0, tile.getRotation());
    }
}
