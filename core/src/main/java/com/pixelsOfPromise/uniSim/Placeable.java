package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;

public interface Placeable {
    Boolean checkIfValidPosition(int x, int y);
    void placeAt(int x, int y);
    void removeFrom(int x, int y);
    void addToLayer(TiledMap map, TextureRegion[] textureRegions);
    void removeFromLayer(TiledMap map, TextureRegion[] textureRegions);

    int getCost();
    String getBuildingName();
}
