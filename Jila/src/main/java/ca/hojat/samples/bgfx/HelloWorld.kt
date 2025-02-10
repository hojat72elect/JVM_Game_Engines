package ca.hojat.samples.bgfx

import org.lwjgl.bgfx.BGFX
import org.lwjgl.bgfx.BGFXInit
import org.lwjgl.bgfx.BGFXResolution
import org.lwjgl.glfw.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.Platform
import kotlin.math.max


/**
 * This is the kotlin version of the `hello world` example from bgfx documentation
 * which is available [here](https://github.com/bkaradzic/bgfx/tree/master/examples/00-helloworld).
 */
fun main() {
    val width = 1_024
    val height = 480

    GLFWErrorCallback.createThrow().set()
    if (GLFW.glfwInit().not())
        throw RuntimeException("Error initializing GLFW")

    // the client (renderer) API is managed by bgfx
    GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API)
    if (Platform.get() == Platform.MACOSX)
        GLFW.glfwWindowHint(GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW.GLFW_FALSE)

    val window = GLFW.glfwCreateWindow(width, height, "25-C99", 0, 0)
    if (window == MemoryUtil.NULL)
        throw RuntimeException("Error creating GLFW window")

    GLFW.glfwSetKeyCallback(window) { windowHnd: Long, key: Int, scancode: Int, action: Int, mods: Int ->
        if (action != GLFW.GLFW_RELEASE) {
            return@glfwSetKeyCallback
        }
        when (key) {
            GLFW.GLFW_KEY_ESCAPE -> GLFW.glfwSetWindowShouldClose(windowHnd, true)
        }
    }

    MemoryStack.stackPush().use { stack ->
        val init = BGFXInit.malloc(stack)
        BGFX.bgfx_init_ctor(init)
        init.resolution { it: BGFXResolution ->
            it.width(width)
                .height(height)
                .reset(BGFX.BGFX_RESET_VSYNC)
        }

        when (Platform.get()) {
            Platform.FREEBSD, Platform.LINUX -> init.platformData()
                .ndt(GLFWNativeX11.glfwGetX11Display())
                .nwh(GLFWNativeX11.glfwGetX11Window(window))

            Platform.MACOSX -> init.platformData()
                .nwh(GLFWNativeCocoa.glfwGetCocoaWindow(window))

            Platform.WINDOWS -> init.platformData()
                .nwh(GLFWNativeWin32.glfwGetWin32Window(window))
        }
        if (BGFX.bgfx_init(init).not())
            throw RuntimeException("Error initializing bgfx renderer")

    }

    println("bgfx renderer: ${BGFX.bgfx_get_renderer_name(BGFX.bgfx_get_renderer_type())}")


    // Enable debug text.
    BGFX.bgfx_set_debug(BGFX.BGFX_DEBUG_TEXT)

    BGFX.bgfx_set_view_clear(0, BGFX.BGFX_CLEAR_COLOR or BGFX.BGFX_CLEAR_DEPTH, 0x303030ff, 1.0f, 0)

    val logo = Logo.createLogo()

    while (GLFW.glfwWindowShouldClose(window).not()) {
        GLFW.glfwPollEvents()

        // Set view 0 default viewport.
        BGFX.bgfx_set_view_rect(0, 0, 0, width, height)

        // This dummy draw call is here to make sure that view 0 is cleared
        // if no other draw calls are submitted to view 0.
        BGFX.bgfx_touch(0)

        // Use debug font to print information about this example.
        BGFX.bgfx_dbg_text_clear(0, false)
        BGFX.bgfx_dbg_text_image(max(width / 2 / 8, 20) - 20, max(height / 2 / 16, 6) - 6, 40, 12, logo, 160)
        BGFX.bgfx_dbg_text_printf(0, 1, 0x1f, "bgfx/examples/25-c99")
        BGFX.bgfx_dbg_text_printf(0, 2, 0x3f, "Description: Initialization and debug text with C99 API.")

        BGFX.bgfx_dbg_text_printf(0, 3, 0x0f, "Color can be changed with ANSI \u001b[9;me\u001b[10;ms\u001b[11;mc\u001b[12;ma\u001b[13;mp\u001b[14;me\u001b[0m code too.")

        BGFX.bgfx_dbg_text_printf(80, 4, 0x0f, "\u001b[;0m    \u001b[;1m    \u001b[; 2m    \u001b[; 3m    \u001b[; 4m    \u001b[; 5m    \u001b[; 6m    \u001b[; 7m    \u001b[0m")
        BGFX.bgfx_dbg_text_printf(80, 5, 0x0f, "\u001b[;8m    \u001b[;9m    \u001b[;10m    \u001b[;11m    \u001b[;12m    \u001b[;13m    \u001b[;14m    \u001b[;15m    \u001b[0m")

        // Advance to next frame. Rendering thread will be kicked to
        // process submitted rendering primitives.
        BGFX.bgfx_frame(false)
    }

    BGFX.bgfx_shutdown()

    Callbacks.glfwFreeCallbacks(window)
    GLFW.glfwDestroyWindow(window)

    GLFW.glfwTerminate()
    GLFW.glfwSetErrorCallback(null)!!.free()
}