package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TerrainTypeTest {

    @Test
    void testEnumValues() {
        assertNotNull(TerrainType.valueOf("MEADOW"));
        assertNotNull(TerrainType.valueOf("TREE"));
        assertNotNull(TerrainType.valueOf("ROCK"));
        assertNotNull(TerrainType.valueOf("MOUNTAIN"));
        assertNotNull(TerrainType.valueOf("FOREST"));
    }

    @Test
    void testEnumLength() {
        assertEquals(5, TerrainType.values().length);
    }
}
