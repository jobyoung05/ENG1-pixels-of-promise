package com.pixelsOfPromise.uniSim;

public class Building {
    private String buildingName;
    private String configLocation = "Buildings.json";
    private int x;
    private int y;
    private TileInfo[][] TileInfoArray;
    public Building(String buildingName, int index, int x, int y) {
        this.buildingName = buildingName;
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
}
