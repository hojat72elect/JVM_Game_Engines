package org.oreon.core.gl.wrapper.parameter;

import org.oreon.core.gl.pipeline.RenderParameter;

import static org.lwjgl.opengl.GL11.*;

public class AlphaBlendingSrcAlpha implements RenderParameter {

    public AlphaBlendingSrcAlpha() {
    }

    public void enable() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_SRC_ALPHA);
    }

    public void disable() {
        glDisable(GL_BLEND);
    }
}
