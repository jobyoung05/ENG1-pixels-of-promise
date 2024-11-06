package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.Arrays;


// TODO
// create an array of Buildings that are available, and an array of Buildings that are being used. this would make
// limits easy to introduce, as well as making it easy to pass into HighlightTiles (hopefully)

public class GameScreen implements Screen {
    final UniSim game;

    private final int tileSize = 16;
    private final int mapWidth = 60;
    private final int mapHeight = 36;
    private boolean isPaused = false;
    private Timer timer;
    private TiledMap map;
    private TiledMapRenderer renderer;
    private Texture tileTexture;
    private TextureRegion[] textureRegions;
    private OrthographicCamera camera;
    private OrthoCamController cameraController;
    private Stage buttonStage;
    private int currentLayer = 0;
    private int[] lastHoveredTile = {0,0};
    private UIButton currentButton;

    // Building related stuff
    private BuildingManager buildingManager;
    private Building[] availableBuildings = new Building[10];
    private Building[] placedBuildings = new Building[10];
    private Building currentBuildingBeingPlaced;
    private Boolean isPlacing = false;
    private HighlightTiles highlightTiles;

    // No more building stuff

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
        buttonStage = new Stage();
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(cameraController);
        //inputMultiplexer.addProcessor(buttonStage);
        //Gdx.input.setInputProcessor(inputMultiplexer)
        Gdx.input.setInputProcessor(buttonStage);

        // Create the timer
        timer = new Timer();

        // Load the tiles from the texture pack
        tileTexture = new Texture(Gdx.files.internal("galletcity.png"));
        TextureRegion[][] splitTiles = TextureRegion.split(tileTexture, tileSize, tileSize);
        // Flatten the 2D array for easier access
        textureRegions = new TextureRegion[168];
        for (int y = 0; y < 21; y++) {
            for (int x = 0; x < 8; x++) {
                textureRegions[y * 8 + x] = splitTiles[y][x];
            }
        }

        // Create map and background layer(0)
        /*
        map = new TiledMap();
        MapLayers layers = map.getLayers();
        TiledMapTileLayer background = getTiledMapTileLayer();
        layers.add(background);
        layers.add(new TiledMapTileLayer(mapWidth, mapHeight, tileSize, tileSize));
        */

        // ****Loads the premade map instead****
        map = new TmxMapLoader().load("untitled.tmx");
        // *************************************

        // add a new empty layer for tile selector
        map.getLayers().add(new TiledMapTileLayer(mapWidth, mapHeight, tileSize, tileSize));
        // Initialize the renderer with the map we just created
        renderer = new OrthogonalTiledMapRenderer(map);

        // Initialize HighlightTiles
        highlightTiles = new HighlightTiles((TiledMapTileLayer) map.getLayers().get(2), textureRegions);

        // Buildings
        buildingManager = new BuildingManager("buildings.json");

        availableBuildings[0] = buildingManager.getBuildingInstance("accommodation");
        availableBuildings[1] = buildingManager.getBuildingInstance("accommodation");
//        System.out.println(Arrays.toString(availableBuildings));
        availableBuildings[0].setLocation(34, 28);
        availableBuildings[1].setLocation(10,10);
        availableBuildings[0].addToLayer(map, textureRegions);
        availableBuildings[1].addToLayer(map, textureRegions);

        UIButton b = new UIButton(buttonStage, "Accommodation", 0, (int) height-32, 128, 32);
        b.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                isPlacing = !isPlacing;
                currentButton = b;
            }
        });

    }

    private TiledMapTileLayer getTiledMapTileLayer() {
        TiledMapTileLayer background = new TiledMapTileLayer(mapWidth, mapHeight, tileSize, tileSize);
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
        return background;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(100f / 255f, 100f / 255f, 250f / 255f, 1f);
        updateSelectionLayer();

        Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        //Switches the layer viewed for debugging
        if (Gdx.input.isKeyJustPressed(Input.Keys.L) && map.getLayers().getCount() > 1) {
            currentLayer = (currentLayer == 0) ? 1 : 0;  // Toggle between layer 0 and 1
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            if (isPlacing) {
                isPlacing = false;
                highlightTiles.clearHighlight( worldCoordinates, buildingManager.getBuilding("accommodation"));
                // maybe it would be better to pass in an oject that already has all of these things? or if not all of it
                // definitely the size, textures to be used, etc. like this works fine for a simple colour of size n*m
                // would not work so well for a building with different tiles being used
                // store all available buildings in a dynamic list? then pass one in
            } else {
                isPlacing = true;
            }
        }
        if (Gdx.input.isTouched()){
            isPlacing = false;
            highlightTiles.clearHighlight(worldCoordinates, buildingManager.getBuilding("accommodation"));
            if (currentButton != null){
                currentButton.setChecked(false);
            }
        }


        if (isPlacing) {
            highlightTiles.updateHighlight(worldCoordinates, buildingManager.getBuilding("accommodation"));
        }

        // Render the map
        camera.update();
        renderer.setView(camera);
        renderer.render();

        timer.add(delta);

        // Get tile information
        TileInfo tileInfo = getCurrentTileInfo();

        int tileX = (int) (worldCoordinates.x / tileSize);
        int tileY = (int) (worldCoordinates.y / tileSize);

        String debugString = "FPS: " + Gdx.graphics.getFramesPerSecond() + "  "
            + "Layer: " + currentLayer + "  "
            + "Cell ID: " + tileInfo.id + "  ("
            + (tileInfo.isFlippedH ? "H, " : "")
            + (tileInfo.isFlippedV ? "V, " : "")
            + tileInfo.rotation + ") "
            + "Position: " + tileX + ", " + tileY + " "
            + "Timer:" + timer.getTimeUI();

        // Render the FPS
        game.batch.begin();
        game.font.draw(game.batch, debugString, 10, 20);
        game.batch.end();

        buttonStage.draw();
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
        tileTexture.dispose();
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
}
