package com.badlogic.gdx.tests.lwjgl3;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

public class GlfwTest {
    private static final GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    public static void main(String[] argv) {
        GLFW.glfwSetErrorCallback(errorCallback);
        if (!glfwInit()) {
            System.out.println("Couldn't initialize GLFW");
            System.exit(-1);
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        // fullscreen, not current resolution, fails
        Buffer modes = glfwGetVideoModes(glfwGetPrimaryMonitor());
        for (int i = 0; i < java.util.Objects.requireNonNull(modes).limit(); i++) {
            System.out.println(modes.get(i).width() + "x" + modes.get(i).height());
        }
        GLFWVidMode mode = modes.get(7);
        System.out.println("Mode: " + mode.width() + "x" + mode.height());
        long windowHandle = glfwCreateWindow(mode.width(), mode.height(), "Test", glfwGetPrimaryMonitor(), 0);
        if (windowHandle == 0) {
            throw new RuntimeException("Couldn't create window");
        }
        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();
        glfwSwapInterval(1);
        glfwShowWindow(windowHandle);

        IntBuffer tmp = BufferUtils.createIntBuffer(1);
        IntBuffer tmp2 = BufferUtils.createIntBuffer(1);

        int fbWidth = 0;
        int fbHeight = 0;

        while (!glfwWindowShouldClose(windowHandle)) {
            glfwGetFramebufferSize(windowHandle, tmp, tmp2);
            if (fbWidth != tmp.get(0) || fbHeight != tmp2.get(0)) {
                fbWidth = tmp.get(0);
                fbHeight = tmp2.get(0);
                System.out.println("Framebuffer: " + tmp.get(0) + "x" + tmp2.get(0));
// GL11.glViewport(0, 0, tmp.get(0) * 2, tmp2.get(0) * 2);
            }
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glVertex2f(-1f, -1f);
            GL11.glVertex2f(1f, -1f);
            GL11.glVertex2f(0, 1f);
            GL11.glEnd();
            glfwSwapBuffers(windowHandle);
            glfwPollEvents();
        }

        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }
}
