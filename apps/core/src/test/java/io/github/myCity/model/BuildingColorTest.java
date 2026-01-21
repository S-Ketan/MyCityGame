package io.github.myCity.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildingColorTest {

    @Test
    void testAllEnumValuesExist() {
        BuildingColor[] colors = BuildingColor.values();
        assertEquals(10, colors.length);
    }

    @Test
    void testBasicColors() {
        assertNotNull(BuildingColor.NONE);
        assertNotNull(BuildingColor.RED);
        assertNotNull(BuildingColor.BLUE);
        assertNotNull(BuildingColor.YELLOW);
        assertNotNull(BuildingColor.GREEN);
        assertNotNull(BuildingColor.PURPLE);
        assertNotNull(BuildingColor.ORANGE);
    }

    @Test
    void testSpecialBuildings() {
        assertNotNull(BuildingColor.CHURCH);
        assertNotNull(BuildingColor.FACTORY);
        assertNotNull(BuildingColor.PALACE);
    }

    @Test
    void testValueOf() {
        assertEquals(BuildingColor.RED, BuildingColor.valueOf("RED"));
        assertEquals(BuildingColor.BLUE, BuildingColor.valueOf("BLUE"));
        assertEquals(BuildingColor.NONE, BuildingColor.valueOf("NONE"));
        assertEquals(BuildingColor.CHURCH, BuildingColor.valueOf("CHURCH"));
        assertEquals(BuildingColor.FACTORY, BuildingColor.valueOf("FACTORY"));
        assertEquals(BuildingColor.PALACE, BuildingColor.valueOf("PALACE"));
    }

    @Test
    void testInvalidValueOfThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> BuildingColor.valueOf("INVALID"));
    }

    @Test
    void testOrdinalValues() {
        assertEquals(0, BuildingColor.NONE.ordinal());
        assertEquals(1, BuildingColor.RED.ordinal());
        assertEquals(2, BuildingColor.BLUE.ordinal());
        assertEquals(3, BuildingColor.YELLOW.ordinal());
        assertEquals(4, BuildingColor.GREEN.ordinal());
        assertEquals(5, BuildingColor.PURPLE.ordinal());
        assertEquals(6, BuildingColor.ORANGE.ordinal());
        assertEquals(7, BuildingColor.CHURCH.ordinal());
        assertEquals(8, BuildingColor.FACTORY.ordinal());
        assertEquals(9, BuildingColor.PALACE.ordinal());
    }
}
