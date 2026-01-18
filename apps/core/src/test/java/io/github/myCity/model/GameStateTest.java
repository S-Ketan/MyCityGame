package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void testConstructorAndGetters() {
        CityGrid cityGrid1 = new CityGrid();
        PlayerState player1 = new PlayerState(cityGrid1, 50);

        CityGrid cityGrid2 = new CityGrid();
        PlayerState player2 = new PlayerState(cityGrid2, 75);

        int episodeNumber = 3;

        GameState gameState = new GameState(episodeNumber, player1, player2);

        assertEquals(episodeNumber, gameState.getEpisodeNumber());
        assertEquals(player1, gameState.getPlayer1());
        assertEquals(player2, gameState.getPlayer2());
    }
}
