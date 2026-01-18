package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStateTest {

    @Test
    void testConstructorAndGetters() {
        CityGrid cityGrid = new CityGrid();
        int score = 100;

        PlayerState playerState = new PlayerState(cityGrid, score);

        assertEquals(cityGrid, playerState.getCityGrid());
        assertEquals(score, playerState.getScore());
    }
}
