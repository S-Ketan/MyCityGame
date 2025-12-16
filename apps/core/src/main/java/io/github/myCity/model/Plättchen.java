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
        rotation = (rotation + 90) % 360;
        form = rotateMatrix(form);
    }

    /**
     * Rotiert eine Matrix um 90 Grad im Uhrzeigersinn
     */
    private boolean[][] rotateMatrix(boolean[][] matrix) {
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
     * Erstellt die Basis-Plättchen für ein Kapitel
     */
    public static List<Plättchen> createBasiPlättchens() {
        List<Plättchen> pieces = new ArrayList<>();

        pieces.add(new Plättchen(1, new boolean[][] { { true, true } }));
        pieces.add(new Plättchen(2, new boolean[][] { { true, true, true, true } }));
        pieces.add(new Plättchen(3, new boolean[][] { { false, true, false }, { true, true, true } }));
        pieces.add(new Plättchen(4, new boolean[][] { { true, false }, { true, true } }));
        pieces.add(new Plättchen(5, new boolean[][] { { true, false, false }, { true, true, true } }));
        pieces.add(new Plättchen(6, new boolean[][] { { true, true }, { true, true } }));
        pieces.add(new Plättchen(7, new boolean[][] { { true, true, false }, { false, true, true } }));
        pieces.add(new Plättchen(8, new boolean[][] { {true, false, true}, {true, true, true}}));

        return pieces;
    }

    /**
     * Erstellt genau 24 Plättchen für ein komplettes Kapitel
     */
    public static List<Plättchen> create24Plättchen() {
        List<Plättchen> plättchen = new ArrayList<>();
        List<Plättchen> basis = createBasiPlättchens();

        int[] anzahl = { 2, 2, 2, 2, 2, 2, 2, 2 }; // Gesamt: 24

        for (int i = 0; i < basis.size(); i++) {
            Plättchen original = basis.get(i);
            for (int j = 0; j < anzahl[i]; j++) {
                boolean[][] originalForm = original.getForm();
                boolean[][] neueForm = new boolean[originalForm.length][];
                for (int k = 0; k < originalForm.length; k++) {
                    neueForm[k] = originalForm[k].clone();
                }
                plättchen.add(new Plättchen(original.getId(), neueForm));
            }
        }

        return plättchen;
    }
}