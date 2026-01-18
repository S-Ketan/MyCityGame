package io.github.myCity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single cell on the city grid.
 */
public class Cell {

    private TerrainType terrain;
    private boolean occupied;
    public boolean riverTop;
    public boolean riverBottom;
    public boolean riverLeft;
    public boolean riverRight;
    private BuildingColor color;
    private int tileId;
    private final List<Sticker> stickers = new ArrayList<>();

    public Cell(TerrainType terrain) {
        this.terrain = terrain;
        this.occupied = false;
        this.color = BuildingColor.NONE;
        this.tileId = 0;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public void setTerrain(TerrainType terrain) {
        this.terrain = terrain;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public BuildingColor getColor() {
        return color;
    }

    public void setColor(BuildingColor color) {
        this.color = color;
    }

    public int getTileId() {
        return tileId;
    }

    public void setTileId(int tileId) {
        this.tileId = tileId;
    }

    public boolean hasRiver() {
        return riverTop || riverBottom || riverLeft || riverRight;
    }

    public void addSticker(Sticker sticker) {
        this.stickers.add(sticker);
    }

    public List<Sticker> getStickers() {
        return stickers;
    }
}
