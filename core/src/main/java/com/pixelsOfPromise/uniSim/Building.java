package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class Building {
    private String name;
    private int cost;
    private TiledMapTileLayer.Cell[][] cells;
    private int x;
    private int y;

    // Constructor for creating a Building instance
    public Building(String name, int cost, TiledMapTileLayer.Cell[][] cells) {
        this.name = name;
        this.cost = cost;
        this.cells = cells;
    }

    // Adds the building's pre-created cells to the map layer
    public void addToLayer(TiledMap map) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        for (int j = 0; j < cells.length; j++) {
            for (int k = 0; k < cells[j].length; k++) {
                layer.setCell(x + k, y + (cells.length - 1 - j), cells[j][k]);
            }
        }
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public TiledMapTileLayer.Cell[][] getCells() {
        return cells;
    }
}
