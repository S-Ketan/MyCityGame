package io.github.myCity.model;

/**
 * Represents a player in the game.
 */
public class Player {
    private static final int START_SCORE = 10;
    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 50;
    private static final int MAX_PROGRESS_POINTS = 100;

    private final String name;
    private int score;
    private final CityGrid cityGrid;
    private int skippedBuildings;
    private int progressPoints;

    public Player(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        this.name = name;
        this.score = START_SCORE;
        this.cityGrid = new CityGrid();
        this.skippedBuildings = 0;
        this.progressPoints = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }

    public CityGrid getCityGrid() {
        return cityGrid;
    }

    public boolean canPlaceTile(Tile tile, Position position) {
        if (tile == null || position == null) {
            return false;
        }
        boolean[][] shape = tile.getShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j]) {
                    int worldX = position.getX() + j;
                    int worldY = position.getY() + i;

                    if (worldX < 0 || worldX >= 11 || worldY < 0 || worldY >= 11) {
                        return false;
                    }

                    Cell cell = cityGrid.getCell(worldX, worldY);
                    if (cell != null && cell.isOccupied()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void placeTile(Tile tile, Position position) {
        cityGrid.placeTile(tile, position);
    }

    public void rotateTileAndPlace(Tile tile, Position position) {
        if (tile == null || position == null) {
            return;
        }
        tile.rotate();
        cityGrid.placeTile(tile, position);
    }

    public void skipTile() {
        skippedBuildings++;
    }

    public int getSkippedBuildings() {
        return skippedBuildings;
    }

    public void resetSkippedBuildings() {
        this.skippedBuildings = 0;
    }

    public int getProgressPoints() {
        return progressPoints;
    }

    public void setProgressPoints(int progressPoints) {
        this.progressPoints = Math.max(0, Math.min(MAX_PROGRESS_POINTS, progressPoints));
    }

    public void addProgressPoints(int points) {
        this.progressPoints = Math.min(MAX_PROGRESS_POINTS, this.progressPoints + points);
    }

    public boolean hasWonGame() {
        return progressPoints >= MAX_PROGRESS_POINTS;
    }

    @Override
    public String toString() {
        return "Player{name='" + name + "', score=" + score + '}';
    }
}
