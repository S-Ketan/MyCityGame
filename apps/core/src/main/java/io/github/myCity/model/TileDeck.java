package io.github.myCity.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the deck of building cards for an episode.
 */
public class TileDeck {

    private final List<BuildingCard> cards;
    private final List<BuildingCard> discardPile;
    private final List<BuildingCard> originalOrder;

    public TileDeck(List<BuildingCard> cards) {
        this.cards = new ArrayList<>(cards);
        this.discardPile = new ArrayList<>();
        this.originalOrder = new ArrayList<>();
        shuffle();
        this.originalOrder.addAll(this.cards);
    }

    /**
     * Shuffles the card deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws the top card from the deck.
     * If the deck is empty, the discard pile is shuffled and becomes the new draw pile.
     *
     * @return the drawn BuildingCard or null if no cards are available
     */
    public BuildingCard drawCard() {
        if (cards.isEmpty()) {
            if (discardPile.isEmpty()) {
                return null;
            }
            cards.addAll(discardPile);
            discardPile.clear();
            shuffle();
        }
        if (!cards.isEmpty()) {
            BuildingCard drawnCard = cards.remove(cards.size() - 1);
            discardPile.add(drawnCard);
            return drawnCard;
        }
        return null;
    }

    public int remainingCards() {
        return cards.size();
    }

    /**
     * Resets the deck to the original shuffled order.
     * Used when switching to player 2 so they get the same tile sequence.
     */
    public void reset() {
        cards.clear();
        discardPile.clear();
        cards.addAll(originalOrder);
    }
}
