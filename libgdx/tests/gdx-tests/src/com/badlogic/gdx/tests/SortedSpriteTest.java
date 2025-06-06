package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Comparator;

/**
 * Demonstrates how to do simple z-sorting of sprites
 */
public class SortedSpriteTest extends GdxTest {
    /**
     * spritebatch used for rendering
     **/
    SpriteBatch batch;
    /**
     * the texture used by the sprites
     **/
    Texture texture;
    /**
     * array of sprites
     **/
    Array<MySprite> sprites = new Array<MySprite>();
    /**
     * a comparator, we keep it around so the GC shuts up
     **/
    MySpriteComparator comparator = new MySpriteComparator();

    @Override
    public void create() {
        // create the SpriteBatch
        batch = new SpriteBatch();

        // load a texture, usually you dispose of this
        // eventually.
        texture = new Texture("data/badlogicsmall.jpg");

        // create 100 sprites, tinted red, from dark to light.
        // red color component is also used as z-value so we
        // can see that the sorting works.
        for (int i = 0; i < 100; i++) {
            // create the sprite and set a random position
            MySprite sprite = new MySprite(texture);
            sprite.setPosition(MathUtils.random() * Gdx.graphics.getWidth(), MathUtils.random() * Gdx.graphics.getHeight());

            // create a random z coordinate in the range 0-1
            sprite.z = MathUtils.random();

            // set the tinting color to the z coordinate as well
            // for visual inspection
            sprite.setColor(sprite.z, 0, 0, 1);

            // add the sprite to the array
            sprites.add(sprite);
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        // sort the sprites (not necessary if we know
        // the are already sorted).
        sprites.sort(comparator);

        // draw the sprites
        batch.begin();
        for (MySprite sprite : sprites) {
            sprite.draw(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
    }

    /**
     * Sprite based class that adds a z-coordinate for depth sorting. Note that allt he constructors were auto-generated in
     * Eclipse (alt + shift + s, c).
     */
    public class MySprite extends Sprite {
        public float z;

        public MySprite() {
            super();
        }

        public MySprite(Sprite sprite) {
            super(sprite);
        }

        public MySprite(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
            super(texture, srcX, srcY, srcWidth, srcHeight);
        }

        public MySprite(Texture texture, int srcWidth, int srcHeight) {
            super(texture, srcWidth, srcHeight);
        }

        public MySprite(Texture texture) {
            super(texture);
        }

        public MySprite(TextureRegion region, int srcX, int srcY, int srcWidth, int srcHeight) {
            super(region, srcX, srcY, srcWidth, srcHeight);
        }

        public MySprite(TextureRegion region) {
            super(region);
        }
    }

    /**
     * Comparator used for sorting, sorts in ascending order (biggset z to smallest z).
     */
    public class MySpriteComparator implements Comparator<MySprite> {
        @Override
        public int compare(MySprite sprite1, MySprite sprite2) {
            return (sprite2.z - sprite1.z) > 0 ? 1 : -1;
        }
    }
}
