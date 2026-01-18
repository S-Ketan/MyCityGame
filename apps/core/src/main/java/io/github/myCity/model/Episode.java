package io.github.myCity.model;

import java.util.List;

/**
 * Represents a game episode (formerly Kapitel).
 */
public class Episode {
    private final int id;
    private final TileDeck deck;
    private final PlacementRule rules;
    private final EpisodeBonus bonus;
    private final List<Sticker> stickerPresets;

    public Episode(int id, TileDeck deck, PlacementRule rules,
            EpisodeBonus bonus, List<Sticker> stickerPresets) {
        this.id = id;
        this.deck = deck;
        this.rules = rules;
        this.bonus = bonus;
        this.stickerPresets = stickerPresets;
    }

    public int getId() {
        return id;
    }

    public TileDeck getDeck() {
        return deck;
    }

    public PlacementRule getRules() {
        return rules;
    }

    public EpisodeBonus getBonus() {
        return bonus;
    }

    public List<Sticker> getStickerPresets() {
        return stickerPresets;
    }

    public BuildingCard drawCard() {
        return deck.drawCard();
    }

    public boolean hasCardsRemaining() {
        return deck.remainingCards() > 0;
    }

    public int remainingCards() {
        return deck.remainingCards();
    }

    public void resetDeck() {
        deck.reset();
    }

    public boolean isPlacementValid(Tile tile, CityGrid cityGrid, Position position, boolean isFirst) {
        return rules.validatePlacement(tile, cityGrid, position, isFirst);
    }

    public int calculateBonus(CityGrid cityGrid) {
        return bonus.calculateBonus(cityGrid, null, 0);
    }

    public int calculateBonus(CityGrid cityGrid, Game game, int playerIndex) {
        return bonus.calculateBonus(cityGrid, game, playerIndex);
    }

    public void applyStickerPresets(CityGrid cityGrid) {
        if (stickerPresets != null && !stickerPresets.isEmpty()) {
            for (Sticker sticker : stickerPresets) {
                cityGrid.addSticker(sticker);
            }
        }
    }

    public String getEpisodeName() {
        if (id >= 1 && id <= 3)
            return "Chapter 1: The New Land - Episode " + id;
        if (id >= 4 && id <= 6)
            return "Chapter 2: The Churches - Episode " + (id - 3);
        if (id >= 7 && id <= 9)
            return "Chapter 3: The Flood/Gold Fields - Episode " + (id - 6);
        if (id >= 10 && id <= 12)
            return "Chapter 4: The Bandits - Episode " + (id - 9);
        if (id >= 13 && id <= 15)
            return "Chapter 5: The Factories - Episode " + (id - 12);
        if (id >= 16 && id <= 18)
            return "Chapter 6: The Mine - Episode " + (id - 15);
        if (id >= 19 && id <= 21)
            return "Chapter 7: The Railroad - Episode " + (id - 18);
        if (id >= 22 && id <= 24)
            return "Chapter 8: The Palace - Episode " + (id - 21);
        return "Episode " + id;
    }

    public boolean isChapterStart() {
        return (id - 1) % 3 == 0;
    }

    public boolean isChapterEnd() {
        return id % 3 == 0;
    }

    public int getChapterNumber() {
        return ((id - 1) / 3) + 1;
    }

    public int getEpisodeWithinChapter() {
        return ((id - 1) % 3) + 1;
    }

    @Override
    public String toString() {
        return "Episode{id=" + id +
                ", name='" + getEpisodeName() + '\'' +
                ", remainingCards=" + remainingCards() +
                ", stickerCount=" + (stickerPresets != null ? stickerPresets.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Episode episode = (Episode) obj;
        return id == episode.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
