package org.oreon.core.gl.wrapper.parameter;

import org.oreon.core.gl.pipeline.RenderParameter;

import static org.lwjgl.opengl.GL11.*;

public class ShadowConfig implements RenderParameter {

    public void enable() {
        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(2.0f, 2.0f);
    }

    public void disable() {
        glDisable(GL_POLYGON_OFFSET_FILL);
    }

}
