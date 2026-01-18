package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TileDeckTest {

    private List<BuildingCard> cardList;
    private TileDeck deck;

    @BeforeEach
    void setUp() {
        cardList = new ArrayList<>();
        boolean[][] shape = {{true}};
        for (int i = 0; i < 5; i++) {
            Tile tile = new Tile(i, shape);
            cardList.add(new BuildingCard(tile));
        }
        deck = new TileDeck(cardList);
    }

    @Test
    void testConstructor() {
        assertEquals(5, deck.remainingCards());
    }

    @Test
    void testDrawCard() {
        assertEquals(5, deck.remainingCards());
        BuildingCard card1 = deck.drawCard();
        assertNotNull(card1);
        assertEquals(4, deck.remainingCards());
        assertTrue(cardList.contains(card1));
    }

    @Test
    void testDrawAllCards() {
        List<BuildingCard> drawnCards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            drawnCards.add(deck.drawCard());
        }

        assertEquals(0, deck.remainingCards());
        assertEquals(5, drawnCards.size());

        // Deck is empty, but discard pile is full. Should reshuffle.
        BuildingCard nextCard = deck.drawCard();
        assertNotNull(nextCard);
        assertEquals(4, deck.remainingCards());
    }

    @Test
    void testEmptyDeck() {
        TileDeck emptyDeck = new TileDeck(new ArrayList<>());
        assertEquals(0, emptyDeck.remainingCards());
        assertNull(emptyDeck.drawCard());
    }
}
