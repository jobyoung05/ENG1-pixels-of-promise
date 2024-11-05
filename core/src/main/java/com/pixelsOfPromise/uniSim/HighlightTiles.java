package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

public class HighlightTiles {
    private int tileSize = 16; // Assuming tile size is 32 pixels
    private int highlightWidth; // Half-width for 4x3 grid (2 tiles on each side horizontally)
    private int highlightHeight; // Half-height for 4x3 grid (1 tile on each side vertically)
    private Array<int[]> lastHoveredTiles = new Array<>(); // Track all highlighted tiles

    public void updateHighlight(TiledMapTileLayer layer, Vector3 worldCoordinates, TextureRegion[] textureRegions, int highlightHeight, int highlightWidth) {
        // Calculate the center tile coordinates from mouse position
        int tileX = (int) (worldCoordinates.x / tileSize);
        int tileY = (int) (worldCoordinates.y / tileSize);

        // Clear previous highlights
        for (int[] tile : lastHoveredTiles) {
            layer.setCell(tile[0], tile[1], null);
        }
        lastHoveredTiles.clear();

        // Highlight a 4x3 grid around the mouse's tile
        for (int dx = 0; dx <= highlightWidth; dx++) {
            for (int dy = 0; dy <= highlightHeight; dy++) {
                int highlightX = tileX + dx;
                int highlightY = tileY + dy;

                // Ensure highlightX and highlightY are within map bounds
                if (highlightX >= 0 && highlightY >= 0 && highlightX < layer.getWidth() && highlightY < layer.getHeight()) {
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    StaticTiledMapTile tile = new StaticTiledMapTile(textureRegions[5]); // Use tile #166 as highlight
                    tile.setId(5);
                    cell.setTile(tile);
                    layer.setCell(highlightX, highlightY, cell);

                    // Track the highlighted cell for clearing next frame
                    lastHoveredTiles.add(new int[]{highlightX, highlightY});
                }
            }
        }
    }

    public void clearHighlight(TiledMapTileLayer layer, Vector3 worldCoordinates, int highlightHeight, int highlightWidth) {
        // Calculate the center tile coordinates from mouse position
        int tileX = (int) (worldCoordinates.x / tileSize);
        int tileY = (int) (worldCoordinates.y / tileSize);

        // Highlight a 4x3 grid around the mouse's tile
        for (int dx = 0; dx <= highlightWidth; dx++) {
            for (int dy = 0; dy <= highlightHeight; dy++) {
                int highlightX = tileX + dx;
                int highlightY = tileY + dy;

                // Ensure highlightX and highlightY are within map bounds
                if (highlightX >= 0 && highlightY >= 0 && highlightX < layer.getWidth() && highlightY < layer.getHeight()) {
                    layer.setCell(highlightX, highlightY, null);
                }
            }
        }
    }
}
