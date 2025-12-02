package io.github.myCity.model;

/**
 * Repräsentiert ein Gebäudeteil (Plättchen), das auf dem Stadtplan platziert wird.
 */
public class Plättchen {

    private final int id;
    private boolean[][] form;
    private int rotation; // 0, 1, 2, 3 für 0, 90, 180, 270 Grad

    public Plättchen(int id, boolean[][] form) {
        this.id = id;
        this.form = form;
        this.rotation = 0;
    }

    public int getId() {
        return id;
    }

    public boolean[][] getForm() {
        return form;
    }

    public int getRotation() {
        return rotation;
    }

    /**
     * Dreht das Plättchen um 90 Grad im Uhrzeigersinn.
     */
    public void drehen() {
        if (form == null || form.length == 0) {
            return;
        }
        int rows = form.length;
        int cols = form[0].length;
        boolean[][] neueForm = new boolean[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                neueForm[j][rows - 1 - i] = form[i][j];
            }
        }
        this.form = neueForm;
        this.rotation = (this.rotation + 1) % 4;
    }
}
