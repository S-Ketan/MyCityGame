package io.github.myCity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game controller class.
 */
public class Game {

    private Episode currentEpisode;
    private final Player[] players;
    private GameState gameState;
    private int currentPlayerIndex;
    private int currentEpisodeNumber;
    private boolean[] isFirstTile;

    private int mineDepthPlayer1 = 0;
    private int mineDepthPlayer2 = 0;
    private int goldNuggetsPlayer1 = 0;
    private int goldNuggetsPlayer2 = 0;
    private int palaceSizePlayer1 = 0;
    private int palaceSizePlayer2 = 0;

    public Game() {
        this.players = new Player[2];
        this.currentPlayerIndex = 0;
        this.currentEpisodeNumber = 1;
        this.isFirstTile = new boolean[] { true, true };
    }

    public void startNewGame(String player1Name, String player2Name) {
        players[0] = new Player(player1Name);
        players[1] = new Player(player2Name);

        currentPlayerIndex = 0;
        currentEpisodeNumber = 1;
        isFirstTile = new boolean[] { true, true };

        loadEpisode(1);

        PlayerState player1State = new PlayerState(players[0].getCityGrid(), players[0].getScore());
        PlayerState player2State = new PlayerState(players[1].getCityGrid(), players[1].getScore());
        gameState = new GameState(1, player1State, player2State);
    }

    public void loadEpisode(int episodeNumber) {
        loadEpisode(episodeNumber, true);
    }

    public void loadEpisode(int episodeNumber, boolean resetPlayerIndex) {
        if (episodeNumber < 1 || episodeNumber > 24) {
            throw new IllegalArgumentException("Episode must be between 1 and 24");
        }

        List<BuildingCard> cards = createStandardDeck();
        TileDeck deck = new TileDeck(cards);

        PlacementRule rules = new PlacementRule();
        String bonusDescription = getBonusDescription(episodeNumber);
        EpisodeBonus bonus = new EpisodeBonus(episodeNumber, bonusDescription);
        List<Sticker> stickers = createStickersForEpisode(episodeNumber);

        currentEpisode = new Episode(episodeNumber, deck, rules, bonus, stickers);
        currentEpisodeNumber = episodeNumber;

        if (players[0] != null && players[1] != null) {
            currentEpisode.applyStickerPresets(players[0].getCityGrid());
            currentEpisode.applyStickerPresets(players[1].getCityGrid());
        }

        isFirstTile = new boolean[] { true, true };
        if (resetPlayerIndex) {
            currentPlayerIndex = 0;
        }

        if (players[0] != null && players[1] != null) {
            refreshScore(0);
            refreshScore(1);
        }
    }

    public void playEpisode() {
        int turnNumber = 1;

        while (currentEpisode.hasCardsRemaining()) {
            BuildingCard card = currentEpisode.drawCard();
            if (card == null) break;

            Tile tile = card.getTile();

            for (int i = 0; i < 2; i++) {
                Player currentPlayer = players[i];
                // GUI interaction would happen here
            }

            turnNumber++;
        }

        for (int i = 0; i < 2; i++) {
            Player player = players[i];
            int bonus = currentEpisode.calculateBonus(player.getCityGrid());
            player.setScore(player.getScore() + bonus);
        }
    }

    public boolean placeCurrentTile(Tile tile, Position position, boolean rotate) {
        Player currentPlayer = getCurrentPlayer();

        if (rotate) {
            tile.rotate();
        }

        boolean isValid = currentEpisode.isPlacementValid(
                tile,
                currentPlayer.getCityGrid(),
                position,
                isFirstTile[currentPlayerIndex]);

        if (!isValid) {
            return false;
        }

        currentPlayer.placeTile(tile, position);
        refreshScore(currentPlayerIndex);
        isFirstTile[currentPlayerIndex] = false;
        switchPlayer();

        return true;
    }

    public void skipTile() {
        int currentIndex = currentPlayerIndex;
        Player currentPlayer = getCurrentPlayer();
        currentPlayer.skipTile();
        refreshScore(currentIndex);
        switchPlayer();
    }

    /**
     * Recalculates and updates the score for the given player.
     */
    public void refreshScore(int playerIndex) {
        if (playerIndex < 0 || playerIndex >= players.length || players[playerIndex] == null) {
            return;
        }
        Player p = players[playerIndex];
        CityGrid grid = p.getCityGrid();

        int score = 10;
        score += grid.countTrees();
        score += grid.countRocks();
        score += grid.countEmptyMeadows();
        score -= p.getSkippedBuildings();

        p.setScore(score);
    }

    public void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 2;
    }

    public void resetDeckForNextPlayer() {
        currentEpisode.resetDeck();
        isFirstTile[currentPlayerIndex] = true;
    }

    public void calculateFinalScores() {
        int score1 = players[0].getScore();
        int score2 = players[1].getScore();

        if (score1 > score2) {
            // Player 1 wins
        } else if (score2 > score1) {
            // Player 2 wins
        } else {
            // Tie
        }
    }

    public int[] awardProgressPoints() {
        int[] awarded = new int[2];
        int score1 = players[0].getScore();
        int score2 = players[1].getScore();

        if (score1 > score2) {
            awarded[0] = 2;
            awarded[1] = 1;
        } else if (score2 > score1) {
            awarded[0] = 1;
            awarded[1] = 2;
        } else {
            int tiebreakResult = resolveTiebreaker();
            if (tiebreakResult > 0) {
                awarded[0] = 2;
                awarded[1] = 1;
            } else if (tiebreakResult < 0) {
                awarded[0] = 1;
                awarded[1] = 2;
            } else {
                awarded[0] = 1;
                awarded[1] = 1;
            }
        }

        if (score1 >= 50) {
            awarded[0] += 1;
        }
        if (score2 >= 50) {
            awarded[1] += 1;
        }

        players[0].addProgressPoints(awarded[0]);
        players[1].addProgressPoints(awarded[1]);

        return awarded;
    }

    private int resolveTiebreaker() {
        int[] player1EmptyPerRow = players[0].getCityGrid().countEmptyMeadowsPerRow();
        int[] player2EmptyPerRow = players[1].getCityGrid().countEmptyMeadowsPerRow();

        for (int row = 0; row < 11; row++) {
            if (player1EmptyPerRow[row] < player2EmptyPerRow[row]) {
                return 1;
            } else if (player2EmptyPerRow[row] < player1EmptyPerRow[row]) {
                return -1;
            }
        }

        return 0;
    }

    public Player checkForGameWinner() {
        if (players[0].hasWonGame()) {
            return players[0];
        }
        if (players[1].hasWonGame()) {
            return players[1];
        }
        return null;
    }

    public void resetEpisodeTracking() {
        mineDepthPlayer1 = 0;
        mineDepthPlayer2 = 0;
        palaceSizePlayer1 = 0;
        palaceSizePlayer2 = 0;
        goldNuggetsPlayer1 = 0;
        goldNuggetsPlayer2 = 0;
    }

    public void saveGameState() {
        PlayerState player1State = new PlayerState(players[0].getCityGrid(), players[0].getScore());
        PlayerState player2State = new PlayerState(players[1].getCityGrid(), players[1].getScore());
        gameState = new GameState(currentEpisodeNumber, player1State, player2State);
    }

    public void loadGameState() {
        if (gameState == null) {
            return;
        }

        players[0].setScore(gameState.getPlayer1().getScore());
        players[1].setScore(gameState.getPlayer2().getScore());
        currentEpisodeNumber = gameState.getEpisodeNumber();

        loadEpisode(currentEpisodeNumber);
    }

    private List<BuildingCard> createStandardDeck() {
        List<BuildingCard> cards = new ArrayList<>();
        List<Tile> tiles = Tile.createFullTileSet();

        for (Tile tile : tiles) {
            cards.add(new BuildingCard(tile));
        }

        return cards;
    }

    private String getBonusDescription(int episode) {
        switch (episode) {
            case 1:
                return "+1 per tree, -1 per rock";
            case 2:
                return "Largest color group = +1 per tile";
            case 3:
                return "+4 if well surrounded, trees/rocks normal";
            case 10:
                return "Surrounded bandit = +2, free = -3";
            default:
                return "Episode " + episode + " bonus";
        }
    }

    private List<Sticker> createStickersForEpisode(int episode) {
        List<Sticker> stickers = new ArrayList<>();

        if (episode >= 10 && episode <= 12) {
            stickers.add(new Sticker(StickerType.STRUCTURE_STICKER,
                    new Position(3, 5), "bandit"));
            stickers.add(new Sticker(StickerType.STRUCTURE_STICKER,
                    new Position(7, 8), "bandit"));
        }

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

    public Episode getCurrentEpisode() {
        return currentEpisode;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public int getCurrentEpisodeNumber() {
        return currentEpisodeNumber;
    }

    public GameState getGameState() {
        return gameState;
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
