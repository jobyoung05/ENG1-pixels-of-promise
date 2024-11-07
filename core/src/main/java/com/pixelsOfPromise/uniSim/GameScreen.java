package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector3;


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
    private String debugString;
    private Vector3 worldCoordinates;

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

        // Buttons and input
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
        highlightTiles = new HighlightTiles((TiledMapTileLayer) map.getLayers().get(2));

        // Buildings
        buildingManager = new BuildingManager("buildings.json", textureRegions);

        availableBuildings[0] = buildingManager.createBuilding("accommodation", 1000);
        availableBuildings[1] = buildingManager.createBuilding("accommodation", 1000);
        availableBuildings[0].setLocation(34, 28);
        availableBuildings[1].setLocation(10,10);
        availableBuildings[0].addToLayer(map);
        availableBuildings[1].addToLayer(map);

        UIButton accommodationButton = new UIButton(buttonStage, "Accommodation", 0, (int) height-32, 128, 32);
        accommodationButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                isPlacing = !isPlacing;
                currentButton = isPlacing ? accommodationButton : null;
            }
        });

        worldCoordinates = new Vector3();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(100f / 255f, 100f / 255f, 250f / 255f, 1f);

        input();
        logic();
        draw();
    }

    private void input() {
        worldCoordinates.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(worldCoordinates);

        //Switches the layer viewed for debugging
        if (Gdx.input.isKeyJustPressed(Input.Keys.L) && map.getLayers().getCount() > 1) {
            currentLayer = (currentLayer == 0) ? 1 : 0;  // Toggle between layer 0 and 1
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            togglePlacingMode();
        }
        if (Gdx.input.isTouched() && Gdx.input.getY() > 64){
            isPlacing = false;
            highlightTiles.clearHighlight(worldCoordinates, buildingManager.createBuilding("accommodation", 0));
            if (currentButton != null){
                currentButton.setChecked(false);
            }
        }

    }

    private void logic() {
        timer.add(Gdx.graphics.getDeltaTime());

        updateSelectionLayer(worldCoordinates);

        if (isPlacing) {
            highlightTiles.updateHighlight(worldCoordinates, buildingManager.createBuilding("accommodation",0));
        }

        // Get tile information
        TileInfo tileInfo = getCurrentTileInfo();

        int tileX = (int) (worldCoordinates.x / tileSize);
        int tileY = (int) (worldCoordinates.y / tileSize);

        debugString = "FPS: " + Gdx.graphics.getFramesPerSecond() + "  "
            + "Layer: " + currentLayer + "  "
            + "Cell ID: " + tileInfo.getId() + "  ("
            + (tileInfo.isFlippedH() ? "H, " : "")
            + (tileInfo.isFlippedV() ? "V, " : "")
            + tileInfo.getRotation() + ") "
            + "Position: " + tileX + ", " + tileY + " "
            + "Timer:" + timer.getTimeUI();
    }

    private void draw() {
        // Update the camera
        camera.update();

        // Set the view for the renderer using the camera
        renderer.setView(camera);

        // Render the map or background using the renderer
        renderer.render();

        // Start the SpriteBatch for drawing other elements
        game.batch.begin();
        game.font.draw(game.batch, debugString, 10, 20);
        buttonStage.draw();
        game.batch.end();
    }


    private void togglePlacingMode() {
        if (isPlacing) {
            isPlacing = false;
            highlightTiles.clearHighlight(worldCoordinates, buildingManager.createBuilding("accommodation", 0));
        } else {
            isPlacing = true;
        }
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

    private void updateSelectionLayer(Vector3 worldCoordinates){
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
