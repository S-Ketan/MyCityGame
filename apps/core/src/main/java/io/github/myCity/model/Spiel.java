package io.github.myCity.model;

import java.util.ArrayList;
import java.util.List;

public class Spiel {

    // ========== ATTRIBUTES ==========

    /**
     * The current episode being played (1-24).
     */
    private Kapitel aktuellesKapitel;
    private final Spieler[] spieler;
    private Spielstand spielstand;
    private int aktuellerSpielerIndex;
    private int aktuellesKapitelNummer;
    private boolean[] erstesTile;
    /**
     * Mine depth for episodes 16-18.
     * Gold nuggets collected in mine (episode 18).
     * Tracks how deep into the mine the player has progressed.
     */
    private int mineDepthPlayer1 = 0;
    private int mineDepthPlayer2 = 0;
    private int goldNuggetsPlayer1 = 0;
    private int goldNuggetsPlayer2 = 0;
    /**
     * Palace size tracking for episodes 22-23.
     * Counts number of palace tiles placed.
     */
    private int palaceSizePlayer1 = 0;
    private int palaceSizePlayer2 = 0;

    // ========== CONSTRUCTOR ==========

    /**
     * Creates a new game instance.
     */
    public Spiel() {
        this.spieler = new Spieler[2];
        this.aktuellerSpielerIndex = 0;
        this.aktuellesKapitelNummer = 1;
        this.erstesTile = new boolean[] { true, true };
    }

    // ========== GAME INITIALIZATION ==========

    /**
     * Starts a new game from the beginning.
     */
    public void neuesSpielStarten(String spieler1Name, String spieler2Name) {
        // Step 1: Create the two players
        spieler[0] = new Spieler(spieler1Name);
        spieler[1] = new Spieler(spieler2Name);

        System.out.println("🎮 New game started!");
        System.out.println("👤 Player 1: " + spieler1Name);
        System.out.println("👤 Player 2: " + spieler2Name);

        // Step 2: Reset scores and game state
        aktuellerSpielerIndex = 0;
        aktuellesKapitelNummer = 1;
        erstesTile = new boolean[] { true, true };

        // Step 3: Load the first episode
        kapitelLaden(1);

        // Step 4: Create initial game state
        Spielerstand spieler1Stand = new Spielerstand(spieler[0].getStadtplan(), spieler[0].getPunktzahl());
        Spielerstand spieler2Stand = new Spielerstand(spieler[1].getStadtplan(), spieler[1].getPunktzahl());
        spielstand = new Spielstand(1, spieler1Stand, spieler2Stand);

        System.out.println("✅ Ready to play Episode 1!\n");
    }

    /**
     * Loads a specific episode (1-24).
     */
    public void kapitelLaden(int episodeNummer) {
        kapitelLaden(episodeNummer, true);
    }

    /**
     * Loads a specific episode (1-24).
     */
    public void kapitelLaden(int episodeNummer, boolean resetPlayerIndex) {
        if (episodeNummer < 1 || episodeNummer > 24) {
            throw new IllegalArgumentException("Episode must be between 1 and 24");
        }

        System.out.println("📖 Loading Episode " + episodeNummer + "...");

        // Step 1: Create the tile deck
        // TODO: In a real implementation, each episode would have different tiles
        // For now, we create a generic deck
        List<Gebäudekarte> karten = erstelleStandardDeck();
        PlättchenDeck deck = new PlättchenDeck(karten);

        // Step 2: Create placement rules
        PlatzierungsRegel regeln = new PlatzierungsRegel();

        // Step 3: Create bonus calculator with episode-specific description
        String bonusBeschreibung = getBonusBeschreibung(episodeNummer);
        KapitelBonus bonus = new KapitelBonus(episodeNummer, bonusBeschreibung);

        // Step 4: Create episode-specific stickers
        List<Sticker> stickers = erstelleStickerFuerEpisode(episodeNummer);

        // Step 5: Create the Kapitel object
        aktuellesKapitel = new Kapitel(episodeNummer, deck, regeln, bonus, stickers);
        aktuellesKapitelNummer = episodeNummer;

        // Step 6: Apply stickers to both players' grids
        if (spieler[0] != null && spieler[1] != null) {
            aktuellesKapitel.applyStickerVorgaben(spieler[0].getStadtplan());
            aktuellesKapitel.applyStickerVorgaben(spieler[1].getStadtplan());
        }

        // Step 7: Reset for new episode
        erstesTile = new boolean[] { true, true };
        if (resetPlayerIndex) {
            aktuellerSpielerIndex = 0;
        }

        // Step 8: Calculate initial scores for both players
        if (spieler[0] != null && spieler[1] != null) {
            refreshScore(0);
            refreshScore(1);
        }

        System.out.println("✅ Episode " + episodeNummer + " loaded: " +
                aktuellesKapitel.getEpisodeName());
    }

    // ========== GAME FLOW ==========

    public void kapitelSpielen() {
        System.out.println("\n🎲 Starting " + aktuellesKapitel.getEpisodeName());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Main game loop - continues until deck is empty
        int turnNumber = 1;

        while (aktuellesKapitel.hatNochKarten()) {
            System.out.println("Turn " + turnNumber + ":");

            // Draw a card (same card for both players!)
            Gebäudekarte karte = aktuellesKapitel.zieheKarte();
            if (karte == null)
                break;

            Plättchen tile = karte.getPlättchen();
            System.out.println("  🎴 Drew tile #" + tile.getId());

            // Both players place this tile
            for (int i = 0; i < 2; i++) {
                Spieler currentPlayer = spieler[i];
                System.out.println("  👤 " + currentPlayer.getName() + "'s turn");

                // In a real game, the player would choose position via GUI
                // Here we just note that the tile should be placed
                // TODO: This will be replaced by GUI interaction

                System.out.println("     (Waiting for " + currentPlayer.getName() +
                        " to place tile...)");
            }

            turnNumber++;
            System.out.println();
        }

        System.out.println("🏁 Episode complete! Calculating bonuses...\n");

        // Calculate and apply bonuses
        for (int i = 0; i < 2; i++) {
            Spieler player = spieler[i];
            int bonus = aktuellesKapitel.berechneBonus(player.getStadtplan());

            System.out.println("🎁 " + player.getName() + "'s bonus: " +
                    (bonus >= 0 ? "+" : "") + bonus + " points");

            player.setPunktzahl(player.getPunktzahl() + bonus);
            System.out.println("   Total score: " + player.getPunktzahl());
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Processes a tile placement by the current player.
     * 
     * This method would be called from the GUI when a player places a tile.
     * 
     * @param tile   the tile to place
     * @param pos    the position to place it at
     * @param rotate whether to rotate the tile before placing
     * @return true if placement was successful, false if invalid
     */
    public boolean platziereAktuellesTile(Plättchen tile, Position pos, boolean rotate) {
        Spieler currentPlayer = getCurrentPlayer();

        // Step 1: Optionally rotate
        if (rotate) {
            tile.drehen();
        }

        // Step 2: Check if placement is valid
        boolean isValid = aktuellesKapitel.istPlatzierungGültig(
                tile,
                currentPlayer.getStadtplan(),
            pos,
            erstesTile[aktuellerSpielerIndex]);

        if (!isValid) {
            System.out.println("❌ Invalid placement!");
            return false;
        }

        // Step 3: Place the tile
        currentPlayer.plaettchenLegen(tile, pos);
        System.out.println("✅ " + currentPlayer.getName() + " placed tile at " + pos);

        // Update score after placement
        refreshScore(aktuellerSpielerIndex);

        // Step 4: No longer the first tile
        erstesTile[aktuellerSpielerIndex] = false;

        // Step 5: Switch to next player
        wechselSpieler();

        return true;
    }

    /**
     * Current player skips placing the tile.
     * Each skipped building results in -1 point.
     */
    public void ueberspringe() {
        int currentIndex = aktuellerSpielerIndex;
        Spieler currentPlayer = getCurrentPlayer();
        currentPlayer.plaettchenUeberspringen();

        // Refresh score to reflect the skipped building penalty
        refreshScore(currentIndex);

        wechselSpieler();
    }

    /**
     * Recalculate and update the score for the given player.
     *
     * Scoring rules:
     * - Start with 10 points
     * - +1 for each visible tree (Baum) on light green space
     * - -1 for each visible rock (Fels) on light green space
     * - -1 for each empty light green space (WIESE not covered)
     * - -1 for each skipped building
     * - Score is clamped between 0 and 50
     */
    public void refreshScore(int playerIndex) {
        if (playerIndex < 0 || playerIndex >= spieler.length || spieler[playerIndex] == null) {
            return;
        }
        Spieler p = spieler[playerIndex];
        Stadtplan plan = p.getStadtplan();

        // Base score: 10 points
        int score = 10;

        // +1 per visible tree
        score += plan.bäumeZählen();

        // -1 per visible rock (felsenZählen returns negative values)
        score += plan.felsenZählen();

        // -1 per empty light green space (leereWiesenZählen returns negative values)
        score += plan.leereWiesenZählen();

        // -1 per skipped building
        score -= p.getÜbersprungeneGebäude();

        // Set score (will be clamped to 0-50 by Spieler.setPunktzahl)
        p.setPunktzahl(score);
    }

    /**
     * Switches to the other player.
     */
    public void wechselSpieler() {
        aktuellerSpielerIndex = (aktuellerSpielerIndex + 1) % 2;
    }

    /**
     * Resets the deck for the next player without creating a new chapter.
     * The same shuffled order is preserved so player 2 gets the same tiles.
     */
    public void resetDeckForNextPlayer() {
        aktuellesKapitel.resetDeck();
        erstesTile[aktuellerSpielerIndex] = true;
    }

    // ========== SCORING ==========

    public void endpunkteBerechnen() {
        System.out.println("\n🏆 FINAL RESULTS 🏆");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        int score1 = spieler[0].getPunktzahl();
        int score2 = spieler[1].getPunktzahl();

        System.out.println("👤 " + spieler[0].getName() + ": " + score1 + " points");
        System.out.println("👤 " + spieler[1].getName() + ": " + score2 + " points");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (score1 > score2) {
            System.out.println("🎉 " + spieler[0].getName() + " WINS!");
        } else if (score2 > score1) {
            System.out.println("🎉 " + spieler[1].getName() + " WINS!");
        } else {
            System.out.println("🤝 It's a TIE!");
        }

        System.out.println();
    }

    /**
     * Calculates and awards progress points at the end of a chapter.
     * - Winner gets 2 progress points
     * - Loser gets 1 progress point
     * - If tie in score, use tiebreaker: player with fewer empty light green spaces
     *   in top row wins. If tied there, check second row, etc.
     * - Bonus: +1 progress point if player reached 50 scoring points
     *
     * @return int array with [player1ProgressAwarded, player2ProgressAwarded]
     */
    public int[] awardProgressPoints() {
        int[] awarded = new int[2];
        int score1 = spieler[0].getPunktzahl();
        int score2 = spieler[1].getPunktzahl();

        // Determine winner and award base progress points
        if (score1 > score2) {
            // Player 1 wins
            awarded[0] = 2;
            awarded[1] = 1;
        } else if (score2 > score1) {
            // Player 2 wins
            awarded[0] = 1;
            awarded[1] = 2;
        } else {
            // Tie in score - use tiebreaker rule
            int tiebreakResult = resolveTiebreaker();
            if (tiebreakResult > 0) {
                // Player 1 wins tiebreaker (fewer empty spaces)
                awarded[0] = 2;
                awarded[1] = 1;
            } else if (tiebreakResult < 0) {
                // Player 2 wins tiebreaker (fewer empty spaces)
                awarded[0] = 1;
                awarded[1] = 2;
            } else {
                // Complete tie - both get 1 point
                awarded[0] = 1;
                awarded[1] = 1;
            }
        }

        // Bonus: +1 if player reached 50 scoring points
        if (score1 >= 50) {
            awarded[0] += 1;
        }
        if (score2 >= 50) {
            awarded[1] += 1;
        }

        // Apply progress points to players
        spieler[0].addProgressPoints(awarded[0]);
        spieler[1].addProgressPoints(awarded[1]);

        System.out.println("📊 Progress points awarded:");
        System.out.println("   " + spieler[0].getName() + ": +" + awarded[0] +
                         " (Total: " + spieler[0].getProgressPoints() + "/100)");
        System.out.println("   " + spieler[1].getName() + ": +" + awarded[1] +
                         " (Total: " + spieler[1].getProgressPoints() + "/100)");

        return awarded;
    }

    /**
     * Resolves a tie by comparing empty light green spaces row by row from top to bottom.
     * Player with fewer empty spaces in the top row wins.
     * If tied, check second row, etc.
     *
     * @return positive if player 1 wins, negative if player 2 wins, 0 if complete tie
     */
    private int resolveTiebreaker() {
        int[] player1EmptyPerRow = spieler[0].getStadtplan().countEmptyWiesePerRow();
        int[] player2EmptyPerRow = spieler[1].getStadtplan().countEmptyWiesePerRow();

        // Compare row by row from top (index 0) to bottom (index 10)
        for (int row = 0; row < 11; row++) {
            if (player1EmptyPerRow[row] < player2EmptyPerRow[row]) {
                // Player 1 has fewer empty spaces in this row - Player 1 wins
                System.out.println("🎯 Tiebreaker: " + spieler[0].getName() + " wins (fewer empty spaces in row " + (row + 1) + ")");
                return 1;
            } else if (player2EmptyPerRow[row] < player1EmptyPerRow[row]) {
                // Player 2 has fewer empty spaces in this row - Player 2 wins
                System.out.println("🎯 Tiebreaker: " + spieler[1].getName() + " wins (fewer empty spaces in row " + (row + 1) + ")");
                return -1;
            }
            // If equal, continue to next row
        }

        // Complete tie - all rows have same number of empty spaces
        System.out.println("🎯 Tiebreaker: Complete tie - all rows equal");
        return 0;
    }

    /**
     * Checks if any player has won the entire game (reached 100 progress points).
     * @return the winning player, or null if no winner yet
     */
    public Spieler checkForGameWinner() {
        if (spieler[0].hasWonGame()) {
            return spieler[0];
        }
        if (spieler[1].hasWonGame()) {
            return spieler[1];
        }
        return null;
    }

    // ========== SAVE/LOAD ==========
    public void resetEpisodeTracking() {
        mineDepthPlayer1 = 0;
        mineDepthPlayer2 = 0;
        palaceSizePlayer1 = 0;
        palaceSizePlayer2 = 0;
        goldNuggetsPlayer1 = 0;
        goldNuggetsPlayer2 = 0;
    }

    public void spielstandSpeichern() {
        // Create Spielerstand snapshots from current Spieler objects
        Spielerstand spieler1Stand = new Spielerstand(spieler[0].getStadtplan(), spieler[0].getPunktzahl());
        Spielerstand spieler2Stand = new Spielerstand(spieler[1].getStadtplan(), spieler[1].getPunktzahl());

        // Create new Spielstand with current state
        spielstand = new Spielstand(aktuellesKapitelNummer, spieler1Stand, spieler2Stand);

        // TODO: Actually serialize to file/database
        System.out.println("💾 Game saved at Episode " + aktuellesKapitelNummer);
    }

    /**
     * Loads a previously saved game state.
     * 
     * According to diagram: spielstandLaden()
     */
    public void spielstandLaden() {
        // TODO: Actually deserialize from file/database

        if (spielstand == null) {
            System.out.println("❌ No saved game found!");
            return;
        }

        // Restore game state
        // Note: Spielerstand only has stadtplan and punktzahl, not player names
        // We need to keep the existing player objects and restore their state
        spieler[0].setPunktzahl(spielstand.getSpieler1().getPunktzahl());
        spieler[1].setPunktzahl(spielstand.getSpieler2().getPunktzahl());
        // TODO: Also restore stadtplan state
        aktuellesKapitelNummer = spielstand.getKapitelNummer();

        // Load the saved episode
        kapitelLaden(aktuellesKapitelNummer);

        System.out.println("📂 Game loaded from Episode " + aktuellesKapitelNummer);
    }

    // ========== HELPER METHODS ==========

    /**
     * Creates a standard deck of 24 tiles.
     * TODO: Each episode should have unique tiles based on the actual game.
     */
    private List<Gebäudekarte> erstelleStandardDeck() {
        List<Gebäudekarte> karten = new ArrayList<>();
        List<Plättchen> plättchenListe = Plättchen.create24Plättchen();

        for (Plättchen plättchen : plättchenListe) {
            karten.add(new Gebäudekarte(plättchen));
        }

        return karten;
    }



    /**
     * Gets the bonus description for each episode.
     */
    private String getBonusBeschreibung(int episode) {
        // Descriptions from the actual game rules
        switch (episode) {
            case 1:
                return "+1 per tree, -1 per rock";
            case 2:
                return "Largest color group = +1 per tile";
            case 3:
                return "+4 if well surrounded, trees/rocks normal";
            case 10:
                return "Surrounded bandit = +2, free = -3";
            // ... add all 24 descriptions
            default:
                return "Episode " + episode + " bonus";
        }
    }

    /**
     * Creates episode-specific stickers.
     */
    private List<Sticker> erstelleStickerFuerEpisode(int episode) {
        List<Sticker> stickers = new ArrayList<>();

        // Example: Episode 10 has bandits
        if (episode >= 10 && episode <= 12) {
            stickers.add(new Sticker(StickerTyp.STRUKTURSTICKER,
                    new Position(3, 5), "bandit"));
            stickers.add(new Sticker(StickerTyp.STRUKTURSTICKER,
                    new Position(7, 8), "bandit"));
        }

        // TODO: Add stickers for all episodes based on actual game

        return stickers;
    }

    public void incrementMineDepth(int playerIndex) {
        if (playerIndex == 0) {
            mineDepthPlayer1++;
        } else {
            mineDepthPlayer2++;
        }
    }

    public void addGoldNugget(int playerIndex) {
        if (playerIndex == 0) {
            goldNuggetsPlayer1++;
        } else {
            goldNuggetsPlayer2++;
        }
    }

    public void incrementPalaceSize(int playerIndex) {
        if (playerIndex == 0) {
            palaceSizePlayer1++;
        } else {
            palaceSizePlayer2++;
        }
    }
    // ========== GETTERS ==========

    public Kapitel getAktuellesKapitel() {
        return aktuellesKapitel;
    }

    public Spieler[] getSpieler() {
        return spieler;
    }

    public Spieler getCurrentPlayer() {
        return spieler[aktuellerSpielerIndex];
    }

    public int getAktuellerSpielerIndex() {
        return aktuellerSpielerIndex;
    }

    public int getAktuellesKapitelNummer() {
        return aktuellesKapitelNummer;
    }

    public Spielstand getSpielstand() {
        return spielstand;
    }

    public int getMineDepth(int playerIndex) {
        return (playerIndex == 0) ? mineDepthPlayer1 : mineDepthPlayer2;
    }

    public int getGoldNuggets(int playerIndex) {
        return (playerIndex == 0) ? goldNuggetsPlayer1 : goldNuggetsPlayer2;
    }

    public int getPalaceSize(int playerIndex) {
        return (playerIndex == 0) ? palaceSizePlayer1 : palaceSizePlayer2;
    }
}