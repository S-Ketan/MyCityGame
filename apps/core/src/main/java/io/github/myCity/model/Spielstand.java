package io.github.myCity.model;

/**
 * Repräsentiert den gesamten Spielstand, der gespeichert und geladen werden kann.
 */
public class Spielstand {

    private final int kapitelNummer;
    private final Spielerstand spieler1;
    private final Spielerstand spieler2;

    public Spielstand(int kapitelNummer, Spielerstand spieler1, Spielerstand spieler2) {
        this.kapitelNummer = kapitelNummer;
        this.spieler1 = spieler1;
        this.spieler2 = spieler2;
    }

    public int getKapitelNummer() {
        return kapitelNummer;
    }

    public Spielerstand getSpieler1() {
        return spieler1;
    }

    public Spielerstand getSpieler2() {
        return spieler2;
    }
}
