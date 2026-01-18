package io.github.myCity.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Complete implementation of bonus calculation for all 24 episodes.
 */
public class EpisodeBonus {
    private final int episodeId;
    private final String bonusDescription;

    public EpisodeBonus(int episodeId, String bonusDescription) {
        this.episodeId = episodeId;
        this.bonusDescription = bonusDescription;
    }

    /**
     * Main bonus calculation with game state support.
     */
    public int calculateBonus(CityGrid cityGrid, Game game, int playerIndex) {
        if (cityGrid == null) return 0;

        switch (episodeId) {
            case 1:  return episode1(cityGrid);
            case 2:  return episode2(cityGrid);
            case 3:  return episode3(cityGrid);
            case 4:  return episode4(cityGrid);
            case 5:  return episode5(cityGrid);
            case 6:  return episode6(cityGrid);
            case 7:  return episode7(cityGrid);
            case 8:  return episode8(cityGrid);
            case 9:  return episode9(cityGrid);
            case 10: return episode10(cityGrid);
            case 11: return episode11(cityGrid);
            case 12: return episode12(cityGrid);
            case 13: return episode13(cityGrid);
            case 14: return episode14(cityGrid);
            case 15: return episode15(cityGrid);
            case 16: return episode16(cityGrid, game, playerIndex);
            case 17: return episode17(cityGrid, game, playerIndex);
            case 18: return episode18(cityGrid, game, playerIndex);
            case 19: return episode19(cityGrid);
            case 20: return episode20(cityGrid);
            case 21: return episode21(cityGrid);
            case 22: return episode22(cityGrid, game, playerIndex);
            case 23: return episode23(cityGrid, game, playerIndex);
            case 24: return episode24(cityGrid, game, playerIndex);
            default: return 0;
        }
    }

    private int episode1(CityGrid cityGrid) {
        return (cityGrid.countTrees() + cityGrid.countRocks()) / 2;
    }

    private int episode2(CityGrid cityGrid) {
        int maxGroupSize = 0;
        boolean[][] visited = new boolean[11][11];

        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 11; x++) {
                Cell cell = cityGrid.getCell(x, y);
                if (cell != null && cell.isOccupied() && !visited[y][x] &&
                    cell.getColor() != BuildingColor.NONE) {
                    int groupSize = dfsColorGroup(cityGrid, x, y, cell.getColor(), visited);
                    maxGroupSize = Math.max(maxGroupSize, groupSize);
                }
            }
        }

        return maxGroupSize;
    }

    private int dfsColorGroup(CityGrid cityGrid, int x, int y, BuildingColor targetColor, boolean[][] visited) {
        if (x < 0 || x >= 11 || y < 0 || y >= 11 || visited[y][x]) {
            return 0;
        }

        Cell cell = cityGrid.getCell(x, y);
        if (cell == null || !cell.isOccupied() || cell.getColor() != targetColor) {
            return 0;
        }

        visited[y][x] = true;
        int size = 1;

        size += dfsColorGroup(cityGrid, x + 1, y, targetColor, visited);
        size += dfsColorGroup(cityGrid, x - 1, y, targetColor, visited);
        size += dfsColorGroup(cityGrid, x, y + 1, targetColor, visited);
        size += dfsColorGroup(cityGrid, x, y - 1, targetColor, visited);

        return size;
    }

    private int episode3(CityGrid cityGrid) {
        int bonus = (cityGrid.countTrees() + cityGrid.countRocks()) / 2;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("well")) {
                if (isSurrounded(cityGrid, sticker.getPosition())) {
                    bonus += 4;
                }
            }
        }

        return bonus;
    }

    private int episode4(CityGrid cityGrid) {
        int bonus = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("church")) {
                Position pos = sticker.getPosition();
                Set<BuildingColor> adjacentColors = getAdjacentColors(cityGrid, pos);
                adjacentColors.remove(BuildingColor.NONE);
                adjacentColors.remove(BuildingColor.CHURCH);

                if (adjacentColors.size() >= 2) {
                    bonus += 3;
                }
            }
        }

        return bonus;
    }

    private int episode5(CityGrid cityGrid) {
        int bonus = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("church")) {
                Position pos = sticker.getPosition();
                Set<BuildingColor> adjacentColors = getAdjacentColors(cityGrid, pos);
                adjacentColors.remove(BuildingColor.NONE);
                adjacentColors.remove(BuildingColor.CHURCH);

                if (adjacentColors.contains(BuildingColor.RED) &&
                    adjacentColors.contains(BuildingColor.BLUE) &&
                    adjacentColors.contains(BuildingColor.YELLOW)) {
                    bonus += 5;
                }
            }
        }

        return bonus;
    }

    private int episode6(CityGrid cityGrid) {
        int bonus = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("church")) {
                Position pos = sticker.getPosition();
                if (countOccupiedNeighbors(cityGrid, pos) == 0) {
                    bonus -= 3;
                }
            }
        }

        return bonus;
    }

    private Set<BuildingColor> getAdjacentColors(CityGrid cityGrid, Position pos) {
        Set<BuildingColor> colors = new HashSet<>();
        int[][] offsets = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] offset : offsets) {
            int nx = pos.getX() + offset[0];
            int ny = pos.getY() + offset[1];

            Cell cell = cityGrid.getCell(nx, ny);
            if (cell != null && cell.isOccupied()) {
                colors.add(cell.getColor());
            }
        }

        return colors;
    }

    private int episode7(CityGrid cityGrid) {
        int bonus = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("gold")) {
                Position pos = sticker.getPosition();
                Cell cell = cityGrid.getCell(pos.getX(), pos.getY());
                if (cell != null && cell.isOccupied()) {
                    bonus += 1;
                }
            }
        }

        return bonus;
    }

    private int episode8(CityGrid cityGrid) {
        int goldFields = 0;
        int coveredGold = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("gold")) {
                goldFields++;
                Position pos = sticker.getPosition();
                Cell cell = cityGrid.getCell(pos.getX(), pos.getY());
                if (cell != null && cell.isOccupied()) {
                    coveredGold++;
                }
            }
        }

        return (goldFields > 0 && goldFields == coveredGold) ? 3 : 0;
    }

    private int episode9(CityGrid cityGrid) {
        int goldFields = 0;
        int coveredGold = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("gold")) {
                goldFields++;
                Position pos = sticker.getPosition();
                Cell cell = cityGrid.getCell(pos.getX(), pos.getY());
                if (cell != null && cell.isOccupied()) {
                    coveredGold++;
                }
            }
        }

        return (goldFields > 0 && goldFields == coveredGold) ? 5 : 0;
    }

    private int episode10(CityGrid cityGrid) {
        int bonus = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("bandit")) {
                if (isSurrounded(cityGrid, sticker.getPosition())) {
                    bonus += 2;
                } else {
                    bonus -= 3;
                }
            }
        }

        return bonus;
    }

    private int episode11(CityGrid cityGrid) {
        int bonus = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("bandit")) {
                if (isSurrounded(cityGrid, sticker.getPosition())) {
                    bonus += 3;
                } else {
                    bonus -= 3;
                }
            }
        }

        return bonus;
    }

    private int episode12(CityGrid cityGrid) {
        int bandits = 0;
        int arrested = 0;
        int bonus = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("bandit")) {
                bandits++;
                if (isSurrounded(cityGrid, sticker.getPosition())) {
                    arrested++;
                    bonus += 2;
                } else {
                    bonus -= 3;
                }
            }
        }

        if (bandits > 0 && bandits == arrested) {
            bonus += 5;
        }

        return bonus;
    }

    private int episode13(CityGrid cityGrid) {
        int bonus = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("factory")) {
                Position pos = sticker.getPosition();
                if (countOccupiedNeighbors(cityGrid, pos) >= 2) {
                    bonus += 2;
                }
            }
        }

        return bonus;
    }

    private int episode14(CityGrid cityGrid) {
        List<Position> factories = new ArrayList<>();
        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("factory")) {
                factories.add(sticker.getPosition());
            }
        }

        if (factories.isEmpty()) return 0;

        boolean[] visited = new boolean[factories.size()];
        int bonus = 0;

        for (int i = 0; i < factories.size(); i++) {
            if (!visited[i]) {
                int clusterSize = dfsClusterSize(factories, visited, i);

                if (clusterSize == 2) bonus += 2;
                else if (clusterSize == 3) bonus += 4;
                else if (clusterSize >= 4) bonus += 7;
            }
        }

        return bonus;
    }

    private int episode15(CityGrid cityGrid) {
        List<Position> factories = new ArrayList<>();
        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("factory")) {
                factories.add(sticker.getPosition());
            }
        }

        if (factories.size() < 4) return 0;

        boolean[] visited = new boolean[factories.size()];
        int validClusters = 0;

        for (int i = 0; i < factories.size(); i++) {
            if (!visited[i]) {
                int clusterSize = dfsClusterSize(factories, visited, i);
                if (clusterSize >= 2) {
                    validClusters++;
                }
            }
        }

        return (validClusters >= 2) ? 4 : 0;
    }

    private int dfsClusterSize(List<Position> positions, boolean[] visited, int index) {
        visited[index] = true;
        int size = 1;
        Position current = positions.get(index);

        for (int i = 0; i < positions.size(); i++) {
            if (!visited[i]) {
                Position other = positions.get(i);
                if (areAdjacent(current, other)) {
                    size += dfsClusterSize(positions, visited, i);
                }
            }
        }

        return size;
    }

    private boolean areAdjacent(Position p1, Position p2) {
        int dx = Math.abs(p1.getX() - p2.getX());
        int dy = Math.abs(p1.getY() - p2.getY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    @SuppressWarnings("unused")
    private int episode16(CityGrid cityGrid, Game game, int playerIndex) {
        if (game == null) return 0;
        return game.getMineDepth(playerIndex);
    }

    @SuppressWarnings("unused")
    private int episode17(CityGrid cityGrid, Game game, int playerIndex) {
        if (game == null) return 0;
        int depth = game.getMineDepth(playerIndex);
        int bonus = depth;

        if (depth >= 4) bonus += 3;
        if (depth >= 6) bonus += 5;

        return bonus;
    }

    @SuppressWarnings("unused")
    private int episode18(CityGrid cityGrid, Game game, int playerIndex) {
        if (game == null) return 0;
        return game.getGoldNuggets(playerIndex) * 2;
    }

    private int episode19(CityGrid cityGrid) {
        int bonus = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("railroad")) {
                Position pos = sticker.getPosition();
                Cell cell = cityGrid.getCell(pos.getX(), pos.getY());
                if (cell != null && cell.isOccupied()) {
                    bonus += 1;
                }
            }
        }

        return bonus;
    }

    private int episode20(CityGrid cityGrid) {
        int bottomRowRailroads = 0;
        int coveredBottomRailroads = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("railroad")) {
                Position pos = sticker.getPosition();

                if (pos.getY() == 10) {
                    bottomRowRailroads++;
                    Cell cell = cityGrid.getCell(pos.getX(), pos.getY());
                    if (cell != null && cell.isOccupied()) {
                        coveredBottomRailroads++;
                    }
                }
            }
        }

        return (bottomRowRailroads > 0 && bottomRowRailroads == coveredBottomRailroads) ? 5 : 0;
    }

    private int episode21(CityGrid cityGrid) {
        int connectedBranches = 0;

        for (Sticker sticker : cityGrid.getStickers()) {
            if (sticker.getEffectDescription().contains("railroad")) {
                Position pos = sticker.getPosition();
                Cell cell = cityGrid.getCell(pos.getX(), pos.getY());
                if (cell != null && cell.isOccupied()) {
                    if (hasRailroadNeighbor(cityGrid, pos)) {
                        connectedBranches++;
                    }
                }
            }
        }

        return connectedBranches * 3;
    }

    private boolean hasRailroadNeighbor(CityGrid cityGrid, Position pos) {
        int[][] offsets = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] offset : offsets) {
            int nx = pos.getX() + offset[0];
            int ny = pos.getY() + offset[1];

            if (nx >= 0 && nx < 11 && ny >= 0 && ny < 11) {
                Cell cell = cityGrid.getCell(nx, ny);
                if (cell != null && cell.isOccupied()) {
                    for (Sticker s : cell.getStickers()) {
                        if (s.getEffectDescription().contains("railroad")) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unused")
    private int episode22(CityGrid cityGrid, Game game, int playerIndex) {
        if (game == null) return 0;
        return game.getPalaceSize(playerIndex);
    }

    @SuppressWarnings("unused")
    private int episode23(CityGrid cityGrid, Game game, int playerIndex) {
        if (game == null) return 0;
        int palaceSize = game.getPalaceSize(playerIndex);
        return (palaceSize >= 4) ? 5 : 0;
    }

    @SuppressWarnings("unused")
    private int episode24(CityGrid cityGrid, Game game, int playerIndex) {
        return 0;
    }

    private boolean isSurrounded(CityGrid cityGrid, Position pos) {
        int[][] offsets = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] offset : offsets) {
            int nx = pos.getX() + offset[0];
            int ny = pos.getY() + offset[1];

            if (nx < 0 || nx >= 11 || ny < 0 || ny >= 11) {
                return false;
            }

            Cell cell = cityGrid.getCell(nx, ny);
            if (cell == null || !cell.isOccupied()) {
                return false;
            }
        }

        return true;
    }

    private int countOccupiedNeighbors(CityGrid cityGrid, Position pos) {
        int count = 0;
        int[][] offsets = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] offset : offsets) {
            int nx = pos.getX() + offset[0];
            int ny = pos.getY() + offset[1];

            Cell cell = cityGrid.getCell(nx, ny);
            if (cell != null && cell.isOccupied()) {
                count++;
            }
        }

        return count;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public String getBonusDescription() {
        return bonusDescription;
    }

    @Override
    public String toString() {
        return "EpisodeBonus{episode=" + episodeId + ", description='" + bonusDescription + "'}";
    }
}
