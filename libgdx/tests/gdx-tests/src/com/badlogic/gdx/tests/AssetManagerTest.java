package com.badlogic.gdx.tests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.TimeUtils;

import java.nio.IntBuffer;
import java.util.Locale;

public class AssetManagerTest extends GdxTest implements AssetErrorListener {
    AssetManager manager;
    BitmapFont font;
    Skin skin;
    Label skinLabel;
    SpriteBatch batch;
    int frame = 0;
    int reloads = 0;
    float elapsed = 0;
    boolean diagnosed = false;
    private long start;
    // private TileMapRenderer renderer;
// private TileAtlas atlas;
// private TiledMap map;
// private Texture tex3;
    private BitmapFont font2;
    private BitmapFont multiPageFont;
    private TextureAtlas tex2;
    private Texture tex1;
    private ShaderProgram shader;

    public void create() {
        Gdx.app.setLogLevel(Application.LOG_ERROR);

        Resolution[] resolutions = {new Resolution(320, 480, ".320480"), new Resolution(480, 800, ".480800"),
                new Resolution(480, 856, ".480854")};
        ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutions);
        manager = new AssetManager();
        manager.setLoader(Texture.class, new TextureLoader(resolver));
        manager.setErrorListener(this);
        load();
        Texture.setAssetManager(manager);
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/font.fnt"), false);
        skin = new Skin();
    }

    private void load() {
// Gdx.app.setLogLevel(Logger.DEBUG);
        start = TimeUtils.nanoTime();
        tex1 = new Texture("data/animation.png");
        tex2 = new TextureAtlas(Gdx.files.internal("data/pack.atlas"));
        font2 = new BitmapFont(Gdx.files.internal("data/verdana39.fnt"), false);
// tex3 = new Texture("data/test.etc1");
// map = TiledLoader.createMap(Gdx.files.internal("data/tiledmap/tilemap csv.tmx"));
// atlas = new TileAtlas(map, Gdx.files.internal("data/tiledmap/"));
// renderer = new TileMapRenderer(map, atlas, 8, 8);
        shader = new ShaderProgram(Gdx.files.internal("data/g2d/batchCommon.vert").readString(),
                Gdx.files.internal("data/g2d/monochrome.frag").readString());
        System.out.println("plain took: " + (TimeUtils.nanoTime() - start) / 1000000000.0f);

        start = TimeUtils.nanoTime();
        // this is a test for lazy loading on GWT
        manager.load("data/animation_gwt_lazy.png", Texture.class);
        manager.load("data/animation.png", Texture.class);
// manager.load("data/pack1.png", Texture.class);
        manager.load("data/pack.atlas", TextureAtlas.class);
// manager.load("data/verdana39.png", Texture.class);
        manager.load("data/verdana39.fnt", BitmapFont.class);
// manager.load("data/multipagefont.fnt", BitmapFont.class);

// manager.load("data/test.etc1", Texture.class);
// manager.load("data/tiledmap/tilemap csv.tmx", TileMapRenderer.class, new
// TileMapRendererLoader.TileMapParameter("data/tiledmap/", 8, 8));
        manager.load("data/i18n/message2", I18NBundle.class,
                new I18NBundleLoader.I18NBundleParameter(reloads % 2 == 0 ? Locale.ITALIAN : Locale.ENGLISH));
        manager.load("data/g2d/monochrome.frag", ShaderProgram.class, new ShaderProgramLoader.ShaderProgramParameter() {
            {
                vertexFile = "data/g2d/batchCommon.vert";
            }
        });
        manager.load("data/uiskin.json", Skin.class);
    }

    private void unload() {
        tex1.dispose();
        tex2.dispose();
        font2.dispose();
// tex3.dispose();
// atlas.dispose();
// renderer.dispose();
        shader.dispose();

        manager.unload("data/animation_gwt_lazy.png");
        manager.unload("data/animation.png");
// manager.unload("data/pack1.png");
        manager.unload("data/pack.atlas");
// manager.unload("data/verdana39.png");
        manager.unload("data/verdana39.fnt");
// manager.unload("data/multipagefont.fnt");

// manager.unload("data/test.etc1");
// manager.unload("data/tiledmap/tilemap csv.tmx");
        manager.unload("data/i18n/message2");
        manager.unload("data/g2d/monochrome.frag");
        manager.unload("data/uiskin.json");
    }

    private void invalidateTexture(Texture texture) {
        IntBuffer buffer = BufferUtils.newIntBuffer(1);
        buffer.put(0, texture.getTextureObjectHandle());
        Gdx.gl.glDeleteTextures(1, buffer);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        boolean result = manager.update(16);
        if (result) {
            if (!diagnosed) {
                diagnosed = true;
                System.out.println("took: " + (TimeUtils.nanoTime() - start) / 1000000000.0f);
                elapsed = 0;
            } else {
                elapsed += Gdx.graphics.getDeltaTime();
                if (elapsed > 0.2f) {
                    unload();
                    load();
                    diagnosed = false;
                    reloads++;
                }
            }
        }
        frame++;

        if (manager.isLoaded("data/g2d/monochrome.frag"))
            batch.setShader(manager.get("data/g2d/monochrome.frag", ShaderProgram.class));
        else
            batch.setShader(null);

        batch.begin();
        if (manager.isLoaded("data/animation_gwt_lazy.png"))
            batch.draw(manager.get("data/animation_gwt_lazy.png", Texture.class), 100, 100);
        if (manager.isLoaded("data/animation.png"))
            batch.draw(manager.get("data/animation.png", Texture.class), 100, 100);
        if (manager.isLoaded("data/verdana39.png"))
            batch.draw(manager.get("data/verdana39.png", Texture.class), 300, 100);
        if (manager.isLoaded("data/pack.atlas"))
            batch.draw(manager.get("data/pack.atlas", TextureAtlas.class).findRegion("particle-star"), 164, 100);
        if (manager.isLoaded("data/verdana39.fnt")) {
            manager.get("data/verdana39.fnt", BitmapFont.class).draw(batch, "This is a test", 100, 80);
        }
        if (manager.isLoaded("data/multipagefont.fnt")) {
            manager.get("data/multipagefont.fnt", BitmapFont.class).draw(batch, "This is a test qpRPN multi page!", 100, 80);
        }
// System.out.println(Arrays.toString(manager.getAssetNames().items));

// if (manager.isLoaded("data/test.etc1")) batch.draw(manager.get("data/test.etc1", Texture.class), 0, 0);
// if (manager.isLoaded("data/tiledmap/tilemap csv.tmx")) manager.get("data/tiledmap/tilemap csv.tmx",
// TileMapRenderer.class).render();
        if (manager.isLoaded("data/i18n/message2")) {
            font.draw(batch, manager.get("data/i18n/message2", I18NBundle.class).get("msg"), 100, 400);
        }
        if (manager.isLoaded("data/uiskin.json")) {
            skin = manager.get("data/uiskin.json", Skin.class);
            skinLabel = new Label("label from a Asset Manager skin", skin, "default");
            font.draw(batch, "Skin Loaded", 100, 420);
        }

        font.draw(batch, "loaded: " + manager.getProgress() + ", reloads: " + reloads, 0, 30);
        batch.end();

// if(Gdx.input.justTouched()) {
// Texture.invalidateAllTextures(Gdx.app);
// diagnosed = false;
// unload();
// load();
// }
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error("AssetManagerTest", "Couldn't load asset: " + asset, throwable);
    }

    @Override
    public void dispose() {
        manager.dispose();
        batch.dispose();
        font.dispose();
    }
}
