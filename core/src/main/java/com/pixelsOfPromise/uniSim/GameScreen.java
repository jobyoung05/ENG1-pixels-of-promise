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

import java.util.ArrayList;
import java.util.List;


public class GameScreen implements Screen {
    final UniSim game;

    // Constants
    private static final int TILE_SIZE = 16;
    private static final int MAP_WIDTH = 60;
    private static final int MAP_HEIGHT = 36;
    private static final int SELECTION_TILE = 166;

    // Game state variables
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
    private int[] lastHoveredTile = {0, 0};
    private UIButton currentButton;
    private String debugString;
    private Vector3 worldCoordinates;

    // Building management
    private BuildingManager buildingManager;
    private List<Building> availableBuildings = new ArrayList<>();
    private List<Building> placedBuildings = new ArrayList<>();
    private Building currentBuildingBeingPlaced;
    private HighlightTiles highlightTiles;


    public GameScreen(final UniSim game) {
        this.game = game;
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        // Creates the camera and sets the viewpoint
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        camera.update();

        // Creates the camera controller
//        cameraController = new OrthoCamController(camera, width, height);

        // Buttons and input
        buttonStage = new Stage();
//        InputMultiplexer inputMultiplexer = new InputMultiplexer();
//        inputMultiplexer.addProcessor(cameraController);
        //inputMultiplexer.addProcessor(buttonStage);
        //Gdx.input.setInputProcessor(inputMultiplexer)
        Gdx.input.setInputProcessor(buttonStage);

        // Create the timer
        timer = new Timer();

        // Load the tiles from the texture pack
        tileTexture = new Texture(Gdx.files.internal("galletcity.png"));
        TextureRegion[][] splitTiles = TextureRegion.split(tileTexture, TILE_SIZE, TILE_SIZE);

        // Flatten the 2D array for easier access
        int totalTiles = splitTiles.length * splitTiles[0].length;
        textureRegions = new TextureRegion[totalTiles];
        for (int y = 0; y < splitTiles.length; y++) {
            for (int x = 0; x < splitTiles[y].length; x++) {
                textureRegions[y * splitTiles[y].length + x] = splitTiles[y][x];
            }
        }

        // Loads the premade map
        map = new TmxMapLoader().load("untitled.tmx");

        // Add a selection layer
        TiledMapTileLayer selectionLayer = new TiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, TILE_SIZE, TILE_SIZE);
        map.getLayers().add(selectionLayer);
        selectionLayer.setOpacity(0.7f);
        //map.getLayers().get(1).setVisible(false);

        // Initialize the renderer with the map we just created
        renderer = new OrthogonalTiledMapRenderer(map);

        // Initialize HighlightTiles
        highlightTiles = new HighlightTiles((TiledMapTileLayer) map.getLayers().get(2));

        // Buildings
        buildingManager = new BuildingManager("buildings.json", textureRegions);

//        Building building1 = buildingManager.createBuilding("accommodation", 1000);
//        Building building2 = buildingManager.createBuilding("accommodation", 1000);
//
//        building1.setLocation(10, 10);
//        building2.setLocation(35, 29);
//
//        placedBuildings.add(building1);
//        placedBuildings.add(building2);
//
//        for (Building building : placedBuildings) {
//            building.addToLayer(map);
//        }

        // Setup UI button for accommodation
        UIButton accommodationButton = new UIButton(buttonStage, "Accommodation", "accommodation", 0, (int) height-32, 128, 32);
        accommodationButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                swapPlacementButton(accommodationButton);

                currentBuildingBeingPlaced = buildingManager.createBuilding("accommodation", 1000);
            }
        });

        UIButton teachingButton = new UIButton(buttonStage, "Teaching", "teaching", 0, (int) height-64, 128, 32);
        teachingButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                swapPlacementButton(teachingButton);

                currentBuildingBeingPlaced = buildingManager.createBuilding("teaching", 1);
            }
        });

        // Initialize world coordinates
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
        // Update world coordinates based on input
        worldCoordinates.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(worldCoordinates);

        // Switches the layer viewed for debugging
        if (Gdx.input.isKeyJustPressed(Input.Keys.L) && map.getLayers().getCount() > 1) {
            currentLayer = (currentLayer == 0) ? 1 : 0;  // Toggle between layer 0 and 1
        }

        // Handle touch or mouse click for placing buildings
        if (currentButton != null && Gdx.input.isTouched() && Gdx.input.getY() > 64) {
            placeBuilding();
        }
    }

    private void logic() {
        // Update timer
        timer.add(Gdx.graphics.getDeltaTime());

        // Ends game after 5 minutes
        if(timer.getSeconds() > 300){
            game.setScreen(new EndScreen(game));
            dispose();
        }

        // Update the selection layer
        updateSelectionLayer(worldCoordinates);

        // Update highlighting if placing is active
        if (currentButton != null && currentBuildingBeingPlaced != null) {
            highlightTiles.updateHighlight(worldCoordinates, currentBuildingBeingPlaced);
        }

        // Get tile information
        TileInfo tileInfo = getCurrentTileInfo();

        // Convert world coordinates to tile coordinates
        int tileX = (int) (worldCoordinates.x / TILE_SIZE);
        int tileY = (int) (worldCoordinates.y / TILE_SIZE);

        // Update debug string
        debugString = String.format(
            "FPS: %d  Layer: %d  Cell ID: %d  (%s%s, Rotation: %d) Position: %d, %d Timer: %s",
            Gdx.graphics.getFramesPerSecond(),
            currentLayer,
            tileInfo.getId(),
            tileInfo.isFlippedH() ? "H, " : "",
            tileInfo.isFlippedV() ? "V, " : "",
            tileInfo.getRotation(),
            tileX,
            tileY,
            timer.getTimeUI()
        );
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

    private void placeBuilding() {
        // Validate placement location
        if (isValidPlacement(worldCoordinates)) {
            currentBuildingBeingPlaced.setLocation((int) worldCoordinates.x / TILE_SIZE, (int) worldCoordinates.y / TILE_SIZE);
            currentBuildingBeingPlaced.addToLayer(map);
//            placedBuildings.add(currentBuildingBeingPlaced);
            currentBuildingBeingPlaced = null;
            if (currentButton != null) {
                currentButton.setChecked(false);
                currentButton = null;
            }
        }
    }

    private boolean isValidPlacement(Vector3 coordinates) {
        // Logic here
        return true; // Placeholder
    }

    private void swapPlacementButton(UIButton button) {
        if (currentButton != null){
            currentButton.setChecked(false);
        }
        if (button == currentButton){
            currentButton = null;
        }
        else if (button != null) {
            currentButton = button;
        }
        highlightTiles.clearHighlight();
    }


    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
//        camera.setToOrtho(false, width, height);
//        camera.update();
//        buttonStage.getViewport().update(width, height, true);
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
        buttonStage.dispose();

    }

    /**
     * Retrieves information about the current tile under the cursor.
     *
     * @return TileInfo object containing tile details.
     */
    private TileInfo getCurrentTileInfo() {
        // Convert world coordinates to tile coordinates
        int tileX = (int) (worldCoordinates.x / TILE_SIZE);
        int tileY = (int) (worldCoordinates.y / TILE_SIZE);

        // Validate tile coordinates
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) {
            return new TileInfo(-1, false, false, -1);
        }

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

    /**
     * Updates the selection layer to highlight the currently hovered tile.
     *
     * @param worldCoordinates The world coordinates of the cursor.
     */
    private void updateSelectionLayer(Vector3 worldCoordinates){
        // Convert world coordinates to tile coordinates
        int tileX = (int) (worldCoordinates.x / TILE_SIZE);
        int tileY = (int) (worldCoordinates.y / TILE_SIZE);

        // Validate tile coordinates
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) {
            return;
        }

        // Select the highlighting layer
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);

        // Remove previously selected tile
        layer.setCell(lastHoveredTile[0], lastHoveredTile[1], null);

        // Set the current cell to be SELECTION_TILE
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        StaticTiledMapTile tile = new StaticTiledMapTile(textureRegions[SELECTION_TILE]);
        tile.setId(SELECTION_TILE);  // Explicitly setting the tile ID
        cell.setTile(tile);
        layer.setCell(tileX, tileY, cell);


        // Record the location of the tile we just set
        lastHoveredTile[0] = tileX;
        lastHoveredTile[1] = tileY;
    }
}
