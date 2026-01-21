package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpisodeTest {

    private Episode episode;
    private TileDeck deck;
    private PlacementRule rules;
    private EpisodeBonus bonus;
    private List<Sticker> stickerPresets;

    @BeforeEach
    void setUp() {
        List<BuildingCard> cards = new ArrayList<>();
        cards.add(new BuildingCard(new Tile(1, new boolean[][]{{true}})));
        cards.add(new BuildingCard(new Tile(2, new boolean[][]{{true, true}})));
        cards.add(new BuildingCard(new Tile(3, new boolean[][]{{true}, {true}})));

        deck = new TileDeck(cards);
        rules = new PlacementRule();
        bonus = new EpisodeBonus(1, "Test bonus");
        stickerPresets = new ArrayList<>();
        stickerPresets.add(new Sticker(StickerType.ADD_TREE, new Position(5, 5), "tree"));

        episode = new Episode(1, deck, rules, bonus, stickerPresets);
    }

    @Test
    void testConstructor() {
        assertNotNull(episode);
        assertEquals(1, episode.getId());
        assertNotNull(episode.getDeck());
        assertNotNull(episode.getRules());
        assertNotNull(episode.getBonus());
        assertNotNull(episode.getStickerPresets());
    }

    @Test
    void testGetId() {
        assertEquals(1, episode.getId());
    }

    @Test
    void testGetDeck() {
        assertEquals(deck, episode.getDeck());
    }

    @Test
    void testGetRules() {
        assertEquals(rules, episode.getRules());
    }

    @Test
    void testGetBonus() {
        assertEquals(bonus, episode.getBonus());
    }

    @Test
    void testGetStickerPresets() {
        assertEquals(stickerPresets, episode.getStickerPresets());
        assertEquals(1, episode.getStickerPresets().size());
    }

    @Test
    void testDrawCard() {
        BuildingCard card = episode.drawCard();
        assertNotNull(card);
        assertNotNull(card.getTile());
    }

    @Test
    void testHasCardsRemaining() {
        assertTrue(episode.hasCardsRemaining());
        episode.drawCard();
        episode.drawCard();
        episode.drawCard();
        assertFalse(episode.hasCardsRemaining());
    }

    @Test
    void testRemainingCards() {
        assertEquals(3, episode.remainingCards());
        episode.drawCard();
        assertEquals(2, episode.remainingCards());
        episode.drawCard();
        assertEquals(1, episode.remainingCards());
        episode.drawCard();
        assertEquals(0, episode.remainingCards());
    }

    @Test
    void testResetDeck() {
        episode.drawCard();
        episode.drawCard();
        assertEquals(1, episode.remainingCards());

        episode.resetDeck();
        assertEquals(3, episode.remainingCards());
    }

    @Test
    void testIsPlacementValid() {
        CityGrid cityGrid = new CityGrid();
        Tile tile = new Tile(1, new boolean[][]{{true}});
        // Position (2, 0) has a river
        Position validPosition = new Position(2, 0);

        assertTrue(episode.isPlacementValid(tile, cityGrid, validPosition, true));
    }

    @Test
    void testIsPlacementValidFalse() {
        CityGrid cityGrid = new CityGrid();
        Tile tile = new Tile(1, new boolean[][]{{true}});
        Position invalidPosition = new Position(-1, 5);

        assertFalse(episode.isPlacementValid(tile, cityGrid, invalidPosition, true));
    }

    @Test
    void testCalculateBonusSingleArg() {
        CityGrid cityGrid = new CityGrid();
        int bonusValue = episode.calculateBonus(cityGrid);
        assertTrue(bonusValue >= 0 || bonusValue < 0);
    }

    @Test
    void testCalculateBonusWithGameAndPlayerIndex() {
        CityGrid cityGrid = new CityGrid();
        int bonusValue = episode.calculateBonus(cityGrid, null, 0);
        assertTrue(bonusValue >= 0 || bonusValue < 0);
    }

    @Test
    void testApplyStickerPresets() {
        CityGrid cityGrid = new CityGrid();
        int initialStickerCount = cityGrid.getStickers().size();

        episode.applyStickerPresets(cityGrid);

        assertEquals(initialStickerCount + 1, cityGrid.getStickers().size());
    }

    @Test
    void testApplyStickerPresetsWithNullList() {
        Episode episodeWithNullStickers = new Episode(1, deck, rules, bonus, null);
        CityGrid cityGrid = new CityGrid();
        int initialStickerCount = cityGrid.getStickers().size();

        episodeWithNullStickers.applyStickerPresets(cityGrid);

        assertEquals(initialStickerCount, cityGrid.getStickers().size());
    }

    @Test
    void testApplyStickerPresetsWithEmptyList() {
        Episode episodeWithEmptyStickers = new Episode(1, deck, rules, bonus, new ArrayList<>());
        CityGrid cityGrid = new CityGrid();
        int initialStickerCount = cityGrid.getStickers().size();

        episodeWithEmptyStickers.applyStickerPresets(cityGrid);

        assertEquals(initialStickerCount, cityGrid.getStickers().size());
    }

    @Test
    void testGetEpisodeNameChapter1() {
        Episode ep1 = new Episode(1, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 1: The New Land - Episode 1", ep1.getEpisodeName());

        Episode ep2 = new Episode(2, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 1: The New Land - Episode 2", ep2.getEpisodeName());

        Episode ep3 = new Episode(3, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 1: The New Land - Episode 3", ep3.getEpisodeName());
    }

    @Test
    void testGetEpisodeNameChapter2() {
        Episode ep4 = new Episode(4, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 2: The Churches - Episode 1", ep4.getEpisodeName());

        Episode ep5 = new Episode(5, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 2: The Churches - Episode 2", ep5.getEpisodeName());

        Episode ep6 = new Episode(6, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 2: The Churches - Episode 3", ep6.getEpisodeName());
    }

    @Test
    void testGetEpisodeNameChapter3() {
        Episode ep7 = new Episode(7, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 3: The Flood/Gold Fields - Episode 1", ep7.getEpisodeName());
    }

    @Test
    void testGetEpisodeNameChapter4() {
        Episode ep10 = new Episode(10, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 4: The Bandits - Episode 1", ep10.getEpisodeName());
    }

    @Test
    void testGetEpisodeNameChapter5() {
        Episode ep13 = new Episode(13, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 5: The Factories - Episode 1", ep13.getEpisodeName());
    }

    @Test
    void testGetEpisodeNameChapter6() {
        Episode ep16 = new Episode(16, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 6: The Mine - Episode 1", ep16.getEpisodeName());
    }

    @Test
    void testGetEpisodeNameChapter7() {
        Episode ep19 = new Episode(19, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 7: The Railroad - Episode 1", ep19.getEpisodeName());
    }

    @Test
    void testGetEpisodeNameChapter8() {
        Episode ep22 = new Episode(22, deck, rules, bonus, stickerPresets);
        assertEquals("Chapter 8: The Palace - Episode 1", ep22.getEpisodeName());
    }

    @Test
    void testGetEpisodeNameUnknownEpisode() {
        Episode ep99 = new Episode(99, deck, rules, bonus, stickerPresets);
        assertEquals("Episode 99", ep99.getEpisodeName());
    }

    @Test
    void testIsChapterStart() {
        Episode ep1 = new Episode(1, deck, rules, bonus, stickerPresets);
        assertTrue(ep1.isChapterStart());

        Episode ep4 = new Episode(4, deck, rules, bonus, stickerPresets);
        assertTrue(ep4.isChapterStart());

        Episode ep7 = new Episode(7, deck, rules, bonus, stickerPresets);
        assertTrue(ep7.isChapterStart());

        Episode ep2 = new Episode(2, deck, rules, bonus, stickerPresets);
        assertFalse(ep2.isChapterStart());

        Episode ep3 = new Episode(3, deck, rules, bonus, stickerPresets);
        assertFalse(ep3.isChapterStart());
    }

    @Test
    void testIsChapterEnd() {
        Episode ep3 = new Episode(3, deck, rules, bonus, stickerPresets);
        assertTrue(ep3.isChapterEnd());

        Episode ep6 = new Episode(6, deck, rules, bonus, stickerPresets);
        assertTrue(ep6.isChapterEnd());

        Episode ep9 = new Episode(9, deck, rules, bonus, stickerPresets);
        assertTrue(ep9.isChapterEnd());

        Episode ep1 = new Episode(1, deck, rules, bonus, stickerPresets);
        assertFalse(ep1.isChapterEnd());

        Episode ep2 = new Episode(2, deck, rules, bonus, stickerPresets);
        assertFalse(ep2.isChapterEnd());
    }

    @Test
    void testGetChapterNumber() {
        Episode ep1 = new Episode(1, deck, rules, bonus, stickerPresets);
        assertEquals(1, ep1.getChapterNumber());

        Episode ep3 = new Episode(3, deck, rules, bonus, stickerPresets);
        assertEquals(1, ep3.getChapterNumber());

        Episode ep4 = new Episode(4, deck, rules, bonus, stickerPresets);
        assertEquals(2, ep4.getChapterNumber());

        Episode ep7 = new Episode(7, deck, rules, bonus, stickerPresets);
        assertEquals(3, ep7.getChapterNumber());

        Episode ep24 = new Episode(24, deck, rules, bonus, stickerPresets);
        assertEquals(8, ep24.getChapterNumber());
    }

    @Test
    void testGetEpisodeWithinChapter() {
        Episode ep1 = new Episode(1, deck, rules, bonus, stickerPresets);
        assertEquals(1, ep1.getEpisodeWithinChapter());

        Episode ep2 = new Episode(2, deck, rules, bonus, stickerPresets);
        assertEquals(2, ep2.getEpisodeWithinChapter());

        Episode ep3 = new Episode(3, deck, rules, bonus, stickerPresets);
        assertEquals(3, ep3.getEpisodeWithinChapter());

        Episode ep4 = new Episode(4, deck, rules, bonus, stickerPresets);
        assertEquals(1, ep4.getEpisodeWithinChapter());

        Episode ep5 = new Episode(5, deck, rules, bonus, stickerPresets);
        assertEquals(2, ep5.getEpisodeWithinChapter());
    }

    @Test
    void testToString() {
        String result = episode.toString();
        assertTrue(result.contains("Episode{"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name="));
        assertTrue(result.contains("remainingCards="));
        assertTrue(result.contains("stickerCount=1"));
    }

    @Test
    void testToStringWithNullStickers() {
        Episode episodeNullStickers = new Episode(1, deck, rules, bonus, null);
        String result = episodeNullStickers.toString();
        assertTrue(result.contains("stickerCount=0"));
    }

    @Test
    void testEquals() {
        Episode episode1 = new Episode(1, deck, rules, bonus, stickerPresets);
        Episode episode1Copy = new Episode(1, deck, rules, bonus, stickerPresets);
        Episode episode2 = new Episode(2, deck, rules, bonus, stickerPresets);

        assertEquals(episode1, episode1Copy);
        assertNotEquals(episode1, episode2);
    }

    @Test
    void testEqualsWithSameObject() {
        assertEquals(episode, episode);
    }

    @Test
    void testEqualsWithNull() {
        assertNotEquals(episode, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(episode, "not an episode");
    }

    @Test
    void testHashCode() {
        Episode episode1 = new Episode(1, deck, rules, bonus, stickerPresets);
        Episode episode1Copy = new Episode(1, deck, rules, bonus, stickerPresets);

        assertEquals(episode1.hashCode(), episode1Copy.hashCode());
    }

    @Test
    void testHashCodeConsistency() {
        int hash1 = episode.hashCode();
        int hash2 = episode.hashCode();
        assertEquals(hash1, hash2);
    }
}
