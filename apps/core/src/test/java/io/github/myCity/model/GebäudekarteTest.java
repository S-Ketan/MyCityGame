package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GebäudekarteTest {

    @Test
    void testConstructorAndGetPlättchen() {
        boolean[][] form = {{true, false}, {false, true}};
        Plättchen plaettchen = new Plättchen(1, form);
        Gebäudekarte gebaeudekarte = new Gebäudekarte(plaettchen);
        assertEquals(plaettchen, gebaeudekarte.getPlättchen());
    }
}
