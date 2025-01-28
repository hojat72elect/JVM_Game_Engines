package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;

/**
 * A viewport that scales the world using {@link Scaling}.
 * <p>
 * {@link Scaling#fit} keeps the aspect ratio by scaling the world up to fit the screen, adding black bars (letterboxing) for the
 * remaining space.
 * <p>
 * {@link Scaling#fill} keeps the aspect ratio by scaling the world up to take the whole screen (some of the world may be off
 * screen).
 * <p>
 * {@link Scaling#stretch} does not keep the aspect ratio, the world is scaled to take the whole screen.
 * <p>
 * {@link Scaling#none} keeps the aspect ratio by using a fixed size world (the world may not fill the screen or some of the world
 * may be off screen).
 */
public class ScalingViewport extends Viewport {
    private Scaling scaling;

    /**
     * Creates a new viewport using a new {@link OrthographicCamera}.
     */
    public ScalingViewport(Scaling scaling, float worldWidth, float worldHeight) {
        this(scaling, worldWidth, worldHeight, new OrthographicCamera());
    }

    public ScalingViewport(Scaling scaling, float worldWidth, float worldHeight, Camera camera) {
        this.scaling = scaling;
        setWorldSize(worldWidth, worldHeight);
        setCamera(camera);
    }

    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        Vector2 scaled = scaling.apply(getWorldWidth(), getWorldHeight(), screenWidth, screenHeight);
        int viewportWidth = Math.round(scaled.x);
        int viewportHeight = Math.round(scaled.y);

        // Center.
        setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);

        apply(centerCamera);
    }

    public Scaling getScaling() {
        return scaling;
    }

    public void setScaling(Scaling scaling) {
        this.scaling = scaling;
    }
}
