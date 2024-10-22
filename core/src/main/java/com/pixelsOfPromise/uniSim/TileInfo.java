package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class TileInfo {
    public String id;
    public boolean isManmade;

    public TileInfo(String id, boolean isManmade) {
        this.id = id;
        this.isManmade = isManmade;
    }
}
