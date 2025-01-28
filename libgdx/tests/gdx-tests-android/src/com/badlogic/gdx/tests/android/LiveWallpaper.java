package com.badlogic.gdx.tests.android;

import android.util.Log;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;
import com.badlogic.gdx.backends.android.AndroidWallpaperListener;
import com.badlogic.gdx.tests.MeshShaderTest;

public class LiveWallpaper extends AndroidLiveWallpaperService {

    @Override
    public void onCreateApplication() {
        super.onCreateApplication();

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        ApplicationListener listener = new MyLiveWallpaperListener();
        initialize(listener, config);
    }

    // implement AndroidWallpaperListener additionally to ApplicationListener
    // if you want to receive callbacks specific to live wallpapers
    public static class MyLiveWallpaperListener extends MeshShaderTest implements AndroidWallpaperListener {

        @Override
        public void offsetChange(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset,
                                 int yPixelOffset) {
            Log.i("LiveWallpaper test", "offsetChange(xOffset:" + xOffset + " yOffset:" + yOffset + " xOffsetSteep:" + xOffsetStep
                    + " yOffsetStep:" + yOffsetStep + " xPixelOffset:" + xPixelOffset + " yPixelOffset:" + yPixelOffset + ")");
        }

        @Override
        public void previewStateChange(boolean isPreview) {
            Log.i("LiveWallpaper test", "previewStateChange(isPreview:" + isPreview + ")");
        }

        @Override
        public void iconDropped(int x, int y) {
            Log.i("LiveWallpaper test", "iconDropped (" + x + ", " + y + ")");
        }

    }
}
