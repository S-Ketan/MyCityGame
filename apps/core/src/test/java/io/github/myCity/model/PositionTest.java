package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testConstructorAndGetters() {
        Position position = new Position(1, 2);
        assertEquals(1, position.getX());
        assertEquals(2, position.getY());
    }

    @Test
    void testSetters() {
        Position position = new Position(0, 0);
        position.setX(5);
        position.setY(10);
        assertEquals(5, position.getX());
        assertEquals(10, position.getY());
    }

    @Test
    void testEquals() {
        Position position1 = new Position(1, 2);
        Position position2 = new Position(1, 2);
        Position position3 = new Position(2, 3);
        Position position4 = new Position(1, 3);

        assertEquals(position1, position2);
        assertNotEquals(position1, position3);
        assertNotEquals(position1, position4);
        assertNotEquals(position1, null);
        assertNotEquals(position1, new Object());
    }

    @Test
    void testHashCode() {
        Position position1 = new Position(1, 2);
        Position position2 = new Position(1, 2);
        assertEquals(position1.hashCode(), position2.hashCode());

        Position position3 = new Position(2, 3);
        assertNotEquals(position1.hashCode(), position3.hashCode());
    }
}
