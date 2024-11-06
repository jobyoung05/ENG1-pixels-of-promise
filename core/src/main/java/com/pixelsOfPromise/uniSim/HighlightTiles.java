package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

public class HighlightTiles {
    private final int tileSize = 16;
    private Array<int[]> lastHoveredTiles = new Array<>(); // Track all highlighted tiles
    private TiledMapTileLayer layer = null;
    private TextureRegion[] textureRegions = null;

    public HighlightTiles(TiledMapTileLayer layer, TextureRegion[] textureRegions) {
        this.layer = layer;
        this.textureRegions = textureRegions;
    }

    public void updateHighlight(Vector3 worldCoordinates, Building building) {
        // Calculate the center tile coordinates from mouse position
        int tileX = (int) (worldCoordinates.x / tileSize);
        int tileY = (int) (worldCoordinates.y / tileSize);

        // Clear previous highlights
        for (int[] tile : lastHoveredTiles) {
            layer.setCell(tile[0], tile[1], null);
        }
        lastHoveredTiles.clear();

        TileInfo[][] tileInfoArray = building.getTileInfoArray();

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

                // Ensures that the building is placed with the x,y being in the bottom-left corner
                layer.setCell(tileX + k, tileY + (tileInfoArray.length - 1 - j), cell);
                lastHoveredTiles.add(new int[]{tileX + k, tileY + (tileInfoArray.length - 1 - j)});
            }
        }
    }


    public void clearHighlight(Vector3 worldCoordinates, Building building) {
        for (int[] tile : lastHoveredTiles) {
            layer.setCell(tile[0], tile[1], null);
        }
        lastHoveredTiles.clear();
    }
}
