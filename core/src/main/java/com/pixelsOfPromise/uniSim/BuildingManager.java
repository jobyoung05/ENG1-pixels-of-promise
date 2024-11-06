package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class BuildingManager {
    private Map<String, Building> buildings;

    public BuildingManager(String jsonFilePath) {
        buildings = new HashMap<>();
        loadBuildings(jsonFilePath);
    }

    private void loadBuildings(String jsonFilePath) {
        JsonReader jsonReader = new JsonReader();
        FileHandle fileHandle = Gdx.files.internal(jsonFilePath);
        JsonValue root = jsonReader.parse(fileHandle);

        JsonValue buildingsJson = root.get("buildings");
        for (JsonValue buildingJson : buildingsJson) {
            Building building = new Building();
            building.setName(buildingJson.getString("name"));
            building.setCost(buildingJson.getInt("cost"));

            // Parse TileInfoArray
            JsonValue tileArrayJson = buildingJson.get("TileInfoArray");
            int rows = tileArrayJson.size; // Number of rows
            int cols = tileArrayJson.get(0).size; // Number of columns in the first row
            TileInfo[][] tileInfoArray = new TileInfo[rows][cols];

            for (int row = 0; row < rows; row++) {
                JsonValue rowJson = tileArrayJson.get(row);
                for (int col = 0; col < cols; col++) {
                    JsonValue tileJson = rowJson.get(col);
                    int id = tileJson.getInt("id");
                    Boolean isFlippedH = tileJson.has("isFlippedH") ? tileJson.getBoolean("isFlippedH") : null;
                    Boolean isFlippedV = tileJson.has("isFlippedV") ? tileJson.getBoolean("isFlippedV") : null;

                    if (isFlippedH == null || isFlippedV == null) {
                        tileInfoArray[row][col] = new TileInfo(id);
                    } else {
                        tileInfoArray[row][col] = new TileInfo(id, isFlippedH, isFlippedV);
                    }
                }
            }
            building.setTileInfoArray(tileInfoArray);

            // Store the building in the map using its name
            buildings.put(building.getName(), building);
        }
    }

    // Access a building by name
    // Get a new instance (clone) of a building by name
    public Building getBuildingInstance(String name) {
        Building original = buildings.get(name);
        if (original != null) {
            return new Building(original); // Return a deep copy
        }
        return null;
    }

    public Building getBuilding(String name) {
        return buildings.get(name);
    }
}
