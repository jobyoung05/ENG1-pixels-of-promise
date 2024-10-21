package com.pixelsOfPromise.uniSim;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Gdx;

public class OrthoCamController extends InputAdapter {
    final OrthographicCamera camera;
    final Vector3 current = new Vector3();
    final Vector3 last = new Vector3(-1, -1, -1);
    final Vector3 delta = new Vector3();

    private float zoomSpeed = 0.1f; // The speed at which zooming occurs
    private float minZoom = 0.1f;   // Minimum zoom level (zoomed in)
    private float maxZoom = 5f;     // Maximum zoom level (zoomed out)

    public OrthoCamController(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        camera.unproject(current.set(x, y, 0));
        if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
            camera.unproject(delta.set(last.x, last.y, 0));
            delta.sub(current);
            camera.position.add(delta.x, delta.y, 0);
        }
        last.set(x, y, 0);
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        last.set(-1, -1, -1);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Get the mouse position in world coordinates before zooming
        Vector3 mousePosBeforeZoom = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        // Apply zoom
        camera.zoom += amountY * zoomSpeed;

        // Clamp the zoom to stay within minZoom and maxZoom values
        camera.zoom = Math.max(minZoom, Math.min(maxZoom, camera.zoom));

        // Update the camera projection matrix
        camera.update();

        // Get the mouse position in world coordinates after zooming
        Vector3 mousePosAfterZoom = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        // Calculate the difference between mouse positions due to zooming
        Vector3 zoomDelta = mousePosBeforeZoom.sub(mousePosAfterZoom);

        // Move the camera by the difference to zoom towards the mouse pointer
        camera.position.add(zoomDelta.x, zoomDelta.y, 0);

        return true;
    }
}
