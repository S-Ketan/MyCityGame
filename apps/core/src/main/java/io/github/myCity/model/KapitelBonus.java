package io.github.myCity.model;


 
public class KapitelBonus {
    private final int kapitelId;
    private final String bonusBeschreibung;

    public KapitelBonus(int kapitelId, String bonusBeschreibung) {
        this.kapitelId = kapitelId;
        this.bonusBeschreibung = bonusBeschreibung;
    }

    public int berechneBonusStadtplan(Stadtplan stadtplan) {
        if (stadtplan == null)
            return 0;

        switch (kapitelId) {
            // Chapter 1 - The New Land
            case 1:
                return episode1(stadtplan);
            case 2:
                return episode2(stadtplan);
            case 3:
                return episode3(stadtplan);

            // Chapter 2 - The Churches
            case 4:
                return episode4(stadtplan);
            case 5:
                return episode5(stadtplan);
            case 6:
                return episode6(stadtplan);

            // Chapter 3 - The Flood / Gold Fields
            case 7:
                return episode7(stadtplan);
            case 8:
                return episode8(stadtplan);
            case 9:
                return episode9(stadtplan);

            // Chapter 4 - The Bandits
            case 10:
                return episode10(stadtplan);
            case 11:
                return episode11(stadtplan);
            case 12:
                return episode12(stadtplan);

            // Chapter 5 - The Factories
            case 13:
                return episode13(stadtplan);
            case 14:
                return episode14(stadtplan);
            case 15:
                return episode15(stadtplan);

            // Chapter 6 - The Mine
            case 16:
                return episode16(stadtplan);
            case 17:
                return episode17(stadtplan);
            case 18:
                return episode18(stadtplan);

            // Chapter 7 - The Railroad
            case 19:
                return episode19(stadtplan);
            case 20:
                return episode20(stadtplan);
            case 21:
                return episode21(stadtplan);

            // Chapter 8 - The Palace
            case 22:
                return episode22(stadtplan);
            case 23:
                return episode23(stadtplan);
            case 24:
                return episode24(stadtplan);

            default:
                return 0;
        }
    }

    private boolean istUmzingelt(Stadtplan stadtplan, Position pos) {
        int[][] offsets = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };

        for (int[] offset : offsets) {
            int nx = pos.getX() + offset[0];
            int ny = pos.getY() + offset[1];

            // Out of bounds counts as not surrounded
            if (nx < 0 || nx >= 11 || ny < 0 || ny >= 11) {
                return false;
            }

            Feld feld = stadtplan.getFeld(nx, ny);
            if (feld == null || !feld.istBelegt()) {
                return false;
            }
        }

        return true;
    }

    public int getKapitelId() {
        return kapitelId;
    }

    public String getBonusBeschreibung() {
        return bonusBeschreibung;
    }

    /**
     * Counts how many adjacent neighbors are occupied.
     */
    private int countBelegteNachbarn(Stadtplan stadtplan, Position pos) {
        int count = 0;
        int[][] offsets = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };

        for (int[] offset : offsets) {
            int nx = pos.getX() + offset[0];
            int ny = pos.getY() + offset[1];

            Feld feld = stadtplan.getFeld(nx, ny);
            if (feld != null && feld.istBelegt()) {
                count++;
            }
        }

        return count;
    }

    private int episode1(Stadtplan stadtplan) {
        // bäumeZählen returns +2 per tree, felsenZählen returns -2 per rock
        // Episode 1: +1 per tree uncovered, -1 per rock uncovered
        return (stadtplan.bäumeZählen() + stadtplan.felsenZählen()) / 2;
    }

    private int episode2(Stadtplan stadtplan) {
        // Episode 2: Largest group of same-color buildings = +1 per tile in that group
        // TODO: Implement color group detection
        // This requires tracking building colors (not implemented in Feld yet)
        // For now, return basic tree/rock bonus
        return (stadtplan.bäumeZählen() + stadtplan.felsenZählen()) / 2;
    }

    private int episode3(Stadtplan stadtplan) {
        int bonus = (stadtplan.bäumeZählen() + stadtplan.felsenZählen()) / 2;

        // Check for well sticker and if it's surrounded
        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getTyp() == StickerTyp.STRUKTURSTICKER) {
                if (istUmzingelt(stadtplan, sticker.getPos())) {
                    bonus += 4;
                }
            }
        }

        return bonus;
    }

    /**
     * Episode 4: Each church touching ≥2 building colors = +3
     */
    private int episode4(Stadtplan stadtplan) {
        // TODO: Implement church and color detection
        return 0;
    }

    /*
     * Episode 5: Church adjacent to all 3 colors = +5
     */
    private int episode5(Stadtplan stadtplan) {
        // TODO: Implement church and 3-color detection
        return 0;
    }

    /**
     * Episode 6: Church not adjacent to any building = -3
     */
    private int episode6(Stadtplan stadtplan) {
        // TODO: Implement isolated church detection
        return 0;
    }

    /**
     * Episode 7: +1 per gold field covered
     */
    private int episode7(Stadtplan stadtplan) {
        int bonus = 0;

        // Check all gold field stickers
        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getEffektBeschreibung().contains("gold")) {
                Position pos = sticker.getPos();
                Feld feld = stadtplan.getFeld(pos.getX(), pos.getY());
                if (feld != null && feld.istBelegt()) {
                    bonus += 1; // Gold field is covered
                }
            }
        }

        return bonus;
    }

    /**
     * Episode 8: +3 if all gold fields covered
     */
    private int episode8(Stadtplan stadtplan) {
        int goldFields = 0;
        int coveredGold = 0;

        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getEffektBeschreibung().contains("gold")) {
                goldFields++;
                Position pos = sticker.getPos();
                Feld feld = stadtplan.getFeld(pos.getX(), pos.getY());
                if (feld != null && feld.istBelegt()) {
                    coveredGold++;
                }
            }
        }

        return (goldFields > 0 && goldFields == coveredGold) ? 3 : 0;
    }

    /**
     * Episode 9: +5 if all gold fields covered
     */
    private int episode9(Stadtplan stadtplan) {
        int goldFields = 0;
        int coveredGold = 0;

        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getEffektBeschreibung().contains("gold")) {
                goldFields++;
                Position pos = sticker.getPos();
                Feld feld = stadtplan.getFeld(pos.getX(), pos.getY());
                if (feld != null && feld.istBelegt()) {
                    coveredGold++;
                }
            }
        }

        return (goldFields > 0 && goldFields == coveredGold) ? 5 : 0;
    }

    /**
     * Episode 10: Each surrounded bandit = +2, unsurrounded = -3
     */
    private int episode10(Stadtplan stadtplan) {
        int bonus = 0;

        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getEffektBeschreibung().contains("bandit")) {
                if (istUmzingelt(stadtplan, sticker.getPos())) {
                    bonus += 2; // Arrested
                } else {
                    bonus -= 3; // Free
                }
            }
        }

        return bonus;
    }

    /**
     * Episode 11: +1 additional per arrested bandit (total +3 per arrested)
     */
    private int episode11(Stadtplan stadtplan) {
        int bonus = 0;

        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getEffektBeschreibung().contains("bandit")) {
                if (istUmzingelt(stadtplan, sticker.getPos())) {
                    bonus += 3; // +2 base + 1 bonus
                } else {
                    bonus -= 3;
                }
            }
        }

        return bonus;
    }

    /**
     * Episode 12: +5 if all bandits surrounded
     */
    private int episode12(Stadtplan stadtplan) {
        int bandits = 0;
        int arrested = 0;

        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getEffektBeschreibung().contains("bandit")) {
                bandits++;
                if (istUmzingelt(stadtplan, sticker.getPos())) {
                    arrested++;
                }
            }
        }

        int bonus = 0;

        // Base scoring
        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getEffektBeschreibung().contains("bandit")) {
                if (istUmzingelt(stadtplan, sticker.getPos())) {
                    bonus += 2;
                } else {
                    bonus -= 3;
                }
            }
        }

        // All arrested bonus
        if (bandits > 0 && bandits == arrested) {
            bonus += 5;
        }

        return bonus;
    }

    /**
     * Episode 13: Each factory touching ≥2 buildings = +2
     */
    private int episode13(Stadtplan stadtplan) {
        int bonus = 0;

        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getEffektBeschreibung().contains("factory")) {
                Position pos = sticker.getPos();
                int neighbors = countBelegteNachbarn(stadtplan, pos);
                if (neighbors >= 2) {
                    bonus += 2;
                }
            }
        }

        return bonus;
    }

    /**
     * Episode 14: Factory clusters: size 2=+2, size 3=+4, size 4+=+7
     */
    private int episode14(Stadtplan stadtplan) {
        // TODO: Implement factory cluster detection
        // Requires tracking factory groups
        return 0;
    }

    /**
     * Episode 15: 2 separate factory clusters (each ≥2) = +4
     */
    private int episode15(Stadtplan stadtplan) {
        // TODO: Implement multiple cluster detection
        return 0;
    }

    /**
     * Episode 16: +1 per depth level reached
     */
    private int episode16(Stadtplan stadtplan) {
        // TODO: Track mine depth (requires game state)
        return 0;
    }

    /**
     * Episode 17: Depth 4=+3, depth 6=+5
     */
    private int episode17(Stadtplan stadtplan) {
        // TODO: Track mine depth
        return 0;
    }

    /**
     * Episode 18: Each gold nugget = +2
     */
    private int episode18(Stadtplan stadtplan) {
        // TODO: Track gold nuggets collected
        return 0;
    }

    /**
     * Episode 19: +1 per railroad space covered
     */
    private int episode19(Stadtplan stadtplan) {
        int bonus = 0;

        for (Sticker sticker : stadtplan.getSticker()) {
            if (sticker.getEffektBeschreibung().contains("railroad")) {
                Position pos = sticker.getPos();
                Feld feld = stadtplan.getFeld(pos.getX(), pos.getY());
                if (feld != null && feld.istBelegt()) {
                    bonus += 1;
                }
            }
        }

        return bonus;
    }

    /**
     * Episode 20: All railroad spaces in bottom row = +5
     */
    private int episode20(Stadtplan stadtplan) {
        // TODO: Check bottom row railroad completion
        return 0;
    }

    /**
     * Episode 21: Each railroad branch connected = +3
     */
    private int episode21(Stadtplan stadtplan) {
        // TODO: Track railroad network connectivity
        return 0;
    }

    // ========== CHAPTER 8: THE PALACE ==========

    /**
     * Episode 22: +1 per palace tile attached to red building
     */
    private int episode22(Stadtplan stadtplan) {
        // TODO: Track palace attachment
        return 0;
    }

    /**
     * Episode 23: Palace ≥4 tiles = +5
     */
    private int episode23(Stadtplan stadtplan) {
        // TODO: Count palace size
        return 0;
    }

    /**
     * Episode 24 (FINAL): Most trees=+5, most gold=+5, fewest rocks=+5
     * Note: This requires comparing between players
     */
    private int episode24(Stadtplan stadtplan) {
        // TODO: This requires multi-player comparison
        // Should be handled by Spiel class, not here
        return 0;
    }

    @Override
    public String toString() {
        return "KapitelBonus{" +
                "episode=" + kapitelId +
                ", beschreibung='" + bonusBeschreibung + '\'' +
                '}';
    }
}
