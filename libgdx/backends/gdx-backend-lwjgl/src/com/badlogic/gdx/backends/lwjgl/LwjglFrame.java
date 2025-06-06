package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.ApplicationListener;

import org.lwjgl.opengl.Display;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;

/**
 * Wraps an {@link LwjglCanvas} in a resizable {@link JFrame}.
 */
public class LwjglFrame extends JFrame {
    LwjglCanvas lwjglCanvas;
    private Thread shutdownHook;

    public LwjglFrame(ApplicationListener listener, String title, int width, int height) {
        super(title);
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = title;
        config.width = width;
        config.height = height;
        construct(listener, config);
    }

    public LwjglFrame(ApplicationListener listener, LwjglApplicationConfiguration config) {
        super(config.title);
        construct(listener, config);
    }

    /**
     * @param graphicsConfig May be null.
     */
    public LwjglFrame(ApplicationListener listener, LwjglApplicationConfiguration config, GraphicsConfiguration graphicsConfig) {
        super(config.title, graphicsConfig);
        construct(listener, config);
    }

    private void construct(ApplicationListener listener, LwjglApplicationConfiguration config) {
        lwjglCanvas = new LwjglCanvas(listener, config) {
            protected void stopped() {
                LwjglFrame.this.dispose();
            }

            protected void setTitle(String title) {
                LwjglFrame.this.setTitle(title);
            }

            protected void setDisplayMode(int width, int height) {
                Dimension size = new Dimension(Math.round(width / scaleX), Math.round(height / scaleY));
                LwjglFrame.this.getContentPane().setPreferredSize(size);
                LwjglFrame.this.getContentPane().invalidate();
                LwjglFrame.this.pack();
                LwjglFrame.this.setLocationRelativeTo(null);
                updateSize(width, height);
            }

            protected void resize(int width, int height) {
                updateSize(width, height);
            }

            protected void create() {
                LwjglFrame.this.creating();
                super.create();
            }

            protected void start() {
                LwjglFrame.this.start();
            }

            protected void disposed() {
                LwjglFrame.this.disposed();
            }

            protected void exception(Throwable t) {
                LwjglFrame.this.exception(t);
            }

            protected void postedException(Throwable ex, Throwable caller) {
                LwjglFrame.this.postedException(ex, caller);
            }

            protected int getFrameRate() {
                int frameRate = LwjglFrame.this.getFrameRate();
                return frameRate == 0 ? super.getFrameRate() : frameRate;
            }

            public LwjglInput createInput(LwjglApplicationConfiguration config) {
                return LwjglFrame.this.createInput(config);
            }

            protected void applyCursor(Cursor cursor) {
                LwjglFrame.this.applyCursor(cursor);
            }
        };

        setHaltOnShutdown(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        AffineTransform transform = getGraphicsConfiguration().getDefaultTransform();
        float scaleX = (float) transform.getScaleX(), scaleY = (float) transform.getScaleY();
        Dimension size = new Dimension(Math.round(config.width / scaleX), Math.round(config.height / scaleY));
        getContentPane().setPreferredSize(size);

        initialize();
        pack();
        Point location = getLocation();
        if (location.x == 0 && location.y == 0) setLocationRelativeTo(null);
        lwjglCanvas.getCanvas().setSize(size);

        addWindowFocusListener(new WindowAdapter() {
            public void windowLostFocus(WindowEvent event) {
                // Display.reshape sizes and positions the OpenGL window to match the canvas.
                // Normally Display.reshape is called from Display.update when the size changes, but LwjglCanvas doesn't call
                // Display.update when rendering is not needed because it also swaps buffers and would flicker.
                // After losing focus rendering may not be needed so Display.reshape must be called, else the OpenGL window may be
                // left in the wrong place.
                // Display.setLocation calls Display.reshape, despite javadocs saying it's a no-op when a canvas is set.
                if (Display.isCreated()) {
                    Display.setLocation(0, 0);
                    lwjglCanvas.graphics.requestRendering();
                }
            }
        });

        // Finish with invokeLater so any LwjglFrame super constructor has a chance to initialize.
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addCanvas();
                setVisible(true);
                try {
                    lwjglCanvas.getCanvas().requestFocus();
                } catch (Throwable ignored) {
                    // Fails on Linux sometimes, seems shared lib isn't loaded for LinuxDisplay#callErrorHandler.
                }
            }
        });
    }

    protected void creating() {
    }

    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        revalidate();
    }

    /**
     * When true, <code>Runtime.getRuntime().halt(0);</code> is used when the JVM shuts down. This prevents Swing shutdown hooks
     * from causing a deadlock and keeping the JVM alive indefinitely. Default is true.
     */
    public void setHaltOnShutdown(boolean halt) {
        try {
            try {
                if (halt) {
                    if (shutdownHook != null) return;
                    shutdownHook = new Thread() {
                        public void run() {
                            Runtime.getRuntime().halt(0);
                        }
                    };
                    Runtime.getRuntime().addShutdownHook(shutdownHook);
                } else if (shutdownHook != null) {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook);
                    shutdownHook = null;
                }
            } catch (Throwable ignored) { // Can happen if already shutting down.
            }
        } catch (IllegalStateException ex) {
            shutdownHook = null;
        }
    }

    protected int getFrameRate() {
        return 0;
    }

    public LwjglInput createInput(LwjglApplicationConfiguration config) {
        return new DefaultLwjglInput();
    }

    protected void exception(Throwable ex) {
        ex.printStackTrace();
        lwjglCanvas.stop();
    }

    protected void postedException(Throwable ex, Throwable caller) {
        if (caller == null) throw new RuntimeException(ex);
        StringWriter buffer = new StringWriter(1024);
        caller.printStackTrace(new PrintWriter(buffer));
        throw new RuntimeException("Posted: " + buffer, ex);
    }

    /**
     * Called before the JFrame is made displayable.
     */
    protected void initialize() {
    }

    /**
     * Adds the canvas to the content pane. This triggers addNotify and starts the canvas' game loop.
     */
    protected void addCanvas() {
        getContentPane().add(lwjglCanvas.getCanvas());
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

    /**
     * Called to set the cursor.
     */
    protected void applyCursor(Cursor cursor) {
        if (cursor != null || !LwjglCanvas.isWindows) {
            try {
                lwjglCanvas.canvas.setCursor(cursor);
            } catch (Throwable ignored) { // Seems to fail on Linux sometimes.
            }
        }
    }

    /**
     * Called after dispose is complete.
     */
    protected void disposed() {
    }

    public LwjglCanvas getLwjglCanvas() {
        return lwjglCanvas;
    }
}
