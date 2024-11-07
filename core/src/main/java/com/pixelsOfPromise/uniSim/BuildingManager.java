package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class BuildingManager {
    private final Map<String, BuildingData> buildingDataMap;
    private final TextureRegion[] textureRegions;
    private final EnumMap<BuildingType, Integer> buildingCounts;
    private final EnumMap<BuildingType, Integer> buildingLimits;

    public BuildingManager(String jsonFilePath, TextureRegion[] textureRegions) {
        this.textureRegions = textureRegions;
        this.buildingDataMap = new HashMap<>();
        this.buildingCounts = new EnumMap<>(BuildingType.class);
        this.buildingLimits = new EnumMap<>(BuildingType.class);
        initializeBuildingCounts();
        loadBuildingData(jsonFilePath);
    }

    // Inner class to hold building data
    private static class BuildingData {
        String name;
        BuildingType type;
        int cost;
        TileInfo[][] tileInfoArray;

        BuildingData(String name, BuildingType type, int cost, TileInfo[][] tileInfoArray) {
            this.name = name;
            this.type = type;
            this.cost = cost;
            this.tileInfoArray = tileInfoArray;
        }
    }

    // Initialize building counts and set limits
    private void initializeBuildingCounts() {
        for (BuildingType type : BuildingType.values()) {
            buildingCounts.put(type, 0);
        }
        // Set limits per building type
        buildingLimits.put(BuildingType.PLACE_TO_SLEEP, 100);
        buildingLimits.put(BuildingType.PLACE_TO_LEARN, 1);
        buildingLimits.put(BuildingType.PLACE_TO_EAT, 1);
        buildingLimits.put(BuildingType.RECREATIONAL_ACTIVITY, 1);
    }

    // Load building data from a JSON file, but do not create Building instances yet
    private void loadBuildingData(String jsonFilePath) {
        JsonReader jsonReader = new JsonReader();
        FileHandle fileHandle = Gdx.files.internal(jsonFilePath);
        JsonValue root = jsonReader.parse(fileHandle);

        JsonValue buildingsJson = root.get("buildings");
        for (JsonValue buildingJson : buildingsJson) {
            String name = buildingJson.getString("name");
            String typeStr = buildingJson.getString("type");
            BuildingType type = BuildingType.valueOf(typeStr.toUpperCase());
            int cost = buildingJson.getInt("cost");
            TileInfo[][] tileInfoArray = parseTileInfoArray(buildingJson.get("TileInfoArray"));

            buildingDataMap.put(name, new BuildingData(name, type, cost, tileInfoArray));
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

    // Create a new Building instance on demand with limit checks
    public Building createBuilding(String name, int cost) {
        BuildingData data = buildingDataMap.get(name);
        if (data == null) {
            Gdx.app.error("BuildingManager", "Building data not found for: " + name);
            return null;
        }

        // Check if the building limit for this type has been reached
        BuildingType type = data.type;

        if (buildingCounts.get(type) >= buildingLimits.get(type)) {
            Gdx.app.log("BuildingManager", "Cannot create more buildings of type: " + type);
            return null;
        }

        // Increment the building count
        buildingCounts.put(type, buildingCounts.get(type) + 1);

        // Convert TileInfo array to Cell array
        TiledMapTileLayer.Cell[][] cells = createCells(data.tileInfoArray);

        return new Building(data.name, data.cost, cells, type);
    }

    // Method to decrement building count when a building is removed
    public void removeBuilding(Building building) {
        BuildingType type = building.getType();
        if (buildingCounts.get(type) > 0) {
            buildingCounts.put(type, buildingCounts.get(type) - 1);
        }
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
