package io.github.myCity.model;

import java.util.List;

public class Kapitel {
    private final int id;
    private final PlättchenDeck deck;
    private final PlatzierungsRegel regeln;
    private final KapitelBonus bonus;
    private final List<Sticker> stickerVorgaben;

    public Kapitel(int id, PlättchenDeck deck, PlatzierungsRegel regeln,
            KapitelBonus bonus, List<Sticker> stickerVorgaben) {
        this.id = id;
        this.deck = deck;
        this.regeln = regeln;
        this.bonus = bonus;
        this.stickerVorgaben = stickerVorgaben;
    }

    public int getId() {
        return id;
    }

    public PlättchenDeck getDeck() {
        return deck;
    }

    public PlatzierungsRegel getRegeln() {
        return regeln;
    }

    public KapitelBonus getBonus() {
        return bonus;
    }

    public List<Sticker> getStickerVorgaben() {
        return stickerVorgaben;
    }

    public Gebäudekarte zieheKarte() {   
        return deck.zieheKarte();
    }

    public boolean hatNochKarten() {
        return deck.verbleibendeKarten() > 0;
    }

    public int verbleibendeKarten() {
        return deck.verbleibendeKarten();
    }

    public boolean istPlatzierungGültig(Plättchen plättchen, Stadtplan stadtplan,
            Position pos, boolean erstes) {
        return regeln.prüfenPlättchen(plättchen, stadtplan, pos, erstes);
    }

    public int berechneBonus(Stadtplan stadtplan) {
        return bonus.berechneBonusStadtplan(stadtplan, null, 0);
    }

    public int berechneBonus(Stadtplan stadtplan, Spiel spiel, int playerIndex) {
        return bonus.berechneBonusStadtplan(stadtplan, spiel, playerIndex);
    }

    public void applyStickerVorgaben(Stadtplan stadtplan) {
        if (stickerVorgaben != null && !stickerVorgaben.isEmpty()) {
            for (Sticker sticker : stickerVorgaben) {
                stadtplan.addSticker(sticker);
            }
        }
    }

    public String getEpisodeName() {
        // Map episode IDs to chapter names
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
        return "Kapitel{" +
                "id=" + id +
                ", name='" + getEpisodeName() + '\'' +
                ", verbleibendeKarten=" + verbleibendeKarten() +
                ", stickerAnzahl=" + (stickerVorgaben != null ? stickerVorgaben.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Kapitel kapitel = (Kapitel) obj;
        return id == kapitel.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
