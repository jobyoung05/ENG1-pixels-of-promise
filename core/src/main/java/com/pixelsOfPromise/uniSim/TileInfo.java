package com.pixelsOfPromise.uniSim;

public class TileInfo {
    private final int id;
    private final boolean isFlippedH;
    private final boolean isFlippedV;
    private final int rotation;

    // Constructors
    public TileInfo(int id, boolean isFlippedH, boolean isFlippedV, int rotation) {
        this.id = id;
        this.isFlippedH = isFlippedH;
        this.isFlippedV = isFlippedV;
        this.rotation = rotation;
    }

    public TileInfo(int id, boolean isFlippedH, boolean isFlippedV) {
        this(id, isFlippedH, isFlippedV, 0);
    }

    public TileInfo(int id) {
        this(id, false, false, 0);
    }

    // Copy constructor
    public TileInfo(TileInfo other) {
        this(other.id, other.isFlippedH, other.isFlippedV, other.rotation);
    }

    // Getters
    public int getId() {
        return id;
    }

    public boolean isFlippedH() {
        return isFlippedH;
    }

    public boolean isFlippedV() {
        return isFlippedV;
    }

    public int getRotation() {
        return rotation;
    }

}
