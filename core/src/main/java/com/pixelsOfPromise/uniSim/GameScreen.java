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

    private TiledMap map;
    private TiledMapRenderer renderer;
    private Texture tiles;
    private TextureRegion[] allTiles;
    private final int tileSize = 16;
    private OrthographicCamera camera;
    private OrthoCamController cameraController;
    private int currentLayer = 0;
    private int[] lastClicked = {0,0};

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


        // Load the tiles from the texture pack
        tiles = new Texture(Gdx.files.internal("galletcity.png"));
        TextureRegion[][] splitTiles = TextureRegion.split(tiles, tileSize, tileSize);
        // Flatten the 2D array for easier access
        allTiles = new TextureRegion[168];
        for (int y = 0; y < 21; y++) {
            for (int x = 0; x < 8; x++) {
                allTiles[y * 8 + x] = splitTiles[y][x];
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
                StaticTiledMapTile tile = new StaticTiledMapTile(allTiles[tileId]);
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
    }


    @Override
    public void render(float v) {
        ScreenUtils.clear(100f / 255f, 100f / 255f, 250f / 255f, 1f);
        UpdateSelectionLayer();
        // Render the map
        camera.update();
        renderer.setView(camera);
        renderer.render();


        //Switches the layer viewed for debugging
        if (Gdx.input.isKeyJustPressed(Input.Keys.L) && map.getLayers().getCount() > 1) {
            currentLayer = (currentLayer == 0) ? 1 : 0;  // Toggle between layer 0 and 1
        }

        // Get tile information
        TileInfo tileInfo = getCurrentTileInfo();

        // Render the FPS
        game.batch.begin();
        game.font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        game.font.draw(game.batch, "Layer: " + currentLayer, 70, 20);
        game.font.draw(game.batch, "Current cell ID (map base): " + tileInfo.id, 130, 20);
        game.font.draw(game.batch, "Manmade tile: " + tileInfo.isManmade, 350, 20);
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

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(currentLayer);
        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);

        String id = (cell != null) ? String.valueOf(cell.getTile().getId()) : "?";
        boolean isManmade = (cell != null && currentLayer != 0);

        return new TileInfo(id, isManmade);
    }

    private void UpdateSelectionLayer(){

        Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        // Convert world coordinates to tile coordinates
        int tileX = (int) (worldCoordinates.x / tileSize);
        int tileY = (int) (worldCoordinates.y / tileSize);

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);


        layer.setCell(lastClicked[0], lastClicked[1], null);

        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        StaticTiledMapTile tile = new StaticTiledMapTile(allTiles[166]);
        tile.setId(166);  // Explicitly setting the tile ID
        cell.setTile(tile);
        layer.setCell(tileX, tileY, cell);



        lastClicked= new int[]{tileX, tileY};
    }
}
