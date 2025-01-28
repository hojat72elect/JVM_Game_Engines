package com.badlogic.gdx.backends.android;

import com.badlogic.gdx.ApplicationListener;

/**
 * Implement this listener in your libGDX application additionally to {@link ApplicationListener} if you want receive live
 * wallpaper specific events, ex: MyApplication implements ApplicationListener, AndroidWallpaperListener. The callbacks will only
 * be called if the ApplicationListener is running from an AndroidLiveWallpaperService.
 * <p>
 */
public interface AndroidWallpaperListener {

    /**
     * Called on the rendering thread after the live wallpaper's offset had changed.
     *
     * @param xOffset
     * @param yOffset
     * @param xOffsetStep
     * @param yOffsetStep
     * @param xPixelOffset
     * @param yPixelOffset
     */
    void offsetChange(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset);

    /**
     * Called after 'isPreview' state had changed. First time called just after application initialization.
     *
     * @param isPreview current status, save this value and update always when this method is called if you want track live
     *                  wallpaper isPreview status.
     */
    void previewStateChange(boolean isPreview);

    /**
     * Called in response to an icon dropping on the home screen. Not all Android launcher apps are guaranteed to support this.
     */
    void iconDropped(int x, int y);

}
