package com.badlogic.gdx.tests.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Demonstrates how to let a sprite follow a finger touching the screen.
 */
public class MoveSpriteExample extends GdxTest {
    Texture texture;
    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 spritePosition = new Vector3();

    public void create() {
        // create a SpriteBatch with which to render the sprite
        batch = new SpriteBatch();

        // load the sprite's texture. note: usually you have more than
        // one sprite in a texture, see {@see TextureAtlas} and {@see TextureRegion}.
        texture = new Texture(Gdx.files.internal("data/bobargb8888-32x32.png"));

        // create an {@link OrthographicCamera} which is used to transform
        // touch coordinates to world coordinates.
        camera = new OrthographicCamera();

        // we want the camera to setup a viewport with pixels as units, with the
        // y-axis pointing upwards. The origin will be in the lower left corner
        // of the screen.
        camera.setToOrtho(false);
    }

    public void render() {
        // set the clear color and clear the screen.
        ScreenUtils.clear(1, 1, 1, 1);

        // draw the sprite
        batch.begin();
        batch.draw(texture, spritePosition.x, spritePosition.y);
        batch.end();

        // if a finger is down, set the sprite's x/y coordinate.
        if (Gdx.input.isTouched()) {
            // the unproject method takes a Vector3 in window coordinates (origin in
            // upper left corner, y-axis pointing down) and transforms it to world
            // coordinates.
            camera.unproject(spritePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        }
    }
}
