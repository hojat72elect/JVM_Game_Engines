package com.badlogic.gdx.tests.lwjgl3;

import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Configuration;

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.*;

public class AwtTestLWJGL {
    static GLFWMouseButtonCallback callback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (action == GLFW_PRESS) {
                System.out.println("Bam");
                SwingUtilities.invokeLater(() -> {
                    javax.swing.JFrame frame = new javax.swing.JFrame("test");
                    frame.setSize(640, 480);
                    frame.setLocationRelativeTo(null);

                    javax.swing.JButton button1 = new javax.swing.JButton("Try ImageIO");
                    frame.getContentPane().add(button1, java.awt.BorderLayout.SOUTH);

                    button1.addActionListener((event) -> {
                        try {
                            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(new java.net.URL("http://n4te.com/x/2586-tiNN.jpg").openStream());
                            frame.getContentPane().add(new javax.swing.JLabel(new javax.swing.ImageIcon(image)), java.awt.BorderLayout.CENTER);
                            frame.getContentPane().revalidate();
                        } catch (java.io.IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                    frame.setVisible(true);
                });
            }
        }
    };

    public static void main(String[] args) throws Exception {
        java.awt.EventQueue.invokeAndWait(java.awt.Toolkit::getDefaultToolkit);

        if (SharedLibraryLoader.os == Os.MacOsX) {
            Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
        }

        if (!glfwInit()) {
            System.out.println("Couldn't initialize GLFW");
            System.exit(-1);
        }
        final long window = glfwCreateWindow(640, 480, "Test", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Couldn't create window");
        }
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(0);
        glfwSetMouseButtonCallback(window, callback);

        while (!glfwWindowShouldClose(window)) {
            GL11.glViewport(0, 0, 640, 480);
            GL11.glClearColor(1, 0, 1, 1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glRotatef(0.1f, 0, 0, 1);
            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glVertex2f(-0.5f, -0.5f);
            GL11.glVertex2f(0.5f, -0.5f);
            GL11.glVertex2f(0, 0.5f);
            GL11.glEnd();
            glfwPollEvents();
            glfwSwapBuffers(window);
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
