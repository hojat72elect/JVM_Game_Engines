package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO.PNG;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.tests.utils.GdxTest;

import java.io.IOException;

public class PngTest extends GdxTest {
    SpriteBatch batch;
    Texture badlogic, screenshot;

    public void create() {
        batch = new SpriteBatch();
        badlogic = new Texture(Gdx.files.internal("data/badlogic.jpg"));
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (screenshot == null) {
            int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
            for (int i = 0; i < 100; i++)
                batch.draw(badlogic, MathUtils.random(width), MathUtils.random(height));
            batch.flush();

            FileHandle file = FileHandle.tempFile("screenshot-");
            System.out.println(file.file().getAbsolutePath());
            Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            try {
                PNG writer = new PNG((int) (pixmap.getWidth() * pixmap.getHeight() * 1.5f));
                // writer.setCompression(Deflater.NO_COMPRESSION);
                writer.write(file, pixmap);
                writer.write(file, pixmap); // Write twice to make sure the object is reusable.
                writer.dispose();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            screenshot = new Texture(file);
        }
        batch.draw(screenshot, 0, 0);
        batch.end();
    }
}
