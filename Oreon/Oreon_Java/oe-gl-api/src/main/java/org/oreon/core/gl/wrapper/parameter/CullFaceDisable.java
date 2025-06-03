package org.oreon.core.gl.wrapper.parameter;

import org.oreon.core.gl.pipeline.RenderParameter;

import static org.lwjgl.opengl.GL11.*;

public class CullFaceDisable implements RenderParameter {

    public void enable() {
        glDisable(GL_CULL_FACE);
    }

    public void disable() {
        glEnable(GL_CULL_FACE);
    }
}