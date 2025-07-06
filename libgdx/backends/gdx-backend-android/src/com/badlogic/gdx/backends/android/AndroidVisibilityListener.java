package com.badlogic.gdx.backends.android;

import android.view.View;

/**
 * Allows immersive mode support while maintaining compatibility with Android versions before API Level 19 (4.4)
 */
public class AndroidVisibilityListener {

    public void createListener(final AndroidApplicationBase application) {
        try {
            View rootView = application.getApplicationWindow().getDecorView();
            rootView.setOnSystemUiVisibilityChangeListener(arg0 -> application.getHandler().post(() -> application.useImmersiveMode(true)));
        } catch (Throwable t) {
            application.log("AndroidApplication", "Can't create OnSystemUiVisibilityChangeListener, unable to use immersive mode.",
                    t);
        }
    }
}
