package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
    }

    @Test
    void testConstructor() {
        assertNotNull(game);
        assertEquals(0, game.getCurrentPlayerIndex());
        assertEquals(1, game.getCurrentEpisodeNumber());
        assertNull(game.getCurrentEpisode());
    }

    @Test
    void testStartNewGame() {
        game.startNewGame("Alice", "Bob");

        Player[] players = game.getPlayers();
        assertNotNull(players[0]);
        assertNotNull(players[1]);
        assertEquals("Alice", players[0].getName());
        assertEquals("Bob", players[1].getName());
        assertEquals(0, game.getCurrentPlayerIndex());
        assertEquals(1, game.getCurrentEpisodeNumber());
        assertNotNull(game.getCurrentEpisode());
        assertNotNull(game.getGameState());
    }

    @Test
    void testLoadEpisode() {
        game.startNewGame("Alice", "Bob");
        game.loadEpisode(5);

        assertEquals(5, game.getCurrentEpisodeNumber());
        assertNotNull(game.getCurrentEpisode());
    }

    @Test
    void testLoadEpisodeInvalidLow() {
        game.startNewGame("Alice", "Bob");
        assertThrows(IllegalArgumentException.class, () -> game.loadEpisode(0));
    }

    @Test
    void testLoadEpisodeInvalidHigh() {
        game.startNewGame("Alice", "Bob");
        assertThrows(IllegalArgumentException.class, () -> game.loadEpisode(25));
    }

    @Test
    void testLoadEpisodeWithoutResetPlayerIndex() {
        game.startNewGame("Alice", "Bob");
        game.switchPlayer();
        assertEquals(1, game.getCurrentPlayerIndex());

        game.loadEpisode(3, false);

        assertEquals(1, game.getCurrentPlayerIndex());
        assertEquals(3, game.getCurrentEpisodeNumber());
    }

    @Test
    void testGetCurrentPlayer() {
        game.startNewGame("Alice", "Bob");

        Player currentPlayer = game.getCurrentPlayer();
        assertEquals("Alice", currentPlayer.getName());

        game.switchPlayer();
        currentPlayer = game.getCurrentPlayer();
        assertEquals("Bob", currentPlayer.getName());
    }

    @Test
    void testSwitchPlayer() {
        game.startNewGame("Alice", "Bob");

        assertEquals(0, game.getCurrentPlayerIndex());
        game.switchPlayer();
        assertEquals(1, game.getCurrentPlayerIndex());
        game.switchPlayer();
        assertEquals(0, game.getCurrentPlayerIndex());
    }

    @Test
    void testSkipTile() {
        game.startNewGame("Alice", "Bob");

        Player player1 = game.getPlayers()[0];
        assertEquals(0, player1.getSkippedBuildings());

        game.skipTile();

        assertEquals(1, player1.getSkippedBuildings());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    void testPlaceCurrentTileValid() {
        game.startNewGame("Alice", "Bob");

        Tile tile = new Tile(1, new boolean[][]{{true}});
        // Position (2, 0) has a river
        Position position = new Position(2, 0);

        boolean result = game.placeCurrentTile(tile, position, false);

        assertTrue(result);
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    void testPlaceCurrentTileInvalid() {
        game.startNewGame("Alice", "Bob");

        Tile tile = new Tile(1, new boolean[][]{{true}});
        Position invalidPosition = new Position(-1, 5);

        boolean result = game.placeCurrentTile(tile, invalidPosition, false);

        assertFalse(result);
        assertEquals(0, game.getCurrentPlayerIndex());
    }

    @Test
    void testPlaceCurrentTileWithRotation() {
        game.startNewGame("Alice", "Bob");

        // Use a single-cell tile to avoid river crossing issues
        Tile tile = new Tile(1, new boolean[][]{{true}});
        // Position (2, 0) has a river
        Position position = new Position(2, 0);

        boolean result = game.placeCurrentTile(tile, position, true);

        assertTrue(result);
        // Single cell tile rotation doesn't change the shape, but rotation is tracked
        assertEquals(90, tile.getRotation());
    }

    @Test
    void testRefreshScore() {
        game.startNewGame("Alice", "Bob");

        game.refreshScore(0);

        Player player1 = game.getPlayers()[0];
        assertTrue(player1.getScore() >= 0);
    }

    @Test
    void testRefreshScoreInvalidIndex() {
        game.startNewGame("Alice", "Bob");
        game.refreshScore(-1);
        game.refreshScore(5);
    }

    @Test
    void testResetDeckForNextPlayer() {
        game.startNewGame("Alice", "Bob");

        game.getCurrentEpisode().drawCard();
        game.getCurrentEpisode().drawCard();

        int remaining = game.getCurrentEpisode().remainingCards();
        game.resetDeckForNextPlayer();

        assertTrue(game.getCurrentEpisode().remainingCards() > remaining);
    }

    @Test
    void testAwardProgressPointsPlayer1Wins() {
        game.startNewGame("Alice", "Bob");

        Player player1 = game.getPlayers()[0];
        Player player2 = game.getPlayers()[1];

        player1.setScore(30);
        player2.setScore(20);

        int[] awarded = game.awardProgressPoints();

        assertEquals(2, awarded[0]);
        assertEquals(1, awarded[1]);
        assertTrue(player1.getProgressPoints() >= 2);
        assertTrue(player2.getProgressPoints() >= 1);
    }

    @Test
    void testAwardProgressPointsPlayer2Wins() {
        game.startNewGame("Alice", "Bob");

        Player player1 = game.getPlayers()[0];
        Player player2 = game.getPlayers()[1];

        player1.setScore(15);
        player2.setScore(25);

        int[] awarded = game.awardProgressPoints();

        assertEquals(1, awarded[0]);
        assertEquals(2, awarded[1]);
    }

    @Test
    void testAwardProgressPointsTie() {
        game.startNewGame("Alice", "Bob");

        Player player1 = game.getPlayers()[0];
        Player player2 = game.getPlayers()[1];

        player1.setScore(20);
        player2.setScore(20);

        int[] awarded = game.awardProgressPoints();

        assertTrue(awarded[0] >= 1);
        assertTrue(awarded[1] >= 1);
    }

    @Test
    void testAwardProgressPointsBonusFor50() {
        game.startNewGame("Alice", "Bob");

        Player player1 = game.getPlayers()[0];
        Player player2 = game.getPlayers()[1];

        player1.setScore(50);
        player2.setScore(40);

        int[] awarded = game.awardProgressPoints();

        assertEquals(3, awarded[0]);
        assertEquals(1, awarded[1]);
    }

    @Test
    void testCheckForGameWinnerNoWinner() {
        game.startNewGame("Alice", "Bob");

        Player winner = game.checkForGameWinner();
        assertNull(winner);
    }

    @Test
    void testCheckForGameWinnerPlayer1Wins() {
        game.startNewGame("Alice", "Bob");

        game.getPlayers()[0].setProgressPoints(100);

        Player winner = game.checkForGameWinner();
        assertNotNull(winner);
        assertEquals("Alice", winner.getName());
    }

    @Test
    void testCheckForGameWinnerPlayer2Wins() {
        game.startNewGame("Alice", "Bob");

        game.getPlayers()[1].setProgressPoints(100);

        Player winner = game.checkForGameWinner();
        assertNotNull(winner);
        assertEquals("Bob", winner.getName());
    }

    @Test
    void testResetEpisodeTracking() {
        game.startNewGame("Alice", "Bob");

        game.incrementMineDepth(0);
        game.addGoldNugget(0);
        game.incrementPalaceSize(0);

        game.resetEpisodeTracking();

        assertEquals(0, game.getMineDepth(0));
        assertEquals(0, game.getGoldNuggets(0));
        assertEquals(0, game.getPalaceSize(0));
    }

    @Test
    void testSaveAndLoadGameState() {
        game.startNewGame("Alice", "Bob");

        game.loadEpisode(7);

        game.saveGameState();

        // Change episode
        game.loadEpisode(10);
        assertEquals(10, game.getCurrentEpisodeNumber());

        // Load the saved state - episode should be restored
        game.loadGameState();

        assertEquals(7, game.getCurrentEpisodeNumber());
        assertNotNull(game.getGameState());
    }

    @Test
    void testLoadGameStateWithNullState() {
        Game newGame = new Game();
        newGame.startNewGame("Test1", "Test2");

        int originalScore = newGame.getPlayers()[0].getScore();
        newGame.loadGameState();
        assertEquals(originalScore, newGame.getPlayers()[0].getScore());
    }

    @Test
    void testIncrementMineDepthPlayer1() {
        game.startNewGame("Alice", "Bob");

        assertEquals(0, game.getMineDepth(0));
        game.incrementMineDepth(0);
        assertEquals(1, game.getMineDepth(0));
        game.incrementMineDepth(0);
        assertEquals(2, game.getMineDepth(0));
    }

    @Test
    void testIncrementMineDepthPlayer2() {
        game.startNewGame("Alice", "Bob");

        assertEquals(0, game.getMineDepth(1));
        game.incrementMineDepth(1);
        assertEquals(1, game.getMineDepth(1));
    }

    @Test
    void testAddGoldNuggetPlayer1() {
        game.startNewGame("Alice", "Bob");

        assertEquals(0, game.getGoldNuggets(0));
        game.addGoldNugget(0);
        assertEquals(1, game.getGoldNuggets(0));
        game.addGoldNugget(0);
        assertEquals(2, game.getGoldNuggets(0));
    }

    @Test
    void testAddGoldNuggetPlayer2() {
        game.startNewGame("Alice", "Bob");

        assertEquals(0, game.getGoldNuggets(1));
        game.addGoldNugget(1);
        assertEquals(1, game.getGoldNuggets(1));
    }

    @Test
    void testIncrementPalaceSizePlayer1() {
        game.startNewGame("Alice", "Bob");

        assertEquals(0, game.getPalaceSize(0));
        game.incrementPalaceSize(0);
        assertEquals(1, game.getPalaceSize(0));
        game.incrementPalaceSize(0);
        assertEquals(2, game.getPalaceSize(0));
    }

    @Test
    void testIncrementPalaceSizePlayer2() {
        game.startNewGame("Alice", "Bob");

        assertEquals(0, game.getPalaceSize(1));
        game.incrementPalaceSize(1);
        assertEquals(1, game.getPalaceSize(1));
    }

    @Test
    void testGetCurrentEpisode() {
        game.startNewGame("Alice", "Bob");
        assertNotNull(game.getCurrentEpisode());
    }

    @Test
    void testGetPlayers() {
        game.startNewGame("Alice", "Bob");

        Player[] players = game.getPlayers();
        assertEquals(2, players.length);
        assertNotNull(players[0]);
        assertNotNull(players[1]);
    }

    @Test
    void testGetCurrentPlayerIndex() {
        game.startNewGame("Alice", "Bob");
        assertEquals(0, game.getCurrentPlayerIndex());
    }

    @Test
    void testGetCurrentEpisodeNumber() {
        game.startNewGame("Alice", "Bob");
        assertEquals(1, game.getCurrentEpisodeNumber());

        game.loadEpisode(10);
        assertEquals(10, game.getCurrentEpisodeNumber());
    }

    @Test
    void testGetGameState() {
        game.startNewGame("Alice", "Bob");
        assertNotNull(game.getGameState());
    }

    @Test
    void testCalculateFinalScores() {
        game.startNewGame("Alice", "Bob");

        game.getPlayers()[0].setScore(30);
        game.getPlayers()[1].setScore(25);

        game.calculateFinalScores();
    }

    @Test
    void testPlayEpisode() {
        game.startNewGame("Alice", "Bob");
        game.playEpisode();
    }

    @Test
    void testMultipleEpisodeTransitions() {
        game.startNewGame("Alice", "Bob");

        for (int episode = 1; episode <= 24; episode++) {
            game.loadEpisode(episode);
            assertEquals(episode, game.getCurrentEpisodeNumber());
            assertNotNull(game.getCurrentEpisode());
        }
    }
}
