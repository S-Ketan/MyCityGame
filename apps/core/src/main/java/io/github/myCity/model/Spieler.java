package io.github.myCity.model;

public class Spieler {
    private final String name;
    private int punktzahl;
    private final Stadtplan stadtplan;

    public Spieler(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        this.name = name;
        this.punktzahl = 0;
        this.stadtplan = new Stadtplan();
    }

    public String getName() {
        return name;
    }

    public int getPunktzahl() {
        return punktzahl;
    }

    public void setPunktzahl(int punktzahl) {
        // Player's score can never be negative
        this.punktzahl = Math.max(0, punktzahl);
    }

    public Stadtplan getStadtplan() {
        return stadtplan;
    }

    public boolean kannLegen(Plättchen plaettchen, Position pos) {

        if (plaettchen == null || pos == null) {
            return false;
        }
        boolean[][] form = plaettchen.getForm();
        for (int i = 0; i < form.length; i++) {
            for (int j = 0; j < form[i].length; j++) {
                if (form[i][j]) {
                    int weltX = pos.getX() + j;
                    int weltY = pos.getY() + i;

                    // Check bounds
                    if (weltX < 0 || weltX >= 11 || weltY < 0 || weltY >= 11) {
                        return false;
                    }

                    // Check if field is already occupied
                    Feld feld = stadtplan.getFeld(weltX, weltY);
                    if (feld != null && feld.istBelegt()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void plaettchenLegen(Plättchen plättchen, Position pos) {
        stadtplan.plättchenPlatzieren(plättchen, pos);
    }

    public void plaettchenDrehen(Plättchen plättchen, Position pos) {
        if (plättchen == null || pos == null) {
            return;
        }

        // Rotate the tile
        plättchen.drehen();

        // Then place the rotated tile
        stadtplan.plättchenPlatzieren(plättchen, pos);
    }

    public void plaettchenUeberspringen() {
        // This might involve:
        // 1. Applying penalty points (subtract from punktzahl)
        // 2. Notifying the game controller
        // 3. Moving to next player's turn
        // The skip action is recorded but penalty is applied by the game controller
        // based on chapter-specific rules
        System.out.println(name + " hat das Plättchen übersprungen.");
    }

    @Override
    public String toString() {
        return "Spieler{" +
                "name='" + name + '\'' +
                ", punktzahl=" + punktzahl +
                '}';
    }
}