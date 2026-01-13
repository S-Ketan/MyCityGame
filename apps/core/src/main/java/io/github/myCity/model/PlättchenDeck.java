package io.github.myCity.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repräsentiert den Stapel der Gebäudekarten für ein Kapitel.
 */
public class PlättchenDeck {

    private final List<Gebäudekarte> karten;
    private final List<Gebäudekarte> ablagestapel;
    private final List<Gebäudekarte> originalOrder;

    public PlättchenDeck(List<Gebäudekarte> karten) {
        this.karten = new ArrayList<>(karten);
        this.ablagestapel = new ArrayList<>();
        this.originalOrder = new ArrayList<>();
        mischen();
        // Save the shuffled order so we can restore it for player 2
        this.originalOrder.addAll(this.karten);
    }

    /**
     * Mischt den Kartenstapel.
     */
    public void mischen() {
        Collections.shuffle(karten);
    }

    /**
     * Zieht die oberste Karte vom Stapel.
     * Wenn der Stapel leer ist, wird der Ablagestapel gemischt und zum neuen Ziehstapel.
     *
     * @return Die gezogene Gebäudekarte oder null, wenn keine Karten mehr vorhanden sind.
     */
    public Gebäudekarte zieheKarte() {
        if (karten.isEmpty()) {
            if (ablagestapel.isEmpty()) {
                return null; // Es gibt keine Karten mehr.
            }
            karten.addAll(ablagestapel);
            ablagestapel.clear();
            mischen();
        }
        if (!karten.isEmpty()) {
            Gebäudekarte gezogeneKarte = karten.remove(karten.size() - 1);
            ablagestapel.add(gezogeneKarte);
            return gezogeneKarte;
        }
        return null;
    }

    public int verbleibendeKarten() {
        return karten.size();
    }

    /**
     * Resets the deck to the original shuffled order.
     * Used when switching to player 2 so they get the same tile sequence.
     */
    public void reset() {
        karten.clear();
        ablagestapel.clear();
        karten.addAll(originalOrder);
    }
}
