package org.oreon.core.gl.wrapper.parameter;

import org.oreon.core.gl.pipeline.RenderParameter;

import static org.lwjgl.opengl.GL11.*;

public class AlphaBlending implements RenderParameter {

    private final int srcBlendFactor;
    private final int dstBlendFactor;

    public AlphaBlending(int srcAlphaBlendFactor,
                         int dstAlphaBlendFactor) {

        srcBlendFactor = srcAlphaBlendFactor;
        dstBlendFactor = dstAlphaBlendFactor;
    }

    public void enable() {
        glEnable(GL_BLEND);
        glBlendFunc(srcBlendFactor, dstBlendFactor);
    }

    public void disable() {
        glDisable(GL_BLEND);
    }
}
