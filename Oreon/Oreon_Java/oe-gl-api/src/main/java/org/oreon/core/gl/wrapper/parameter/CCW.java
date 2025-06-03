package org.oreon.core.gl.wrapper.parameter;

import org.oreon.core.gl.pipeline.RenderParameter;

import static org.lwjgl.opengl.GL11.*;

public class CCW implements RenderParameter {

    public void enable() {
        glFrontFace(GL_CCW);
    }

    public void disable() {
        glFrontFace(GL_CW);
    }
}
