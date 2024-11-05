package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class Building {
    private String name;
    private int cost;
    private TileInfo[][] tileInfoArray;
    private int x;
    private int y;

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

    public TileInfo[][] getTileInfoArray() {
        return tileInfoArray;
    }

    public void setTileInfoArray(TileInfo[][] tileInfoArray) {
        this.tileInfoArray = tileInfoArray;
    }

    public void addToLayer(TiledMap map, TextureRegion[] textureRegions) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        for (int j = 0; j < tileInfoArray.length; j++) {
            for (int k = 0; k < tileInfoArray[j].length; k++) {

                TileInfo currentTileInfo = tileInfoArray[j][k];

                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                StaticTiledMapTile tile = new StaticTiledMapTile(textureRegions[currentTileInfo.id]);
                tile.setId(currentTileInfo.id);  // Explicitly setting the tile ID
                cell.setTile(tile);
                cell.setFlipHorizontally(currentTileInfo.isFlippedH);
                cell.setFlipVertically(currentTileInfo.isFlippedV);
                cell.setRotation(currentTileInfo.rotation);
                layer.setCell(x + k, y - j, cell);
            }
        }
    }

    public Building(Building other) {
        this.name = other.name;
        this.cost = other.cost;
        this.setLocation(0,0);
        this.tileInfoArray = new TileInfo[other.tileInfoArray.length][];
        for (int i = 0; i < other.tileInfoArray.length; i++) {
            this.tileInfoArray[i] = new TileInfo[other.tileInfoArray[i].length];
            for (int j = 0; j < other.tileInfoArray[i].length; j++) {
                this.tileInfoArray[i][j] = new TileInfo(other.tileInfoArray[i][j]);
            }
        }
    }

    public Building() {
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
