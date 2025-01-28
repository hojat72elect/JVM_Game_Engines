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

import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import kotlin.math.max
import kotlin.math.min


class Lwjgl3Graphics(val window: Lwjgl3Window) : Graphics {
    val lwjglGL = Lwjgl3GL()

    @Volatile
    override var backBufferWidth = 0
    @Volatile
    override var backBufferHeight = 0
    @Volatile
    var logicalWidth = 0
        private set
    @Volatile
    var logicalHeight = 0
        private set
    override var bufferFormat: BufferFormat? = null
    private var windowPosXBeforeFullscreen = 0
    private var windowPosYBeforeFullscreen = 0
    private var displayModeBeforeFullscreen: DisplayMode? = null

    override val safeInsetLeft: Int
        get() = 0

    override val safeInsetBottom: Int
        get() = 0

    override val safeInsetRight: Int
        get() = 0

    override val safeInsetTop: Int
        get() = 0

    override val isFullscreen: Boolean
        get() = GLFW.glfwGetWindowMonitor(window.windowHandle) != 0L

    override val ppiX: Float
        get() = ppcX / 0.393701f

    override val ppiY: Float
        get() = ppcY / 0.393701f

    override val ppcX: Float
        get() {
            val monitor = monitor as Lwjgl3Monitor
            GLFW.glfwGetMonitorPhysicalSize(monitor.monitorHandle, tmpBuffer, tmpBuffer2)
            val sizeX = tmpBuffer[0]
            val mode = displayMode
            return mode.width / sizeX.toFloat() * 10
        }

    override val ppcY: Float
        get() {
            val monitor = monitor as Lwjgl3Monitor
            GLFW.glfwGetMonitorPhysicalSize(monitor.monitorHandle, tmpBuffer, tmpBuffer2)
            val sizeY = tmpBuffer2[0]
            val mode = displayMode
            return mode.height / sizeY.toFloat() * 10
        }

    override val density: Float
        get() = ppiX / 160f

    override val primaryMonitor: Monitor?
        get() = Lwjgl3WindowConf.toLwjgl3Monitor(GLFW.glfwGetPrimaryMonitor())

    override val monitor: Monitor
        get() {
            val monitors = monitors
            var result = monitors[0]
            GLFW.glfwGetWindowPos(window.windowHandle, tmpBuffer, tmpBuffer2)
            val windowX = tmpBuffer[0]
            val windowY = tmpBuffer2[0]
            GLFW.glfwGetWindowSize(window.windowHandle, tmpBuffer, tmpBuffer2)
            val windowWidth = tmpBuffer[0]
            val windowHeight = tmpBuffer2[0]
            var overlap: Int
            var bestOverlap = 0
            for (monitor in monitors) {
                val mode = getDisplayMode(monitor)
                overlap = (max(0,
                        min(windowX + windowWidth, monitor.virtualX + mode.width)
                                - max(windowX, monitor.virtualX))
                        * max(0, min(windowY + windowHeight, monitor.virtualY + mode.height)
                        - max(windowY, monitor.virtualY)))
                if (bestOverlap < overlap) {
                    bestOverlap = overlap
                    result = monitor
                }
            }
            return result
        }

    override val displayMode: DisplayMode
        get() = Lwjgl3WindowConf.getDisplayMode(monitor)

    override val monitors: Array<Monitor>
        get() {
            val glfwMonitors = GLFW.glfwGetMonitors()
            return Array(glfwMonitors!!.limit()) { Lwjgl3WindowConf.toLwjgl3Monitor(glfwMonitors[it]) }
        }

    override val displayModes: Array<DisplayMode>
        get() = Lwjgl3WindowConf.getDisplayModes(monitor)

    var tmpBuffer = BufferUtils.createIntBuffer(1)
    var tmpBuffer2 = BufferUtils.createIntBuffer(1)
    private val resizeCallback: GLFWFramebufferSizeCallback = object : GLFWFramebufferSizeCallback() {
        override fun invoke(windowHandle: Long, width: Int, height: Int) {
            updateFramebufferInfo()
            if (!window.isListenerInitialized) {
                return
            }
            window.makeCurrent()
            lwjglGL.glViewport(0, 0, width, height)
            GLFW.glfwSwapBuffers(windowHandle)
        }
    }

    private fun updateFramebufferInfo() {
        GLFW.glfwGetFramebufferSize(window.windowHandle, tmpBuffer, tmpBuffer2)
        lwjglGL.mainFrameBufferWidth = tmpBuffer[0]
        lwjglGL.mainFrameBufferHeight = tmpBuffer2[0]
        backBufferWidth = tmpBuffer[0]
        backBufferHeight = tmpBuffer2[0]
        GLFW.glfwGetWindowSize(window.windowHandle, tmpBuffer, tmpBuffer2)
        logicalWidth = tmpBuffer[0]
        logicalHeight = tmpBuffer2[0]
        val config = window.config
        bufferFormat = BufferFormat(
            config.redBits, config.greenBits, config.blueBits, config.alphaBits, config.depthBits, config.stencilBits,
            config.msaaSamples, false
        )
    }

    override fun supportsDisplayModeChange(): Boolean {
        return true
    }

    override fun getDisplayModes(monitor: Monitor): Array<DisplayMode> {
        return Lwjgl3WindowConf.getDisplayModes(monitor)
    }

    override fun getDisplayMode(monitor: Monitor): DisplayMode {
        return Lwjgl3WindowConf.getDisplayMode(monitor)
    }

    override fun setFullscreenMode(displayMode: DisplayMode): Boolean {
        val newMode = displayMode as Lwjgl3DisplayMode
        if (isFullscreen) {
            val currentMode = getDisplayMode(monitor) as Lwjgl3DisplayMode
            if (currentMode.monitor == newMode.monitor && currentMode.refreshRate == newMode.refreshRate) { // same monitor and refresh rate
                GLFW.glfwSetWindowSize(window.windowHandle, newMode.width, newMode.height)
            } else { // different monitor and/or refresh rate
                GLFW.glfwSetWindowMonitor(window.windowHandle, newMode.monitor,
                        0, 0, newMode.width, newMode.height, newMode.refreshRate)
            }
        } else { // store window position so we can restore it when switching from fullscreen to windowed later
            storeCurrentWindowPositionAndDisplayMode()
            // switch from windowed to fullscreen
            GLFW.glfwSetWindowMonitor(window.windowHandle, newMode.monitor,
                    0, 0, newMode.width, newMode.height, newMode.refreshRate)
        }
        updateFramebufferInfo()
        return true
    }

    private fun storeCurrentWindowPositionAndDisplayMode() {
        windowPosXBeforeFullscreen = window.positionX
        windowPosYBeforeFullscreen = window.positionY
        displayModeBeforeFullscreen = displayMode
    }

    override fun setWindowedMode(width: Int, height: Int): Boolean {
        if (!isFullscreen) {
            GLFW.glfwSetWindowSize(window.windowHandle, width, height)
        } else {
            if (displayModeBeforeFullscreen == null) {
                storeCurrentWindowPositionAndDisplayMode()
            }
            GLFW.glfwSetWindowMonitor(window.windowHandle, 0,
                    windowPosXBeforeFullscreen, windowPosYBeforeFullscreen, width, height,
                    displayModeBeforeFullscreen!!.refreshRate)
        }
        updateFramebufferInfo()
        return true
    }

    override fun setTitle(title: String) {
        GLFW.glfwSetWindowTitle(window.windowHandle, title)
    }

    override fun setUndecorated(undecorated: Boolean) {
        val config = window.config
        config.decorated = !undecorated
        GLFW.glfwSetWindowAttrib(window.windowHandle, GLFW.GLFW_DECORATED, if (undecorated) GLFW.GLFW_FALSE else GLFW.GLFW_TRUE)
    }

    override fun setResizable(resizable: Boolean) {
        val config = window.config
        config.resizable = resizable
        GLFW.glfwSetWindowAttrib(window.windowHandle, GLFW.GLFW_RESIZABLE, if (resizable) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)
    }

    override fun setVSync(vsync: Boolean) {
        GLFW.glfwSwapInterval(if (vsync) 1 else 0)
    }

    override fun supportsExtension(extension: String): Boolean {
        return GLFW.glfwExtensionSupported(extension)
    }

    override fun requestRendering() {
        window.requestRendering()
    }

    fun dispose() {
        resizeCallback.free()
    }

    init {
        updateFramebufferInfo()
        GLFW.glfwSetFramebufferSizeCallback(window.windowHandle, resizeCallback)
    }
}
