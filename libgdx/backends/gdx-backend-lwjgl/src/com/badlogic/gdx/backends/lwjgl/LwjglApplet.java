package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.ApplicationListener;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

/**
 * An OpenGL surface in an applet.
 */
public class LwjglApplet extends Applet {
    final Canvas canvas;
    LwjglApplication app;

    public LwjglApplet(final ApplicationListener listener) {
        LwjglNativesLoader.load = false;
        canvas = new Canvas() {
            public void addNotify() {
                super.addNotify();
                app = new LwjglAppletApplication(listener, canvas);
            }

            public void removeNotify() {
                app.stop();
                super.removeNotify();
            }
        };
        setLayout(new BorderLayout());
        canvas.setIgnoreRepaint(true);
        add(canvas);
        canvas.setFocusable(true);
        canvas.requestFocus();
    }

    public void destroy() {
        remove(canvas);
        super.destroy();
    }

    static class LwjglAppletApplication extends LwjglApplication {

        public LwjglAppletApplication(ApplicationListener listener, Canvas canvas) {
            super(listener, canvas);
        }

        @Override
        public ApplicationType getType() {
            return ApplicationType.Applet;
        }
    }
}
