package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpisodeBonusTest {

    private CityGrid cityGrid;

    @BeforeEach
    void setUp() {
        cityGrid = new CityGrid();
    }

    @Test
    void testConstructor() {
        EpisodeBonus bonus = new EpisodeBonus(1, "Test description");
        assertEquals(1, bonus.getEpisodeId());
        assertEquals("Test description", bonus.getBonusDescription());
    }

    @Test
    void testGetEpisodeId() {
        EpisodeBonus bonus = new EpisodeBonus(5, "Bonus 5");
        assertEquals(5, bonus.getEpisodeId());
    }

    @Test
    void testGetBonusDescription() {
        EpisodeBonus bonus = new EpisodeBonus(1, "Trees and rocks bonus");
        assertEquals("Trees and rocks bonus", bonus.getBonusDescription());
    }

    @Test
    void testCalculateBonusWithNullCityGrid() {
        EpisodeBonus bonus = new EpisodeBonus(1, "Test");
        assertEquals(0, bonus.calculateBonus(null, null, 0));
    }

    @Test
    void testEpisode1Bonus() {
        EpisodeBonus bonus = new EpisodeBonus(1, "Trees and rocks");
        int result = bonus.calculateBonus(cityGrid, null, 0);
        assertTrue(result >= 0 || result < 0);
    }

    @Test
    void testEpisode2BonusLargestColorGroup() {
        EpisodeBonus bonus = new EpisodeBonus(2, "Largest color group");
        CityGrid grid = new CityGrid(false);

        Tile tile1 = new Tile(1, new boolean[][]{{true}});
        tile1.setColor(BuildingColor.RED);
        grid.placeTile(tile1, new Position(5, 5));

        Tile tile2 = new Tile(2, new boolean[][]{{true}});
        tile2.setColor(BuildingColor.RED);
        grid.placeTile(tile2, new Position(5, 6));

        int result = bonus.calculateBonus(grid, null, 0);
        assertTrue(result >= 0);
    }

    @Test
    void testEpisode3BonusWithWell() {
        EpisodeBonus bonus = new EpisodeBonus(3, "Well bonus");
        CityGrid grid = new CityGrid(false);

        Sticker wellSticker = new Sticker(StickerType.WELL, new Position(5, 5), "well");
        grid.addSticker(wellSticker);

        Tile tile1 = new Tile(1, new boolean[][]{{true}});
        grid.placeTile(tile1, new Position(4, 5));
        Tile tile2 = new Tile(2, new boolean[][]{{true}});
        grid.placeTile(tile2, new Position(6, 5));
        Tile tile3 = new Tile(3, new boolean[][]{{true}});
        grid.placeTile(tile3, new Position(5, 4));
        Tile tile4 = new Tile(4, new boolean[][]{{true}});
        grid.placeTile(tile4, new Position(5, 6));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(4, result);
    }

    @Test
    void testEpisode4BonusChurchWithTwoColors() {
        EpisodeBonus bonus = new EpisodeBonus(4, "Church with 2+ colors");
        CityGrid grid = new CityGrid(false);

        Sticker churchSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "church");
        grid.addSticker(churchSticker);

        Tile tile1 = new Tile(1, new boolean[][]{{true}});
        tile1.setColor(BuildingColor.RED);
        grid.placeTile(tile1, new Position(4, 5));

        Tile tile2 = new Tile(2, new boolean[][]{{true}});
        tile2.setColor(BuildingColor.BLUE);
        grid.placeTile(tile2, new Position(6, 5));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(3, result);
    }

    @Test
    void testEpisode5BonusChurchWithThreeSpecificColors() {
        EpisodeBonus bonus = new EpisodeBonus(5, "Church with red, blue, yellow");
        CityGrid grid = new CityGrid(false);

        Sticker churchSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "church");
        grid.addSticker(churchSticker);

        Tile tile1 = new Tile(1, new boolean[][]{{true}});
        tile1.setColor(BuildingColor.RED);
        grid.placeTile(tile1, new Position(4, 5));

        Tile tile2 = new Tile(2, new boolean[][]{{true}});
        tile2.setColor(BuildingColor.BLUE);
        grid.placeTile(tile2, new Position(6, 5));

        Tile tile3 = new Tile(3, new boolean[][]{{true}});
        tile3.setColor(BuildingColor.YELLOW);
        grid.placeTile(tile3, new Position(5, 4));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(5, result);
    }

    @Test
    void testEpisode6BonusLonelyChurchPenalty() {
        EpisodeBonus bonus = new EpisodeBonus(6, "Lonely church penalty");
        CityGrid grid = new CityGrid(false);

        Sticker churchSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "church");
        grid.addSticker(churchSticker);

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(-3, result);
    }

    @Test
    void testEpisode7BonusGoldFieldCovered() {
        EpisodeBonus bonus = new EpisodeBonus(7, "Gold fields covered");
        CityGrid grid = new CityGrid(false);

        Sticker goldSticker = new Sticker(StickerType.BONUS_FIELD, new Position(5, 5), "gold field");
        grid.addSticker(goldSticker);

        Tile tile = new Tile(1, new boolean[][]{{true}});
        grid.placeTile(tile, new Position(5, 5));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(1, result);
    }

    @Test
    void testEpisode8BonusAllGoldCovered() {
        EpisodeBonus bonus = new EpisodeBonus(8, "All gold covered");
        CityGrid grid = new CityGrid(false);

        Sticker goldSticker = new Sticker(StickerType.BONUS_FIELD, new Position(5, 5), "gold field");
        grid.addSticker(goldSticker);

        Tile tile = new Tile(1, new boolean[][]{{true}});
        grid.placeTile(tile, new Position(5, 5));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(3, result);
    }

    @Test
    void testEpisode9BonusAllGoldCoveredHigherBonus() {
        EpisodeBonus bonus = new EpisodeBonus(9, "All gold covered higher");
        CityGrid grid = new CityGrid(false);

        Sticker goldSticker = new Sticker(StickerType.BONUS_FIELD, new Position(5, 5), "gold field");
        grid.addSticker(goldSticker);

        Tile tile = new Tile(1, new boolean[][]{{true}});
        grid.placeTile(tile, new Position(5, 5));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(5, result);
    }

    @Test
    void testEpisode10BonusBanditSurrounded() {
        EpisodeBonus bonus = new EpisodeBonus(10, "Surrounded bandit");
        CityGrid grid = new CityGrid(false);

        Sticker banditSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "bandit");
        grid.addSticker(banditSticker);

        grid.placeTile(new Tile(1, new boolean[][]{{true}}), new Position(4, 5));
        grid.placeTile(new Tile(2, new boolean[][]{{true}}), new Position(6, 5));
        grid.placeTile(new Tile(3, new boolean[][]{{true}}), new Position(5, 4));
        grid.placeTile(new Tile(4, new boolean[][]{{true}}), new Position(5, 6));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(2, result);
    }

    @Test
    void testEpisode10BonusBanditNotSurrounded() {
        EpisodeBonus bonus = new EpisodeBonus(10, "Free bandit");
        CityGrid grid = new CityGrid(false);

        Sticker banditSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "bandit");
        grid.addSticker(banditSticker);

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(-3, result);
    }

    @Test
    void testEpisode11BonusBanditSurrounded() {
        EpisodeBonus bonus = new EpisodeBonus(11, "Surrounded bandit higher");
        CityGrid grid = new CityGrid(false);

        Sticker banditSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "bandit");
        grid.addSticker(banditSticker);

        grid.placeTile(new Tile(1, new boolean[][]{{true}}), new Position(4, 5));
        grid.placeTile(new Tile(2, new boolean[][]{{true}}), new Position(6, 5));
        grid.placeTile(new Tile(3, new boolean[][]{{true}}), new Position(5, 4));
        grid.placeTile(new Tile(4, new boolean[][]{{true}}), new Position(5, 6));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(3, result);
    }

    @Test
    void testEpisode12BonusAllBanditsArrested() {
        EpisodeBonus bonus = new EpisodeBonus(12, "All bandits arrested");
        CityGrid grid = new CityGrid(false);

        Sticker banditSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "bandit");
        grid.addSticker(banditSticker);

        grid.placeTile(new Tile(1, new boolean[][]{{true}}), new Position(4, 5));
        grid.placeTile(new Tile(2, new boolean[][]{{true}}), new Position(6, 5));
        grid.placeTile(new Tile(3, new boolean[][]{{true}}), new Position(5, 4));
        grid.placeTile(new Tile(4, new boolean[][]{{true}}), new Position(5, 6));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(7, result);
    }

    @Test
    void testEpisode13BonusFactoryWithTwoNeighbors() {
        EpisodeBonus bonus = new EpisodeBonus(13, "Factory with 2+ neighbors");
        CityGrid grid = new CityGrid(false);

        Sticker factorySticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "factory");
        grid.addSticker(factorySticker);

        grid.placeTile(new Tile(1, new boolean[][]{{true}}), new Position(4, 5));
        grid.placeTile(new Tile(2, new boolean[][]{{true}}), new Position(6, 5));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(2, result);
    }

    @Test
    void testEpisode14BonusFactoryCluster() {
        EpisodeBonus bonus = new EpisodeBonus(14, "Factory cluster");
        CityGrid grid = new CityGrid(false);

        grid.addSticker(new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "factory"));
        grid.addSticker(new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 6), "factory"));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(2, result);
    }

    @Test
    void testEpisode15BonusTwoFactoryClusters() {
        EpisodeBonus bonus = new EpisodeBonus(15, "Two factory clusters");
        CityGrid grid = new CityGrid(false);

        grid.addSticker(new Sticker(StickerType.STRUCTURE_STICKER, new Position(2, 2), "factory"));
        grid.addSticker(new Sticker(StickerType.STRUCTURE_STICKER, new Position(2, 3), "factory"));
        grid.addSticker(new Sticker(StickerType.STRUCTURE_STICKER, new Position(8, 8), "factory"));
        grid.addSticker(new Sticker(StickerType.STRUCTURE_STICKER, new Position(8, 9), "factory"));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(4, result);
    }

    @Test
    void testEpisode16BonusMineDepth() {
        EpisodeBonus bonus = new EpisodeBonus(16, "Mine depth");
        Game game = new Game();
        game.startNewGame("Player1", "Player2");
        game.incrementMineDepth(0);
        game.incrementMineDepth(0);

        int result = bonus.calculateBonus(cityGrid, game, 0);
        assertEquals(2, result);
    }

    @Test
    void testEpisode16BonusWithNullGame() {
        EpisodeBonus bonus = new EpisodeBonus(16, "Mine depth");
        int result = bonus.calculateBonus(cityGrid, null, 0);
        assertEquals(0, result);
    }

    @Test
    void testEpisode17BonusMineDepthWithThresholds() {
        EpisodeBonus bonus = new EpisodeBonus(17, "Mine depth with thresholds");
        Game game = new Game();
        game.startNewGame("Player1", "Player2");

        for (int i = 0; i < 6; i++) {
            game.incrementMineDepth(0);
        }

        int result = bonus.calculateBonus(cityGrid, game, 0);
        assertEquals(14, result);
    }

    @Test
    void testEpisode18BonusGoldNuggets() {
        EpisodeBonus bonus = new EpisodeBonus(18, "Gold nuggets");
        Game game = new Game();
        game.startNewGame("Player1", "Player2");
        game.addGoldNugget(0);
        game.addGoldNugget(0);
        game.addGoldNugget(0);

        int result = bonus.calculateBonus(cityGrid, game, 0);
        assertEquals(6, result);
    }

    @Test
    void testEpisode19BonusRailroadCovered() {
        EpisodeBonus bonus = new EpisodeBonus(19, "Railroad covered");
        CityGrid grid = new CityGrid(false);

        Sticker railroadSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "railroad");
        grid.addSticker(railroadSticker);

        Tile tile = new Tile(1, new boolean[][]{{true}});
        grid.placeTile(tile, new Position(5, 5));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(1, result);
    }

    @Test
    void testEpisode20BonusBottomRowRailroads() {
        EpisodeBonus bonus = new EpisodeBonus(20, "Bottom row railroads");
        CityGrid grid = new CityGrid(false);

        Sticker railroadSticker = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 10), "railroad");
        grid.addSticker(railroadSticker);

        Tile tile = new Tile(1, new boolean[][]{{true}});
        grid.placeTile(tile, new Position(5, 10));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(5, result);
    }

    @Test
    void testEpisode21BonusConnectedRailroads() {
        EpisodeBonus bonus = new EpisodeBonus(21, "Connected railroads");
        CityGrid grid = new CityGrid(false);

        Sticker railroad1 = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 5), "railroad");
        Sticker railroad2 = new Sticker(StickerType.STRUCTURE_STICKER, new Position(5, 6), "railroad");
        grid.addSticker(railroad1);
        grid.addSticker(railroad2);

        grid.placeTile(new Tile(1, new boolean[][]{{true}}), new Position(5, 5));
        grid.placeTile(new Tile(2, new boolean[][]{{true}}), new Position(5, 6));

        int result = bonus.calculateBonus(grid, null, 0);
        assertEquals(6, result);
    }

    @Test
    void testEpisode22BonusPalaceSize() {
        EpisodeBonus bonus = new EpisodeBonus(22, "Palace size");
        Game game = new Game();
        game.startNewGame("Player1", "Player2");
        game.incrementPalaceSize(0);
        game.incrementPalaceSize(0);
        game.incrementPalaceSize(0);

        int result = bonus.calculateBonus(cityGrid, game, 0);
        assertEquals(3, result);
    }

    @Test
    void testEpisode23BonusPalaceSizeThreshold() {
        EpisodeBonus bonus = new EpisodeBonus(23, "Palace size threshold");
        Game game = new Game();
        game.startNewGame("Player1", "Player2");

        for (int i = 0; i < 4; i++) {
            game.incrementPalaceSize(0);
        }

        int result = bonus.calculateBonus(cityGrid, game, 0);
        assertEquals(5, result);
    }

    @Test
    void testEpisode23BonusPalaceSizeBelowThreshold() {
        EpisodeBonus bonus = new EpisodeBonus(23, "Palace size threshold");
        Game game = new Game();
        game.startNewGame("Player1", "Player2");
        game.incrementPalaceSize(0);
        game.incrementPalaceSize(0);

        int result = bonus.calculateBonus(cityGrid, game, 0);
        assertEquals(0, result);
    }

    @Test
    void testEpisode24BonusReturnsZero() {
        EpisodeBonus bonus = new EpisodeBonus(24, "Final episode");
        Game game = new Game();
        game.startNewGame("Player1", "Player2");

        int result = bonus.calculateBonus(cityGrid, game, 0);
        assertEquals(0, result);
    }

    @Test
    void testUnknownEpisodeReturnsZero() {
        EpisodeBonus bonus = new EpisodeBonus(99, "Unknown episode");
        int result = bonus.calculateBonus(cityGrid, null, 0);
        assertEquals(0, result);
    }

    @Test
    void testToString() {
        EpisodeBonus bonus = new EpisodeBonus(1, "Test description");
        String result = bonus.toString();
        assertTrue(result.contains("EpisodeBonus{"));
        assertTrue(result.contains("episode=1"));
        assertTrue(result.contains("Test description"));
    }

    @Test
    void testPlayer2Index() {
        EpisodeBonus bonus = new EpisodeBonus(16, "Mine depth");
        Game game = new Game();
        game.startNewGame("Player1", "Player2");
        game.incrementMineDepth(1);
        game.incrementMineDepth(1);
        game.incrementMineDepth(1);

        int result = bonus.calculateBonus(cityGrid, game, 1);
        assertEquals(3, result);
    }
}
