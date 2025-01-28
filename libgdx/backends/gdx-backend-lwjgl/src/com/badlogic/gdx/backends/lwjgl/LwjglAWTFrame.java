package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.ApplicationListener;

import javax.swing.*;
import java.awt.*;

/**
 * Wraps an {@link LwjglAWTCanvas} in a resizable {@link JFrame}.
 */
public class LwjglAWTFrame extends JFrame {
    final LwjglAWTCanvas lwjglAWTCanvas;
    private Thread shutdownHook;

    public LwjglAWTFrame(ApplicationListener listener, String title, int width, int height) {
        super(title);

        lwjglAWTCanvas = new LwjglAWTCanvas(listener) {
            protected void stopped() {
                LwjglAWTFrame.this.dispose();
            }

            protected void setTitle(String title) {
                LwjglAWTFrame.this.setTitle(title);
            }

            protected void setDisplayMode(int width, int height) {
                LwjglAWTFrame.this.getContentPane().setPreferredSize(new Dimension(width, height));
                LwjglAWTFrame.this.getContentPane().invalidate();
                LwjglAWTFrame.this.pack();
                LwjglAWTFrame.this.setLocationRelativeTo(null);
                updateSize(width, height);
            }

            protected void resize(int width, int height) {
                updateSize(width, height);
            }

            protected void start() {
                LwjglAWTFrame.this.start();
            }
        };
        getContentPane().add(lwjglAWTCanvas.getCanvas());

        setHaltOnShutdown(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(new Dimension(width, height));
        initialize();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        lwjglAWTCanvas.getCanvas().requestFocus();
    }

    /**
     * When true, <code>Runtime.getRuntime().halt(0);</code> is used when the JVM shuts down. This prevents Swing shutdown hooks
     * from causing a deadlock and keeping the JVM alive indefinitely. Default is true.
     */
    public void setHaltOnShutdown(boolean halt) {
        if (halt) {
            if (shutdownHook != null) return;
            shutdownHook = new Thread() {
                public void run() {
                    Runtime.getRuntime().halt(0); // Because fuck you, deadlock causing Swing shutdown hooks.
                }
            };
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        } else if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
            shutdownHook = null;
        }
    }

    /**
     * Called before the JFrame is shown.
     */
    protected void initialize() {
    }

    /**
     * Called after {@link ApplicationListener} create and resize, but before the game loop iteration.
     */
    protected void start() {
    }

    /**
     * Called when the canvas size changes.
     */
    public void updateSize(int width, int height) {
    }

    public LwjglAWTCanvas getLwjglAWTCanvas() {
        return lwjglAWTCanvas;
    }
}
