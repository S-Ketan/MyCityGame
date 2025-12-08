package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpielstandTest {

    @Test
    void testConstructorAndGetters() {
        Stadtplan stadtplan1 = new Stadtplan();
        Spielerstand spieler1 = new Spielerstand(stadtplan1, 50);

        Stadtplan stadtplan2 = new Stadtplan();
        Spielerstand spieler2 = new Spielerstand(stadtplan2, 75);

        int kapitelNummer = 3;

        Spielstand spielstand = new Spielstand(kapitelNummer, spieler1, spieler2);

        assertEquals(kapitelNummer, spielstand.getKapitelNummer());
        assertEquals(spieler1, spielstand.getSpieler1());
        assertEquals(spieler2, spielstand.getSpieler2());
    }
}
