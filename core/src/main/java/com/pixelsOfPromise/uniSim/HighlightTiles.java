package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

public class HighlightTiles {
    private final int tileSize = 16;
    private Array<int[]> lastHoveredTiles = new Array<>(); // Track all highlighted tiles
    static private TiledMapTileLayer layer = null;

    public HighlightTiles(TiledMapTileLayer layer) {
        this.layer = layer;
    }

    public void updateHighlight(Vector3 worldCoordinates, TiledMapTileLayer.Cell[][] cells) {
        // Calculate the center tile coordinates from mouse position
        int tileX = (int) (worldCoordinates.x / tileSize);
        int tileY = (int) (worldCoordinates.y / tileSize);

        // Clear previous highlights
        for (int[] tile : lastHoveredTiles) {
            layer.setCell(tile[0], tile[1], null);
        }
        lastHoveredTiles.clear();

        for (int j = 0; j < cells.length; j++) {
            for (int k = 0; k < cells[j].length; k++) {
                // Ensures that the building is placed with the x,y being in the bottom-left corner
                layer.setCell(tileX + k, tileY + (cells.length - 1 - j), cells[j][k]);
                lastHoveredTiles.add(new int[]{tileX + k, tileY + (cells.length - 1 - j)});
            }
        }
    }

    public void clearHighlight() {
        for (int[] tile : lastHoveredTiles) {
            layer.setCell(tile[0], tile[1], null);
        }
        lastHoveredTiles.clear();
    }
}
