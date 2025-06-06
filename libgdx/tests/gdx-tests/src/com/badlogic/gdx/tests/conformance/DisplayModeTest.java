package com.badlogic.gdx.tests.conformance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.tests.utils.GdxTest;

import java.util.Arrays;

public class DisplayModeTest extends GdxTest {

    @Override
    public void create() {
        DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        DisplayMode displayModeForMonitor = Gdx.graphics.getDisplayMode(Gdx.graphics.getMonitor());
        DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();
        DisplayMode[] displayModesForMonitor = Gdx.graphics.getDisplayModes(Gdx.graphics.getMonitor());

        Gdx.app.log("DisplayModeTest", "Display mode (using Gdx.graphics.getDisplayMode() ) : " + displayMode);
        Gdx.app.log("DisplayModeTest",
                "Display mode (using Gdx.graphics.getDisplayMode(Gdx.graphics.getMonitor()) ) : " + Arrays.toString(displayModes));
        Gdx.app.log("DisplayModeTest",
                "Display mode (using Gdx.graphics.getDisplayModes() ) : " + Arrays.toString(displayModesForMonitor));
        Gdx.app.log("DisplayModeTest",
                "Display mode (using Gdx.graphics.getDisplayModes(Gdx.graphics.getMonitor()) ): " + displayModeForMonitor);
        assertDisplayModeEquals(displayMode, displayModeForMonitor);
        assertDisplayModesEquals(displayModes, displayModesForMonitor);
    }

    void assertDisplayModesEquals(DisplayMode[] a, DisplayMode[] b) {
        if (a.length == 0 || b.length == 0) throw new AssertionError("Argument a or b can't be a zero length array");
        if (a.length != b.length) {
            throw new AssertionError("Display modes " + Arrays.toString(a) + " aren't equal to display modes " + Arrays.toString(b));
        }
        boolean equal = true;
        for (int i = 0; i < a.length; i++) {
            equal = equal && isDisplayModeEqual(a[i], b[i]);
        }
        if (!equal) {
            throw new AssertionError("Display modes " + Arrays.toString(a) + " aren't equal to display modes " + Arrays.toString(b));
        }
    }

    void assertDisplayModeEquals(DisplayMode a, DisplayMode b) {
        if (!isDisplayModeEqual(a, b)) {
            throw new AssertionError(a + " isn't equal to " + b);
        }
    }

    boolean isDisplayModeEqual(DisplayMode a, DisplayMode b) {
        if (a == null || b == null) return false;
        boolean equal = a.bitsPerPixel == b.bitsPerPixel && a.height == b.height && a.refreshRate == b.refreshRate
                && a.width == b.width;
        return equal;
    }
}
