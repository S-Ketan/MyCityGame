package io.github.myCity.model;

public class Spieler {
    private static final int START_PUNKTZAHL = 10;
    private static final int MIN_PUNKTZAHL = 0;
    private static final int MAX_PUNKTZAHL = 50;
    private static final int MAX_PROGRESS_POINTS = 100;

    private final String name;
    private int punktzahl;
    private final Stadtplan stadtplan;
    private int übersprungeneGebäude;
    private int progressPoints;

    public Spieler(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        this.name = name;
        this.punktzahl = START_PUNKTZAHL;
        this.stadtplan = new Stadtplan();
        this.übersprungeneGebäude = 0;
        this.progressPoints = 0;
    }

    public String getName() {
        return name;
    }

    public int getPunktzahl() {
        return punktzahl;
    }

    public void setPunktzahl(int punktzahl) {
        // Clamp score between MIN_PUNKTZAHL (0) and MAX_PUNKTZAHL (50)
        this.punktzahl = Math.max(MIN_PUNKTZAHL, Math.min(MAX_PUNKTZAHL, punktzahl));
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
        // Track skipped tiles for score calculation (-1 point each)
        übersprungeneGebäude++;
        System.out.println(name + " hat das Plättchen übersprungen.");
    }

    public int getÜbersprungeneGebäude() {
        return übersprungeneGebäude;
    }

    /**
     * Resets skipped buildings counter. Used between episodes in Chapter 1.
     */
    public void resetÜbersprungeneGebäude() {
        this.übersprungeneGebäude = 0;
    }

    public int getProgressPoints() {
        return progressPoints;
    }

    public void setProgressPoints(int progressPoints) {
        this.progressPoints = Math.max(0, Math.min(MAX_PROGRESS_POINTS, progressPoints));
    }

    /**
     * Adds progress points (capped at 100).
     */
    public void addProgressPoints(int points) {
        this.progressPoints = Math.min(MAX_PROGRESS_POINTS, this.progressPoints + points);
    }

    /**
     * Checks if player has won the entire game (reached 100 progress points).
     */
    public boolean hasWonGame() {
        return progressPoints >= MAX_PROGRESS_POINTS;
    }

    @Override
    public String toString() {
        return "Spieler{" +
                "name='" + name + '\'' +
                ", punktzahl=" + punktzahl +
                '}';
    }
}