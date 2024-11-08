package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;

public interface Placeable {
    void addToLayer(TiledMap map);
    int getCost();
    void setLocation(int x, int y);
}
