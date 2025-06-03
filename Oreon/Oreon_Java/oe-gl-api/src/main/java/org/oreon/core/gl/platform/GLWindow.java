package org.oreon.core.gl.platform;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.WGLEXTSwapControl;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.platform.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

public class GLWindow extends Window {

    public GLWindow() {

        super(GLContext.getConfig().getDisplayTitle(),
                GLContext.getConfig().getWindowWidth(), GLContext.getConfig().getWindowHeight());
    }

    public void create() {
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        setId(glfwCreateWindow(getWidth(), getHeight(), getTitle(), 0, 0));

        if (getId() == 0) {
            throw new RuntimeException("Failed to create window");
        }

        setIcon("textures/logo/oreon_lwjgl_icon32.png");

        glfwMakeContextCurrent(getId());

        glfwSwapInterval(0);

        if (BaseContext.getConfig().isGlfwGLVSync()) {
            WGLEXTSwapControl.wglSwapIntervalEXT(1);
            glfwSwapInterval(1);
        }

        GL.createCapabilities();
    }

    public void show() {
        glfwShowWindow(getId());
    }

    public void draw() {
        glfwSwapBuffers(getId());
    }

    public void shutdown() {
        glfwDestroyWindow(getId());
    }

    public boolean isCloseRequested() {
        return glfwWindowShouldClose(getId());
    }

    public void resize(int width, int height) {
        glfwSetWindowSize(getId(), width, height);
        setHeight(height);
        setWidth(width);
        BaseContext.getConfig().setWindowWidth(width);
        BaseContext.getConfig().setWindowHeight(height);
        // TODO set camera projection
    }
}
