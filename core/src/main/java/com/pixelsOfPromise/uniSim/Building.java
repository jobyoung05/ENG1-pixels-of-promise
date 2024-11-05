package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class Building implements Placeable {
    private String buildingName;
    private int cost;
    private int x;
    private int y;
    private TileInfo[][] TileInfoArray;

    public Building(String buildingName, int cost, int x, int y) {
        this.buildingName = buildingName;
        this.cost = cost;
        this.x = x;
        this.y = y;

        // Hard coded building for now ;)
        TileInfoArray = new TileInfo[5][5];
        TileInfoArray[0][0] = new TileInfo(72);
        TileInfoArray[0][1] = new TileInfo(74);
        TileInfoArray[0][2] = new TileInfo(74);
        TileInfoArray[0][3] = new TileInfo(74);
        TileInfoArray[0][4] = new TileInfo(72, true, false);
        TileInfoArray[1][0] = new TileInfo(136);
        TileInfoArray[1][1] = new TileInfo(5);
        TileInfoArray[1][2] = new TileInfo(81);
        TileInfoArray[1][3] = new TileInfo(5);
        TileInfoArray[1][4] = new TileInfo(80, true, false);
        TileInfoArray[2][0] = new TileInfo(160);
        TileInfoArray[2][1] = new TileInfo(156);
        TileInfoArray[2][2] = new TileInfo(156);
        TileInfoArray[2][3] = new TileInfo(156);
        TileInfoArray[2][4] = new TileInfo(153);
        TileInfoArray[3][0] = new TileInfo(160);
        TileInfoArray[3][1] = new TileInfo(157);
        TileInfoArray[3][2] = new TileInfo(157);
        TileInfoArray[3][3] = new TileInfo(157);
        TileInfoArray[3][4] = new TileInfo(153);
        TileInfoArray[4][0] = new TileInfo(160);
        TileInfoArray[4][1] = new TileInfo(162);
        TileInfoArray[4][2] = new TileInfo(162);
        TileInfoArray[4][3] = new TileInfo(162);
        TileInfoArray[4][4] = new TileInfo(153);
        // How lovely

    }

    public TileInfo[][] getTileInfoArray(){
        return TileInfoArray;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void addToLayer(TiledMap map, TextureRegion[] textureRegions) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        for (int j = 0; j < TileInfoArray.length; j++) {
            for (int k = 0; k < TileInfoArray[j].length; k++) {

                TileInfo currentTileInfo = TileInfoArray[j][k];

                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                StaticTiledMapTile tile = new StaticTiledMapTile(textureRegions[currentTileInfo.id]);
                tile.setId(currentTileInfo.id);  // Explicitly setting the tile ID
                cell.setTile(tile);
                cell.setFlipHorizontally(currentTileInfo.isFlippedH);
                cell.setFlipVertically(currentTileInfo.isFlippedV);
                cell.setRotation(currentTileInfo.rotation);
                layer.setCell(x + k, y - j, cell);
            }
        }
    }

    @Override
    public void removeFromLayer(TiledMap map, TextureRegion[] textureRegions) {

    }

    @Override
    public Boolean checkIfValidPosition(int x, int y) {
        return null;
    }

    @Override
    public void placeAt(int x, int y) {

    }

    @Override
    public void removeFrom(int x, int y) {

    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public String getBuildingName() {
        return this.buildingName;
    }
}
