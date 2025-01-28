package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.ApplicationListener;

import java.applet.Applet;
import java.awt.*;

/**
 * An OpenGL surface in an applet.
 */
@SuppressWarnings("removal")
public class LwjglApplet extends Applet {
    final Canvas canvas;
    LwjglApplication app;

    public LwjglApplet(final ApplicationListener listener, final LwjglApplicationConfiguration config) {
        LwjglNativesLoader.load = false;
        canvas = new Canvas() {
            public final void addNotify() {
                super.addNotify();
                app = new LwjglAppletApplication(listener, canvas, config);
            }

            public final void removeNotify() {
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

    public LwjglApplet(final ApplicationListener listener) {
        LwjglNativesLoader.load = false;
        canvas = new Canvas() {
            public final void addNotify() {
                super.addNotify();
                app = new LwjglAppletApplication(listener, canvas);
            }

            public final void removeNotify() {
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

    class LwjglAppletApplication extends LwjglApplication {

        public LwjglAppletApplication(ApplicationListener listener, Canvas canvas) {
            super(listener, canvas);
        }

        public LwjglAppletApplication(ApplicationListener listener, Canvas canvas, LwjglApplicationConfiguration config) {
            super(listener, config, canvas);
        }

        @Override
        public ApplicationType getType() {
            return ApplicationType.Applet;
        }
    }
}
