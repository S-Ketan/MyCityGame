package io.github.myCity.model;

/**
 * Represents a card from the building card deck that specifies which tile to build.
 */
public class BuildingCard {

    private final Tile tile;

    public BuildingCard(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }
}
