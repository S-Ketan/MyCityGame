package io.github.myCity.model;

/**
 * Speichert den Zustand eines einzelnen Spielers zu Speicher- und Ladezwecken.
 */
public class Spielerstand {

    private final Stadtplan stadtplan;
    private final int punktzahl;

    public Spielerstand(Stadtplan stadtplan, int punktzahl) {
        this.stadtplan = stadtplan;
        this.punktzahl = punktzahl;
    }

    public Stadtplan getStadtplan() {
        return stadtplan;
    }

    public int getPunktzahl() {
        return punktzahl;
    }
}
