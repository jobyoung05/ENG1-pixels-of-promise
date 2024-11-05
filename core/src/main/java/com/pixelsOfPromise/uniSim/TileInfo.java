package com.pixelsOfPromise.uniSim;

public class TileInfo {
    public int id;
    public boolean isFlippedH;
    public boolean isFlippedV;
    public int rotation;

    public TileInfo(int id, boolean isFlippedH, boolean isFlippedV, int rotation) {
        this.id = id;
        this.isFlippedH = isFlippedH;
        this.isFlippedV = isFlippedV;
        this.rotation = rotation;
    }

    public TileInfo(int id, boolean isFlippedH, boolean isFlippedV) {
        this.id = id;
        this.isFlippedH = isFlippedH;
        this.isFlippedV = isFlippedV;
        this.rotation = 0;
    }

    public TileInfo(int id) {
        this.id = id;
        this.isFlippedH = false;
        this.isFlippedV = false;
        this.rotation = 0;
    }

    // Copy constructor
    public TileInfo(TileInfo other) {
        this.id = other.id;
        this.isFlippedH = other.isFlippedH;
        this.isFlippedV = other.isFlippedV;
        this.rotation = 0;
    }

}
