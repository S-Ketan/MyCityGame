package io.github.myCity.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a player's personal city grid.
 */
public class CityGrid {

    private static final int GRID_SIZE = 11;

    private final Cell[][] grid;
    private final List<Sticker> stickers;

    public CityGrid() {
        this(true);
    }

    public CityGrid(boolean initializeDefaultTerrain) {
        this.grid = new Cell[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = new Cell(TerrainType.MEADOW);
            }
        }
        this.stickers = new ArrayList<>();
        if (initializeDefaultTerrain) {
            initializeRiver();
            initializeMountainsAndForest();
            initializeTreesAndRocks();
        }
    }

    private void initializeMountainsAndForest() {
        grid[4][0].setTerrain(TerrainType.MOUNTAIN);
        grid[5][0].setTerrain(TerrainType.MOUNTAIN);
        grid[6][0].setTerrain(TerrainType.MOUNTAIN);
        grid[6][1].setTerrain(TerrainType.MOUNTAIN);
        grid[7][0].setTerrain(TerrainType.MOUNTAIN);
        grid[8][0].setTerrain(TerrainType.MOUNTAIN);

        grid[8][10].setTerrain(TerrainType.FOREST);
        grid[9][10].setTerrain(TerrainType.FOREST);
        grid[0][10].setTerrain(TerrainType.FOREST);
        grid[1][10].setTerrain(TerrainType.FOREST);
        grid[2][10].setTerrain(TerrainType.FOREST);
        grid[3][10].setTerrain(TerrainType.FOREST);
        grid[4][10].setTerrain(TerrainType.FOREST);
        grid[5][10].setTerrain(TerrainType.FOREST);
        grid[6][10].setTerrain(TerrainType.FOREST);
        grid[0][9].setTerrain(TerrainType.FOREST);
        grid[1][9].setTerrain(TerrainType.FOREST);
        grid[2][9].setTerrain(TerrainType.FOREST);
        grid[3][9].setTerrain(TerrainType.FOREST);
        grid[4][9].setTerrain(TerrainType.FOREST);
        grid[5][9].setTerrain(TerrainType.FOREST);
        grid[6][9].setTerrain(TerrainType.FOREST);
        grid[0][8].setTerrain(TerrainType.FOREST);
        grid[1][8].setTerrain(TerrainType.FOREST);
        grid[4][8].setTerrain(TerrainType.FOREST);
        grid[0][7].setTerrain(TerrainType.FOREST);
    }

    private void initializeTreesAndRocks() {
        grid[3][2].setTerrain(TerrainType.TREE);
        grid[10][2].setTerrain(TerrainType.TREE);
        grid[2][6].setTerrain(TerrainType.TREE);
        grid[7][6].setTerrain(TerrainType.TREE);
        grid[9][8].setTerrain(TerrainType.TREE);

        grid[1][0].setTerrain(TerrainType.ROCK);
        grid[5][1].setTerrain(TerrainType.ROCK);
        grid[9][0].setTerrain(TerrainType.ROCK);
        grid[0][6].setTerrain(TerrainType.ROCK);
        grid[2][8].setTerrain(TerrainType.ROCK);
        grid[5][8].setTerrain(TerrainType.ROCK);
        grid[7][10].setTerrain(TerrainType.ROCK);
        grid[10][10].setTerrain(TerrainType.ROCK);
    }

    private void initializeRiver() {
        grid[0][2].riverLeft = true;
        grid[0][2].riverTop = true;

        grid[0][1].riverRight = true;

        grid[1][2].riverRight = true;
        grid[1][2].riverBottom = true;

        grid[1][3].riverLeft = true;
        grid[1][3].riverTop = true;

        grid[2][3].riverRight = true;
        grid[2][3].riverBottom = true;

        grid[2][4].riverLeft = true;
        grid[2][4].riverTop = true;

        grid[3][4].riverRight = true;
        grid[3][4].riverBottom = true;

        grid[3][5].riverLeft = true;

        grid[4][4].riverRight = true;

        grid[4][5].riverLeft = true;

        grid[5][4].riverRight = true;
        grid[5][4].riverTop = true;

        grid[5][5].riverLeft = true;

        grid[5][3].riverTop = true;

        grid[6][3].riverLeft = true;
        grid[6][3].riverTop = true;
        grid[6][3].riverBottom = true;

        grid[6][2].riverRight = true;

        grid[7][3].riverRight = true;
        grid[7][3].riverBottom = true;

        grid[7][4].riverLeft = true;

        grid[8][3].riverRight = true;

        grid[8][4].riverLeft = true;
        grid[8][4].riverTop = true;

        grid[9][4].riverRight = true;
        grid[9][4].riverBottom = true;

        grid[9][5].riverLeft = true;

        grid[10][4].riverRight = true;

        grid[10][5].riverLeft = true;
    }

    /**
     * Places a tile on the city grid at the given position.
     * Assumes placement rules have already been validated.
     */
    public void placeTile(Tile tile, Position position) {
        boolean[][] shape = tile.getShape();
        BuildingColor color = tile.getColor();
        int tileId = tile.getId();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j]) {
                    int worldX = position.getX() + j;
                    int worldY = position.getY() + i;
                    if (worldX >= 0 && worldX < GRID_SIZE && worldY >= 0 && worldY < GRID_SIZE) {
                        Cell cell = getCell(worldX, worldY);
                        cell.setOccupied(true);
                        cell.setColor(color);
                        cell.setTileId(tileId);
                    }
                }
            }
        }
    }

    public Cell getCell(int x, int y) {
        if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
            return grid[y][x];
        }
        return null;
    }

    /**
     * Counts visible trees (uncovered TREE cells).
     * Each visible tree = +1 point.
     */
    public int countTrees() {
        int count = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j].getTerrain() == TerrainType.TREE && !grid[i][j].isOccupied()) {
                    count += 1;
                }
            }
        }
        return count;
    }

    /**
     * Counts visible rocks (uncovered ROCK cells).
     * Each visible rock = -1 point.
     */
    public int countRocks() {
        int count = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j].getTerrain() == TerrainType.ROCK && !grid[i][j].isOccupied()) {
                    count -= 1;
                }
            }
        }
        return count;
    }

    /**
     * Counts empty meadow spaces (uncovered MEADOW cells that are buildable).
     * Each empty meadow space = -1 point.
     * Excludes cells with rivers as they cannot be built on.
     */
    public int countEmptyMeadows() {
        int count = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell cell = grid[i][j];
                if (cell.getTerrain() == TerrainType.MEADOW && !cell.isOccupied() && !cell.hasRiver()) {
                    count -= 1;
                }
            }
        }
        return count;
    }

    public void addSticker(Sticker newSticker) {
        this.stickers.add(newSticker);
        Position pos = newSticker.getPosition();
        Cell cell = getCell(pos.getX(), pos.getY());
        if (cell != null) {
            cell.addSticker(newSticker);
            switch (newSticker.getType()) {
                case ADD_ROCK:
                    cell.setTerrain(TerrainType.ROCK);
                    break;
                case ADD_TREE:
                    cell.setTerrain(TerrainType.TREE);
                    break;
                case STRUCTURE_STICKER:
                case BONUS_FIELD:
                    break;
                case WELL:
                    break;
            }
        }
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    /**
     * Counts how many cells are currently occupied by placed tiles.
     */
    public int countOccupiedCells() {
        int count = 0;
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                Cell cell = grid[y][x];
                if (cell != null && cell.isOccupied()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Clears all placed buildings from the board while keeping stickers.
     * Used between episodes in Chapter 1.
     */
    public void clearBuildings() {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                Cell cell = grid[y][x];
                if (cell != null) {
                    cell.setOccupied(false);
                    cell.setColor(BuildingColor.NONE);
                    cell.setTileId(0);
                }
            }
        }
    }

    /**
     * Episode 2 Rule: Calculates bonus points for connected same-color tiles.
     */
    public int calculateConnectedColorBonus() {
        int totalBonus = 0;
        BuildingColor[] colors = {BuildingColor.BLUE, BuildingColor.YELLOW, BuildingColor.RED};

        for (BuildingColor color : colors) {
            boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];
            int maxConnectedForColor = 0;

            for (int y = 0; y < GRID_SIZE; y++) {
                for (int x = 0; x < GRID_SIZE; x++) {
                    Cell cell = grid[y][x];
                    if (cell != null && cell.isOccupied() && cell.getColor() == color && cell.getTileId() > 0 && !visited[y][x]) {
                        Set<Integer> uniqueTileIds = new HashSet<>();
                        countConnectedTiles(x, y, color, visited, uniqueTileIds);
                        uniqueTileIds.remove(0);
                        int tileCount = uniqueTileIds.size();
                        if (tileCount > maxConnectedForColor) {
                            maxConnectedForColor = tileCount;
                        }
                    }
                }
            }
            totalBonus += maxConnectedForColor;
        }

        return totalBonus;
    }

    private void countConnectedTiles(int x, int y, BuildingColor color, boolean[][] visited, Set<Integer> uniqueTileIds) {
        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) {
            return;
        }
        if (visited[y][x]) {
            return;
        }

        Cell cell = grid[y][x];
        if (cell == null || !cell.isOccupied() || cell.getColor() != color || cell.getTileId() <= 0) {
            return;
        }

        visited[y][x] = true;
        uniqueTileIds.add(cell.getTileId());

        countConnectedTiles(x + 1, y, color, visited, uniqueTileIds);
        countConnectedTiles(x - 1, y, color, visited, uniqueTileIds);
        countConnectedTiles(x, y + 1, color, visited, uniqueTileIds);
        countConnectedTiles(x, y - 1, color, visited, uniqueTileIds);
    }

    /**
     * Episode 3 Rule: Checks if all 4 corners around a Well are covered with tiles.
     * Returns 4 if all corners are covered, 0 otherwise.
     */
    public int calculateWellBonus() {
        for (Sticker s : stickers) {
            if (s.getType() == StickerType.WELL) {
                Position pos = s.getPosition();
                int x = pos.getX();
                int y = pos.getY();

                boolean allCornersOccupied = true;
                int[][] corners = {
                    {x - 1, y - 1}, {x + 1, y - 1},
                    {x - 1, y + 1}, {x + 1, y + 1}
                };

                for (int[] corner : corners) {
                    int cx = corner[0];
                    int cy = corner[1];

                    if (cx < 0 || cx >= GRID_SIZE || cy < 0 || cy >= GRID_SIZE) {
                        allCornersOccupied = false;
                        break;
                    }

                    Cell cell = grid[cy][cx];
                    if (cell == null || !cell.isOccupied()) {
                        allCornersOccupied = false;
                        break;
                    }
                }

                if (allCornersOccupied) {
                    return 4;
                }
            }
        }
        return 0;
    }

    public boolean hasWell() {
        for (Sticker s : stickers) {
            if (s.getType() == StickerType.WELL) {
                return true;
            }
        }
        return false;
    }

    public Position getWellPosition() {
        for (Sticker s : stickers) {
            if (s.getType() == StickerType.WELL) {
                return s.getPosition();
            }
        }
        return null;
    }

    /**
     * Counts empty MEADOW spaces for each row from top to bottom.
     * Used for tiebreaker rule.
     */
    public int[] countEmptyMeadowsPerRow() {
        int[] emptyPerRow = new int[GRID_SIZE];
        for (int y = GRID_SIZE - 1; y >= 0; y--) {
            int emptyCount = 0;
            for (int x = 0; x < GRID_SIZE; x++) {
                Cell cell = grid[y][x];
                if (cell != null && cell.getTerrain() == TerrainType.MEADOW && !cell.isOccupied() && !cell.hasRiver()) {
                    emptyCount++;
                }
            }
            emptyPerRow[GRID_SIZE - 1 - y] = emptyCount;
        }
        return emptyPerRow;
    }
}
