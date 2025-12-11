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
    private boolean erstesTile;
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
        this.erstesTile = true;
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
        erstesTile = true;

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
        erstesTile = true;
        aktuellerSpielerIndex = 0;

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
                erstesTile);

        if (!isValid) {
            System.out.println("❌ Invalid placement!");
            return false;
        }

        // Step 3: Place the tile
        currentPlayer.plaettchenLegen(tile, pos);
        System.out.println("✅ " + currentPlayer.getName() + " placed tile at " + pos);

        // Step 4: No longer the first tile
        erstesTile = false;

        // Step 5: Switch to next player
        wechselSpieler();

        return true;
    }

    /**
     * Current player skips placing the tile.
     */
    public void ueberspringe() {
        Spieler currentPlayer = getCurrentPlayer();
        currentPlayer.plaettchenUeberspringen();

        // TODO: Apply skip penalty based on episode rules

        wechselSpieler();
    }

    /**
     * Switches to the other player.
     */
    private void wechselSpieler() {
        aktuellerSpielerIndex = (aktuellerSpielerIndex + 1) % 2;
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

        // Create 24 different tiles with various shapes
        for (int i = 1; i <= 24; i++) {
            boolean[][] form = erstelleTileForm(i);
            Plättchen tile = new Plättchen(i, form);
            karten.add(new Gebäudekarte(tile));
        }

        return karten;
    }

    /**
     * Creates a tile shape based on ID.
     * TODO: Use actual tile shapes from the game.
     */
    private boolean[][] erstelleTileForm(int id) {
        // Simple variety of shapes
        switch (id % 5) {
            case 0:
                return new boolean[][] { { true, true }, { true, false } };
            case 1:
                return new boolean[][] { { true, true }, { false, true } };
            case 2:
                return new boolean[][] { { true }, { true }, { true } };
            case 3:
                return new boolean[][] { { true, true, true } };
            default:
                return new boolean[][] { { true, true }, { true, true } };
        }
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