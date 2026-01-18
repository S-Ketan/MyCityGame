package io.github.myCity.model;

/**
 * Validates tile placements according to game rules.
 */
public class PlacementRule {

    private static final int GRID_SIZE = 11;

    /**
     * Validates a tile placement.
     *
     * @param tile      the tile to place
     * @param cityGrid  the city grid
     * @param position  the position to place at
     * @param isFirst   whether this is the first tile placement
     * @return true if placement is valid
     */
    public boolean validatePlacement(Tile tile, CityGrid cityGrid, Position position, boolean isFirst) {
        if (tile == null || cityGrid == null || position == null) {
            return false;
        }

        if (!isWithinBounds(tile, cityGrid, position)) {
            return false;
        }

        if (isFirst) {
            if (!validateFirstTilePlacement(tile, cityGrid, position)) {
                return false;
            }
        } else {
            if (!validateAdjacentToBuilding(tile, cityGrid, position)) {
                return false;
            }
        }

        if (!validateBuildableTerrain(tile, cityGrid, position)) {
            return false;
        }

        if (!validateNotCrossingRiver(tile, cityGrid, position)) {
            return false;
        }

        if (!validateNotOnRestrictedArea(tile, cityGrid, position)) {
            return false;
        }

        return true;
    }

    private boolean isWithinBounds(Tile tile, CityGrid cityGrid, Position position) {
        boolean[][] shape = tile.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j]) {
                    int worldX = position.getX() + j;
                    int worldY = position.getY() + i;

                    if (worldX < 0 || worldX >= GRID_SIZE || worldY < 0 || worldY >= GRID_SIZE) {
                        return false;
                    }

                    Cell cell = cityGrid.getCell(worldX, worldY);
                    if (cell == null || cell.isOccupied()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * First tile must be adjacent to the river.
     */
    private boolean validateFirstTilePlacement(Tile tile, CityGrid cityGrid, Position position) {
        boolean[][] shape = tile.getShape();
        boolean touchesRiver = false;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j]) {
                    int worldX = position.getX() + j;
                    int worldY = position.getY() + i;

                    Cell cell = cityGrid.getCell(worldX, worldY);
                    if (cell == null) {
                        continue;
                    }

                    if (cell.hasRiver()) {
                        touchesRiver = true;
                        break;
                    }
                }
            }
            if (touchesRiver) break;
        }

        return touchesRiver;
    }

    /**
     * Tile must connect to at least one existing building (orthogonally).
     */
    private boolean validateAdjacentToBuilding(Tile tile, CityGrid cityGrid, Position position) {
        boolean[][] shape = tile.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j]) {
                    int worldX = position.getX() + j;
                    int worldY = position.getY() + i;

                    if (hasOccupiedNeighbor(cityGrid, worldX, worldY)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean hasOccupiedNeighbor(CityGrid cityGrid, int x, int y) {
        int[][] offsets = {
                { 0, -1 },
                { 0, 1 },
                { -1, 0 },
                { 1, 0 }
        };

        for (int[] offset : offsets) {
            int nx = x + offset[0];
            int ny = y + offset[1];

            if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE) {
                Cell cell = cityGrid.getCell(nx, ny);
                if (cell != null && cell.isOccupied()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Cannot build on certain terrain types (mountains, forest).
     */
    private boolean validateBuildableTerrain(Tile tile, CityGrid cityGrid, Position position) {
        boolean[][] shape = tile.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j]) {
                    int worldX = position.getX() + j;
                    int worldY = position.getY() + i;

                    Cell cell = cityGrid.getCell(worldX, worldY);
                    if (cell != null) {
                        TerrainType terrain = cell.getTerrain();
                        if (terrain == TerrainType.MOUNTAIN || terrain == TerrainType.FOREST) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Cannot build across rivers (tile cannot span across river edges).
     */
    private boolean validateNotCrossingRiver(Tile tile, CityGrid cityGrid, Position position) {
        boolean[][] shape = tile.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (!shape[i][j]) continue;

                int worldX = position.getX() + j;
                int worldY = position.getY() + i;
                Cell cell = cityGrid.getCell(worldX, worldY);

                if (j + 1 < shape[i].length && shape[i][j + 1]) {
                    Cell right = cityGrid.getCell(worldX + 1, worldY);
                    if ((cell != null && cell.riverRight) || (right != null && right.riverLeft)) {
                        return false;
                    }
                }

                if (j - 1 >= 0 && shape[i][j - 1]) {
                    Cell left = cityGrid.getCell(worldX - 1, worldY);
                    if ((cell != null && cell.riverLeft) || (left != null && left.riverRight)) {
                        return false;
                    }
                }

                if (i + 1 < shape.length && shape[i + 1][j]) {
                    Cell down = cityGrid.getCell(worldX, worldY + 1);
                    if ((cell != null && cell.riverTop) || (down != null && down.riverBottom)) {
                        return false;
                    }
                }

                if (i - 1 >= 0 && shape[i - 1][j]) {
                    Cell up = cityGrid.getCell(worldX, worldY - 1);
                    if ((cell != null && cell.riverBottom) || (up != null && up.riverTop)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Cannot build in restricted areas marked by stickers.
     */
    private boolean validateNotOnRestrictedArea(Tile tile, CityGrid cityGrid, Position position) {
        boolean[][] shape = tile.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j]) {
                    int worldX = position.getX() + j;
                    int worldY = position.getY() + i;

                    Cell cell = cityGrid.getCell(worldX, worldY);
                    if (cell != null) {
                        for (Sticker sticker : cell.getStickers()) {
                            if (sticker.getEffectDescription().contains("restricted") ||
                                    sticker.getEffectDescription().contains("forbidden")) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Validates a placement with detailed error reporting.
     */
    public String validateWithErrorMessage(Tile tile, CityGrid cityGrid, Position position, boolean isFirst) {
        if (tile == null || cityGrid == null || position == null) {
            return "Invalid parameters";
        }

        if (!isWithinBounds(tile, cityGrid, position)) {
            return "Tile is out of bounds or overlaps existing buildings";
        }

        if (isFirst) {
            if (!validateFirstTilePlacement(tile, cityGrid, position)) {
                return "First tile must be adjacent to the river";
            }
            return null;
        }

        if (!validateAdjacentToBuilding(tile, cityGrid, position)) {
            return "Tile must connect to at least one existing building";
        }

        if (!validateBuildableTerrain(tile, cityGrid, position)) {
            return "Cannot build on mountains or forest";
        }

        if (!validateNotCrossingRiver(tile, cityGrid, position)) {
            return "Cannot build across rivers";
        }

        if (!validateNotOnRestrictedArea(tile, cityGrid, position)) {
            return "Cannot build in restricted area";
        }

        return null;
    }

    public boolean isValid(Tile tile, CityGrid cityGrid, Position position, boolean isFirst) {
        return validatePlacement(tile, cityGrid, position, isFirst);
    }
}
