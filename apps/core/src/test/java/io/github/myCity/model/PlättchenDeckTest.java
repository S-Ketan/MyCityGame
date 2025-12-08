package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlättchenDeckTest {

    private List<Gebäudekarte> kartenListe;
    private PlättchenDeck deck;

    @BeforeEach
    void setUp() {
        kartenListe = new ArrayList<>();
        boolean[][] form = {{true}};
        for (int i = 0; i < 5; i++) {
            Plättchen plättchen = new Plättchen(i, form);
            kartenListe.add(new Gebäudekarte(plättchen));
        }
        deck = new PlättchenDeck(kartenListe);
    }

    @Test
    void testConstructor() {
        assertEquals(5, deck.verbleibendeKarten());
    }

    @Test
    void testZieheKarte() {
        assertEquals(5, deck.verbleibendeKarten());
        Gebäudekarte karte1 = deck.zieheKarte();
        assertNotNull(karte1);
        assertEquals(4, deck.verbleibendeKarten());
        assertTrue(kartenListe.contains(karte1));
    }

    @Test
    void testZieheAlleKarten() {
        List<Gebäudekarte> gezogeneKarten = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            gezogeneKarten.add(deck.zieheKarte());
        }

        assertEquals(0, deck.verbleibendeKarten());
        assertEquals(5, gezogeneKarten.size());

        // Deck is empty, but discard pile is full. Should reshuffle.
        Gebäudekarte nächsteKarte = deck.zieheKarte();
        assertNotNull(nächsteKarte);
        assertEquals(4, deck.verbleibendeKarten());
    }

    @Test
    void testLeeresDeck() {
        // Create an empty deck
        PlättchenDeck leeresDeck = new PlättchenDeck(new ArrayList<>());
        assertEquals(0, leeresDeck.verbleibendeKarten());
        assertNull(leeresDeck.zieheKarte());
    }
}
