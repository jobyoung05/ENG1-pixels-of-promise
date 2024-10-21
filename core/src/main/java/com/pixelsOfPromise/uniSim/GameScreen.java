package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.Gdx;
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

public class GameScreen implements Screen {
    final UniSim game;

    private TiledMap map;
    private TiledMapRenderer renderer;
    private Texture tiles;
    private final int tileSize = 16;
    private OrthographicCamera camera;
    private OrthoCamController cameraController;

    public GameScreen(final UniSim game) {
        this.game = game;

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        camera.update();

        //cameraController = new OrthoCamController(camera);
        Gdx.input.setInputProcessor(cameraController);
        {
            tiles = new Texture(Gdx.files.internal("galletcity.png"));
            TextureRegion[][] splitTiles = TextureRegion.split(tiles, tileSize, tileSize);
            map = new TiledMap();

            MapLayers layers = map.getLayers();

            for (int l = 0; l < 20; l++) {
                TiledMapTileLayer layer = new TiledMapTileLayer(150, 150, tileSize, tileSize);
                for (int x = 0; x < 150; x++) {
                    for (int y = 0; y < 100; y++) {
                        int ty = (int)(Math.random() * splitTiles.length);
                        int tx = (int)(Math.random() * splitTiles[ty].length);
                        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                        cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
                        layer.setCell(x, y, cell);
                    }
                }
                layers.add(layer);

            }

            // Initialize the renderer with the map
            renderer = new OrthogonalTiledMapRenderer(map);
        }
        map = new TmxMapLoader().load("untitled.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(100f / 255f, 100f / 255f, 250f / 255f, 1f);
        camera.update();

        // Render the map
        renderer.setView(camera);
        renderer.render();

        // Render the FPS
        game.batch.begin();
        game.font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        game.batch.end();
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
}
