/*
 * Copyright 2020-2021 Anton Trushkov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.thelema.lwjgl3


/** This interface encapsulates communication with the graphics processor.
 *
 *
 * If supported by the backend, this interface lets you query the available display modes (graphics resolution and color depth)
 * and change it.
 *
 *
 * @author mzechner
 */
interface Graphics {
    /** @return the width of the framebuffer in physical pixels
     */
    val backBufferWidth: Int

    /** @return the height of the framebuffer in physical pixels
     */
    val backBufferHeight: Int

    /**
     * @return the inset from the left which avoids display cutouts in pixels
     */
    val safeInsetLeft: Int

    /**
     * @return the inset from the top which avoids display cutouts in pixels
     */
    val safeInsetTop: Int

    /**
     * @return the inset from the bottom which avoids display cutouts or floating gesture bars, in pixels
     */
    val safeInsetBottom: Int

    /**
     * @return the inset from the right which avoids display cutouts in pixels
     */
    val safeInsetRight: Int

    /** @return the pixels per inch on the x-axis
     */
    val ppiX: Float

    /** @return the pixels per inch on the y-axis
     */
    val ppiY: Float

    /** @return the pixels per centimeter on the x-axis
     */
    val ppcX: Float

    /** @return the pixels per centimeter on the y-axis.
     */
    val ppcY: Float

    /** This is a scaling factor for the Density Independent Pixel unit, following the same conventions as
     * android.util.DisplayMetrics#density, where one DIP is one pixel on an approximately 160 dpi screen. Thus on a 160dpi screen
     * this density value will be 1; on a 120 dpi screen it would be .75; etc.
     *
     * @return the logical density of the Display.
     */
    val density: Float

    /** Whether the given backend supports a display mode change via calling [Graphics.setFullscreenMode]
     *
     * @return whether display mode changes are supported or not.
     */
    fun supportsDisplayModeChange(): Boolean

    /** @return the primary monitor
     */
    val primaryMonitor: Monitor?

    /** @return the monitor the application's window is located on
     */
    val monitor: Monitor?

    /** @return the currently connected [Monitor]s
     */
    val monitors: Array<Monitor>

    /** @return the supported fullscreen [DisplayMode](s) of the monitor the window is on
     */
    val displayModes: Array<DisplayMode>

    /** @return the supported fullscreen [DisplayMode]s of the given [Monitor]
     */
    fun getDisplayModes(monitor: Monitor): Array<DisplayMode>

    /** @return the current [DisplayMode] of the monitor the window is on.
     */
    val displayMode: DisplayMode

    /** @return the current [DisplayMode] of the given [Monitor]
     */
    fun getDisplayMode(monitor: Monitor): DisplayMode?

    /** Sets the window to full-screen mode.
     *
     * @param displayMode the display mode.
     * @return whether the operation succeeded.
     */
    fun setFullscreenMode(displayMode: DisplayMode = this.displayMode): Boolean

    /** Sets the window to windowed mode.
     *
     * @param width the width in pixels
     * @param height the height in pixels
     * @return whether the operation succeeded
     */
    fun setWindowedMode(width: Int, height: Int): Boolean

    /** Sets the title of the window. Ignored on Android.
     *
     * @param title the title.
     */
    fun setTitle(title: String)

    /** Sets the window decoration as enabled or disabled. On Android, this will enable/disable
     * the menu bar.
     *
     * Note that immediate behavior of this method may vary depending on the implementation. It
     * may be necessary for the window to be recreated in order for the changes to take effect.
     * Consult the documentation for the backend in use for more information.
     *
     * Supported on all GDX desktop backends and on Android (to disable the menu bar).
     *
     * @param undecorated true if the window border or status bar should be hidden. false otherwise.
     */
    fun setUndecorated(undecorated: Boolean)

    /** Sets whether or not the window should be resizable. Ignored on Android.
     *
     * Note that immediate behavior of this method may vary depending on the implementation. It
     * may be necessary for the window to be recreated in order for the changes to take effect.
     * Consult the documentation for the backend in use for more information.
     *
     * Supported on all GDX desktop backends.
     *
     * @param resizable
     */
    fun setResizable(resizable: Boolean)

    /** Enable/Disable vsynching. This is a best-effort attempt which might not work on all platforms.
     *
     * @param vsync vsync enabled or not.
     */
    fun setVSync(vsync: Boolean)

    /** @return the format of the color, depth and stencil buffer in a [BufferFormat] instance
     */
    val bufferFormat: BufferFormat?

    /** @param extension the extension name
     * @return whether the extension is supported
     */
    fun supportsExtension(extension: String): Boolean

    /** Requests a new frame to be rendered if the rendering mode is non-continuous. This method can be called from any thread.  */
    fun requestRendering()

    /** Whether the app is fullscreen or not  */
    val isFullscreen: Boolean
}
