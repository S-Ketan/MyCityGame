package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpielerstandTest {

    @Test
    void testConstructorAndGetters() {
        Stadtplan stadtplan = new Stadtplan();
        int punktzahl = 100;

        Spielerstand spielerstand = new Spielerstand(stadtplan, punktzahl);

        assertEquals(stadtplan, spielerstand.getStadtplan());
        assertEquals(punktzahl, spielerstand.getPunktzahl());
    }
}
