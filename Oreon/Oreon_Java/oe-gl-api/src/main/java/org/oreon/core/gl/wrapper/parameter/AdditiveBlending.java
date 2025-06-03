package org.oreon.core.gl.wrapper.parameter;

import org.oreon.core.gl.pipeline.RenderParameter;

import static org.lwjgl.opengl.GL11.*;


public class AdditiveBlending implements RenderParameter {

    public AdditiveBlending() {
    }

    public void enable() {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
    }

    public void disable() {
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
    }
}
