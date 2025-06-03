package org.oreon.core.vk.platform;

import org.oreon.core.platform.Window;
import org.oreon.core.vk.context.VkContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

public class VkWindow extends Window {

    public VkWindow() {

        super(VkContext.getConfig().getDisplayTitle(),
                VkContext.getConfig().getWindowWidth(), VkContext.getConfig().getWindowHeight());
    }

    @Override
    public void create() {

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);

        setId(glfwCreateWindow(getWidth(), getHeight(), getTitle(), 0, 0));

        if (getId() == 0) {
            throw new RuntimeException("Failed to create window");
        }

        setIcon("textures/logo/oreon_lwjgl_icon32.png");
    }

    @Override
    public void show() {
        glfwShowWindow(getId());
    }

    @Override
    public void draw() {
    }

    @Override
    public void shutdown() {

        glfwDestroyWindow(getId());
    }

    @Override
    public boolean isCloseRequested() {

        return glfwWindowShouldClose(getId());
    }

    @Override
    public void resize(int x, int y) {
        // TODO Auto-generated method stub

    }

}
