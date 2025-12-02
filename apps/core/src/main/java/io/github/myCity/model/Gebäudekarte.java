package io.github.myCity.model;

/**
 * Repräsentiert eine Karte aus dem Gebäudekarten-Stapel,
 * die ein bestimmtes Plättchen zum Bauen vorschreibt.
 */
public class Gebäudekarte {

    private final Plättchen plättchen;

    public Gebäudekarte(Plättchen plättchen) {
        this.plättchen = plättchen;
    }

    public Plättchen getPlättchen() {
        return plättchen;
    }
}
