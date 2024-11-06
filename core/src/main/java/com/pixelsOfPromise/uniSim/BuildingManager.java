package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class BuildingManager {
    private final Map<String, TileInfo[][]> buildingData;
    private final TextureRegion[] textureRegions;

    public BuildingManager(String jsonFilePath, TextureRegion[] textureRegions) {
        this.textureRegions = textureRegions;
        this.buildingData = new HashMap<>();
        loadBuildingData(jsonFilePath);
    }

    // Load building data from a JSON file, but do not create Building instances yet
    private void loadBuildingData(String jsonFilePath) {
        JsonReader jsonReader = new JsonReader();
        FileHandle fileHandle = Gdx.files.internal(jsonFilePath);
        JsonValue root = jsonReader.parse(fileHandle);

        JsonValue buildingsJson = root.get("buildings");
        for (JsonValue buildingJson : buildingsJson) {
            String name = buildingJson.getString("name");

            // Parse TileInfoArray and store it in the map
            TileInfo[][] tileInfoArray = parseTileInfoArray(buildingJson.get("TileInfoArray"));
            buildingData.put(name, tileInfoArray);
        }
    }

    // Parse the TileInfoArray from JSON and create TileInfo objects
    private TileInfo[][] parseTileInfoArray(JsonValue tileArrayJson) {
        int rows = tileArrayJson.size;
        int cols = tileArrayJson.get(0).size;
        TileInfo[][] tileInfoArray = new TileInfo[rows][cols];

        for (int row = 0; row < rows; row++) {
            JsonValue rowJson = tileArrayJson.get(row);
            for (int col = 0; col < cols; col++) {
                JsonValue tileJson = rowJson.get(col);
                int id = tileJson.getInt("id");
                boolean isFlippedH = tileJson.has("isFlippedH") && tileJson.getBoolean("isFlippedH");
                boolean isFlippedV = tileJson.has("isFlippedV") && tileJson.getBoolean("isFlippedV");
                int rotation = tileJson.has("rotation") ? tileJson.getInt("rotation") : 0;

                tileInfoArray[row][col] = new TileInfo(id, isFlippedH, isFlippedV, rotation);
            }
        }
        return tileInfoArray;
    }

    // Create a new Building instance on demand
    public Building createBuilding(String name, int cost) {
        TileInfo[][] tileInfoArray = buildingData.get(name);
        if (tileInfoArray == null) {
            return null; // Building data not found
        }

        // Convert TileInfo array to Cell array
        TiledMapTileLayer.Cell[][] cells = createCells(tileInfoArray);
        return new Building(name, cost, cells);
    }

    // Helper method to create cells from TileInfo data
    private TiledMapTileLayer.Cell[][] createCells(TileInfo[][] tileInfoArray) {
        TiledMapTileLayer.Cell[][] cells = new TiledMapTileLayer.Cell[tileInfoArray.length][];
        for (int j = 0; j < tileInfoArray.length; j++) {
            cells[j] = new TiledMapTileLayer.Cell[tileInfoArray[j].length];
            for (int k = 0; k < tileInfoArray[j].length; k++) {
                TileInfo currentTileInfo = tileInfoArray[j][k];
                cells[j][k] = createCell(currentTileInfo);
            }
        }
        return cells;
    }

    // Helper method to create a single cell
    private TiledMapTileLayer.Cell createCell(TileInfo tileInfo) {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        StaticTiledMapTile tile = new StaticTiledMapTile(textureRegions[tileInfo.getId()]);
        tile.setId(tileInfo.getId());
        cell.setTile(tile);
        cell.setFlipHorizontally(tileInfo.isFlippedH());
        cell.setFlipVertically(tileInfo.isFlippedV());
        cell.setRotation(tileInfo.getRotation());
        return cell;
    }
}
