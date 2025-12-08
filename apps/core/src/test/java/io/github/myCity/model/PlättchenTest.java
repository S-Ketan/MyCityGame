package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlättchenTest {

    @Test
    void testConstructorAndGetters() {
        boolean[][] form = {{true, true}, {false, true}};
        Plättchen plaettchen = new Plättchen(1, form);

        assertEquals(1, plaettchen.getId());
        assertArrayEquals(form, plaettchen.getForm());
        assertEquals(0, plaettchen.getRotation());
    }

    @Test
    void testDrehen() {
        boolean[][] initialForm = {
            {true, false},
            {true, true}
        };
        Plättchen plaettchen = new Plättchen(1, initialForm);

        // 1. Rotation (90 degrees)
        plaettchen.drehen();
        boolean[][] form90 = {
            {true, true},
            {true, false}
        };
        assertEquals(1, plaettchen.getRotation());
        assertArrayEquals(form90, plaettchen.getForm());

        // 2. Rotation (180 degrees)
        plaettchen.drehen();
        boolean[][] form180 = {
            {true, true},
            {false, true}
        };
        assertEquals(2, plaettchen.getRotation());
        assertArrayEquals(form180, plaettchen.getForm());

        // 3. Rotation (270 degrees)
        plaettchen.drehen();
        boolean[][] form270 = {
            {false, true},
            {true, true}
        };
        assertEquals(3, plaettchen.getRotation());
        assertArrayEquals(form270, plaettchen.getForm());

        // 4. Rotation (360 degrees -> back to initial)
        plaettchen.drehen();
        assertEquals(0, plaettchen.getRotation());
        assertArrayEquals(initialForm, plaettchen.getForm());
    }

    @Test
    void testDrehenEmpty() {
        boolean[][] form = {};
        Plättchen plaettchen = new Plättchen(1, form);
        assertDoesNotThrow(plaettchen::drehen);
    }
}
