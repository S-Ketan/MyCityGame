package io.github.myCity.model;

/**
 * Represents the complete game state that can be saved and loaded.
 */
public class GameState {

    private final int episodeNumber;
    private final PlayerState player1;
    private final PlayerState player2;

    public GameState(int episodeNumber, PlayerState player1, PlayerState player2) {
        this.episodeNumber = episodeNumber;
        this.player1 = player1;
        this.player2 = player2;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public PlayerState getPlayer1() {
        return player1;
    }

    public PlayerState getPlayer2() {
        return player2;
    }
}
