package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen {
    final UniSim game;

    private Timer timer;
    private TiledMap map;
    private TiledMapRenderer renderer;
    private Texture tiles;
    private TextureRegion[] textureRegions;
    private final int tileSize = 16;
    private OrthographicCamera camera;
    private OrthoCamController cameraController;
    private int currentLayer = 0;
    private int[] lastHoveredTile = {0,0};
    private Building[] buildings;

    public GameScreen(final UniSim game) {
        this.game = game;
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        // Creates the camera and sets the viewpoint
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        camera.update();
        // Creates the camera controller
        cameraController = new OrthoCamController(camera, width, height);
        Gdx.input.setInputProcessor(cameraController);

        //create the timer
        timer = new Timer();

        // Load the tiles from the texture pack
        tiles = new Texture(Gdx.files.internal("galletcity.png"));
        TextureRegion[][] splitTiles = TextureRegion.split(tiles, tileSize, tileSize);
        // Flatten the 2D array for easier access
        textureRegions = new TextureRegion[168];
        for (int y = 0; y < 21; y++) {
            for (int x = 0; x < 8; x++) {
                textureRegions[y * 8 + x] = splitTiles[y][x];
            }
        }

        // Create map and background layer(0)
        map = new TiledMap();
        MapLayers layers = map.getLayers();
        TiledMapTileLayer background = new TiledMapTileLayer(60, 36, tileSize, tileSize);
        int tileId = 95;
        for (int x = 0; x < 60; x++) {
            for (int y = 0; y < 36; y++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                StaticTiledMapTile tile = new StaticTiledMapTile(textureRegions[tileId]);
                tile.setId(tileId);  // Explicitly setting the tile ID
                cell.setTile(tile);
                background.setCell(x, y, cell);

            }
        }
        layers.add(background);

        // ****Loads the premade map instead****
        map = new TmxMapLoader().load("untitled.tmx");
        // *************************************

        // add a new empty layer for tile selector
        map.getLayers().add(new TiledMapTileLayer(60, 36, tileSize, tileSize));
        // Initialize the renderer with the map we just created
        renderer = new OrthogonalTiledMapRenderer(map);

        buildings = new Building[1]; // this will need to be a dynamic array at some point but for now it's static
        buildings[0] = new Building("test", 2,34, 28);
        buildings[0].addToLayer(map, textureRegions);
    }


    @Override
    public void render(float v) {
        ScreenUtils.clear(100f / 255f, 100f / 255f, 250f / 255f, 1f);
        updateSelectionLayer();
        // Render the map
        camera.update();
        renderer.setView(camera);
        renderer.render();

        timer.add(Gdx.graphics.getDeltaTime());

        //Switches the layer viewed for debugging
        if (Gdx.input.isKeyJustPressed(Input.Keys.L) && map.getLayers().getCount() > 1) {
            currentLayer = (currentLayer == 0) ? 1 : 0;  // Toggle between layer 0 and 1
        }

        // Get tile information
        TileInfo tileInfo = getCurrentTileInfo();

        String debugString = "FPS: " + Gdx.graphics.getFramesPerSecond() + "  "
            + "Layer: " + currentLayer + "  "
            + "Cell ID: " + tileInfo.id + "  ("
            + (tileInfo.isFlippedH ? "H, " : "")
            + (tileInfo.isFlippedV ? "V, " : "")
            + tileInfo.rotation + ") "
            + "Timer:" + (float) Math.round(timer.getTime()*10) / 10;

        // Render the FPS
        game.batch.begin();
        game.font.draw(game.batch, debugString, 10, 20);

        game.batch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        tiles.dispose();
    }

    //Returns the tile id and whether it is man made (can be used to determine if a tile is removable)
    private TileInfo getCurrentTileInfo() {
        // Get the mouse screen coordinates
        Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        // Convert world coordinates to tile coordinates
        int tileX = (int) (worldCoordinates.x / tileSize);
        int tileY = (int) (worldCoordinates.y / tileSize);

        // Get the current cell
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(currentLayer);
        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);

        // Get the required data from the cell
        int id = (cell != null) ? cell.getTile().getId() - 1: -1;
        boolean isFlippedH = cell != null && cell.getFlipHorizontally();
        boolean isFlippedV = cell != null && cell.getFlipVertically();
        int rotation = (cell != null) ? cell.getRotation() : -1;

        return new TileInfo(id, isFlippedH, isFlippedV, rotation);
    }

    private void updateSelectionLayer(){
        // Get the mouse screen coordinates
        Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        // Convert world coordinates to tile coordinates
        int tileX = (int) (worldCoordinates.x / tileSize);
        int tileY = (int) (worldCoordinates.y / tileSize);

        // Select the highlighting layer
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);

        // Remove previously selected tile
        layer.setCell(lastHoveredTile[0], lastHoveredTile[1], null);

        // Set the current cell to be tile #166 (selection tile)
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        StaticTiledMapTile tile = new StaticTiledMapTile(textureRegions[166]);
        tile.setId(166);  // Explicitly setting the tile ID
        cell.setTile(tile);
        layer.setCell(tileX, tileY, cell);

        // Record the location of the tile we just set
        lastHoveredTile = new int[]{tileX, tileY};
    }

    private void drawBuildings(){
        /*
        Although this function 'works', I want to move it inside the building class, as it only gets called when the
        building is created, for that instance of Building. If we were to draw more than one building, we would be
        re-drawing every building each time a new building was created
         */


        // Get the 'building' layer
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        // For each building, add each tile to the map at its specified position
        for (int i = 0; i < buildings.length; i++) {
            TileInfo[][] tiles = buildings[i].getTileInfoArray();
            int x = buildings[i].getX();
            int y = buildings[i].getY();
            for (int j = 0; j < tiles.length; j++) {
                for (int k = 0; k < tiles[j].length; k++) {

                    TileInfo currentTileInfo = tiles[j][k];

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
    }
}
