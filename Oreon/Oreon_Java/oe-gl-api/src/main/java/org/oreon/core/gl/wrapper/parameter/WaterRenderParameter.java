package org.oreon.core.gl.wrapper.parameter;

import org.oreon.core.gl.pipeline.RenderParameter;

import static org.lwjgl.opengl.GL11.*;

public class WaterRenderParameter implements RenderParameter {

    public void enable() {
        glDisable(GL_CULL_FACE);
    }

    public void disable() {
        glEnable(GL_CULL_FACE);
    }

    public void clearScreenDeepOcean() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
