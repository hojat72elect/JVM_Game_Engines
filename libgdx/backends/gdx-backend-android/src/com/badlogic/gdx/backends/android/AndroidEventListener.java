package com.badlogic.gdx.backends.android;

import android.content.Intent;

/**
 * A listener for special Android events such onActivityResult(...). This can be used by e.g. extensions to plug into the Android
 * system.
 */
public interface AndroidEventListener {

    /**
     * Will be called if the application's onActivityResult(...) method is called.
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
