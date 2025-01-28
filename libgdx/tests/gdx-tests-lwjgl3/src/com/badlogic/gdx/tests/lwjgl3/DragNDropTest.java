package com.badlogic.gdx.tests.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Tests for GLFW's drop callback.
 * <p>
 * External files (e.g from the desktop) can be dragged into the GLFW window.
 */
public class DragNDropTest extends GdxTest {

    private Skin skin;
    private Stage stage;
    private Table root;

    public static void main(String[] argv) throws SecurityException {
        final DragNDropTest test = new DragNDropTest();

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(640, 480);
        config.setTitle("Drag files in this window");
        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public void filesDropped(String[] files) {
                for (String file : files) {
                    Gdx.app.log("GLWF Drop", file);
                }
                test.addFiles(files);
            }

        });

        new Lwjgl3Application(test, config);
    }

    @Override
    public void create() {
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        root = new Table();
        root.setFillParent(true);
        root.align(Align.left | Align.top);
        stage.addActor(root);
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 0, 0, 1);

        stage.act();
        stage.draw();
    }

    public void addFiles(String[] files) {
        for (String file : files) {
            root.add(new Label(file, skin)).left().row();
        }
    }

}
