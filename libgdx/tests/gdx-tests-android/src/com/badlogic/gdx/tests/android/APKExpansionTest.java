package com.badlogic.gdx.tests.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * To use this test, an APK expansion file must be present on the device
 * run: gradlew tests:gdx-tests-android:uploadOBB
 * to generate and upload the aforementioned file
 */
public class APKExpansionTest extends GdxTest {
    FileHandleResolver resolver;
    Sound sound;
    SpriteBatch batch;
    Texture texture;
    TextureRegion atlasTextureRegion;
    AssetManager assetManager;

    /**
     * The OBB zip assets are taken from the android tests assets directory Extension prefix is prepended to each asset and zipped
     * up into a obb. Extension prefix is required so we can be certain the assets are coming from the obb, while keeping the
     * structure of the assets file tree for easier testing
     **/
    String extensionPrefix = "obbasset-";

    @Override
    public void create() {
        if ((((AndroidFiles) Gdx.files)).setAPKExpansion(1, 0)) {
            resolver = new ZipFileHandleResolver();
        } else {
            Gdx.app.error("libgdx", "No Expansion can be opened");
        }

        assetManager = new AssetManager();
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        loadFont(assetManager, "data/DroidSerif-Regular.ttf", 12);
        loadFont(assetManager, "data/" + extensionPrefix + "DroidSerif-Regular.ttf", 12);

        assetManager.load("data/" + extensionPrefix + "testpackobb", TextureAtlas.class);
        assetManager.finishLoading();

        sound = Gdx.audio.newSound(Gdx.files.internal("data/" + extensionPrefix + "chirp.ogg"));
        sound.play();
        texture = new Texture(resolver.resolve("data/" + extensionPrefix + "badlogic.jpg"));
        batch = new SpriteBatch();
        TextureAtlas atlas = assetManager.get("data/" + extensionPrefix + "testpackobb");
        atlasTextureRegion = new TextureRegion(atlas.findRegion("water"));
        sound = Gdx.audio.newSound(Gdx.files.internal("data/shotgun.ogg"));
        sound.play();
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.RED);
        batch.begin();
        batch.draw(texture, 0, 0);
        batch.draw(atlasTextureRegion, 0, 0);
        batch.end();
    }

    private void loadFont(AssetManager assetManager, String fontName, int size) {
        FreeTypeFontLoaderParameter param = new FreeTypeFontLoaderParameter();
        param.fontFileName = fontName;
        param.fontParameters.size = 12;
        param.fontParameters.genMipMaps = true;
        param.fontParameters.minFilter = TextureFilter.MipMapLinearLinear;
        param.fontParameters.magFilter = TextureFilter.Linear;
        assetManager.load(fontName + "size" + size + ".ttf", BitmapFont.class, param);
    }
}
