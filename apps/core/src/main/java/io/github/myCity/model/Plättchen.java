package io.github.myCity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert ein Gebäudeteil (Plättchen), das auf dem Stadtplan platziert
 * werden kann.
 */
public class Plättchen {
    private final int id;
    private boolean[][] form; // true = belegt, false = leer
    private int rotation; // 0, 90, 180, 270 Grad
    private BuildingColor color;

    public Plättchen(int id, boolean[][] form) {
        this.id = id;
        this.form = form;
        this.rotation = 0;
        this.color = BuildingColor.NONE;
    }

    public Plättchen(int id, boolean[][] form, BuildingColor color) {
        this.id = id;
        this.form = form;
        this.rotation = 0;
        this.color = color;
    }

    /**
     * Dreht das Plättchen um 90 Grad im Uhrzeigersinn
     */
    public void drehen() {
        if (form == null || form.length == 0 || (form.length > 0 && form[0].length == 0)) {
            // Cannot rotate an empty or invalid form, so don't change rotation
            return;
        }
        rotation = (rotation + 90) % 360;
        form = rotateMatrix(form);
    }

    /**
     * Rotiert eine Matrix um 90 Grad im Uhrzeigersinn
     */
    private boolean[][] rotateMatrix(boolean[][] matrix) {
        if (matrix == null || matrix.length == 0) { // Check for null or empty matrix (no rows)
            return matrix;
        }
        if (matrix[0].length == 0) { // Check for empty rows (no columns)
            return matrix;
        }

        int n = matrix.length;
        int m = matrix[0].length;
        boolean[][] rotated = new boolean[m][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                rotated[j][n - 1 - i] = matrix[i][j];
            }
        }
        return rotated;
    }

    public int getId() {
        return id;
    }

    public boolean[][] getForm() {
        return form;
    }

    public BuildingColor getColor() {
        return color;
    }

    public void setColor(BuildingColor color) {
        this.color = color;
    }

    public int getRotation() {
        return rotation;
    }

    public int getBreite() {
        return form[0].length;
    }

    public int getHöhe() {
        return form.length;
    }

    // ========== FACTORY METHODS ==========

    /**
     * Erstellt die Basis-Plättchen für ein Kapitel (ohne Farbe)
     */
    public static List<Plättchen> createBasisPlättchen() {
        return createBasisPlättchen(BuildingColor.NONE);
    }

    /**
     * Erstellt die Basis-Plättchen mit einer bestimmten Farbe
     */
    public static List<Plättchen> createBasisPlättchen(BuildingColor color) {
        List<Plättchen> pieces = new ArrayList<>();

        pieces.add(new Plättchen(1, new boolean[][] { { true, true } }, color));
        pieces.add(new Plättchen(2, new boolean[][] { { true, true, true, true } }, color));
        pieces.add(new Plättchen(3, new boolean[][] { { false, true, false }, { true, true, true } }, color));
        pieces.add(new Plättchen(4, new boolean[][] { { true, false }, { true, true } }, color));
        pieces.add(new Plättchen(5, new boolean[][] { { true, false, false }, { true, true, true } }, color));
        pieces.add(new Plättchen(6, new boolean[][] { { true, true }, { true, true } }, color));
        pieces.add(new Plättchen(7, new boolean[][] { { true, true, false }, { false, true, true } }, color));
        pieces.add(new Plättchen(8, new boolean[][] { { true, false, true }, { true, true, true } }, color));

        return pieces;
    }

    /**
     * Erstellt genau 24 Plättchen für ein komplettes Kapitel.
     * 8 blaue, 8 gelbe und 8 rote Plättchen.
     */
    public static List<Plättchen> create24Plättchen() {
        List<Plättchen> plättchen = new ArrayList<>();

        // 8 blaue Plättchen
        plättchen.addAll(createBasisPlättchen(BuildingColor.BLUE));

        // 8 gelbe Plättchen
        plättchen.addAll(createBasisPlättchen(BuildingColor.YELLOW));

        // 8 rote Plättchen
        plättchen.addAll(createBasisPlättchen(BuildingColor.RED));

        return plättchen;
    }
}