package io.github.myCity.model;

/**
 * Stores the state of a single player for saving and loading purposes.
 */
public class PlayerState {

    private final CityGrid cityGrid;
    private final int score;

    public PlayerState(CityGrid cityGrid, int score) {
        this.cityGrid = cityGrid;
        this.score = score;
    }

    public CityGrid getCityGrid() {
        return cityGrid;
    }

    public int getScore() {
        return score;
    }
}
