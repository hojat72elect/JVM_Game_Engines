package org.oreon.core.gl.surface;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class FullScreenMSQuadShader extends GLShaderProgram {

    private static FullScreenMSQuadShader instance = null;

    protected FullScreenMSQuadShader() {
        super();

        addVertexShader(ResourceLoader.loadShader("shaders/quad/quad_VS.glsl"));
        addFragmentShader(ResourceLoader.loadShader("shaders/quad/quadMS_FS.glsl"));
        compileShader();

        addUniform("texture");
        addUniform("width");
        addUniform("height");
        addUniform("multisamples");
    }

    public static FullScreenMSQuadShader getInstance() {
        if (instance == null) {
            instance = new FullScreenMSQuadShader();
        }
        return instance;
    }

    public void updateUniforms(GLTexture texture) {
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        setUniformi("texture", 0);

        setUniformi("width", BaseContext.getConfig().getFrameWidth());
        setUniformi("height", BaseContext.getConfig().getFrameHeight());
        setUniformi("multisamples", BaseContext.getConfig().getMultisampling_sampleCount());
    }
}
