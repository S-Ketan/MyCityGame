package io.github.myCity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a building tile that can be placed on the city grid.
 */
public class Tile {
    private final int id;
    private boolean[][] shape;
    private int rotation;
    private BuildingColor color;

    public Tile(int id, boolean[][] shape) {
        this.id = id;
        this.shape = shape;
        this.rotation = 0;
        this.color = BuildingColor.NONE;
    }

    public Tile(int id, boolean[][] shape, BuildingColor color) {
        this.id = id;
        this.shape = shape;
        this.rotation = 0;
        this.color = color;
    }

    /**
     * Rotates the tile 90 degrees clockwise.
     */
    public void rotate() {
        if (shape == null || shape.length == 0 || (shape.length > 0 && shape[0].length == 0)) {
            return;
        }
        rotation = (rotation + 90) % 360;
        shape = rotateMatrix(shape);
    }

    private boolean[][] rotateMatrix(boolean[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return matrix;
        }
        if (matrix[0].length == 0) {
            return matrix;
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] rotated = new boolean[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = matrix[i][j];
            }
        }
        return rotated;
    }

    public int getId() {
        return id;
    }

    public boolean[][] getShape() {
        return shape;
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

    public int getWidth() {
        return shape[0].length;
    }

    public int getHeight() {
        return shape.length;
    }

    /**
     * Creates the base tiles for an episode (without color).
     */
    public static List<Tile> createBaseTiles() {
        return createBaseTiles(BuildingColor.NONE);
    }

    /**
     * Creates the base tiles with a specific color.
     */
    public static List<Tile> createBaseTiles(BuildingColor color) {
        List<Tile> tiles = new ArrayList<>();

        tiles.add(new Tile(1, new boolean[][] { { true, true } }, color));
        tiles.add(new Tile(2, new boolean[][] { { true, true, true, true } }, color));
        tiles.add(new Tile(3, new boolean[][] { { false, true, false }, { true, true, true } }, color));
        tiles.add(new Tile(4, new boolean[][] { { true, false }, { true, true } }, color));
        tiles.add(new Tile(5, new boolean[][] { { true, false, false }, { true, true, true } }, color));
        tiles.add(new Tile(6, new boolean[][] { { true, true }, { true, true } }, color));
        tiles.add(new Tile(7, new boolean[][] { { true, true, false }, { false, true, true } }, color));
        tiles.add(new Tile(8, new boolean[][] { { true, false, true }, { true, true, true } }, color));

        return tiles;
    }

    /**
     * Creates exactly 24 tiles for a complete episode.
     * 8 blue, 8 yellow, and 8 red tiles.
     */
    public static List<Tile> createFullTileSet() {
        List<Tile> tiles = new ArrayList<>();

        tiles.addAll(createBaseTiles(BuildingColor.BLUE));
        tiles.addAll(createBaseTiles(BuildingColor.YELLOW));
        tiles.addAll(createBaseTiles(BuildingColor.RED));

        return tiles;
    }
}
