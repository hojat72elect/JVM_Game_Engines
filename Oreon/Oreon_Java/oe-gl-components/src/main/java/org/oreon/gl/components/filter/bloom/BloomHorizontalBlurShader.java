package org.oreon.gl.components.filter.bloom;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

import static org.lwjgl.opengl.GL13.*;

public class BloomHorizontalBlurShader extends GLShaderProgram {

    private static BloomHorizontalBlurShader instance = null;

    protected BloomHorizontalBlurShader() {
        super();

        addComputeShader(ResourceLoader.loadShader("shaders/filter/bloom/bloom_horizontalGaussianBlur.comp"));

        compileShader();

        addUniform("sceneBrightnessSampler0");
        addUniform("sceneBrightnessSampler1");
        addUniform("sceneBrightnessSampler2");
        addUniform("sceneBrightnessSampler3");
    }

    public static BloomHorizontalBlurShader getInstance() {
        if (instance == null) {
            instance = new BloomHorizontalBlurShader();
        }
        return instance;
    }

    public void updateUniforms(GLTexture sceneBrightnessSampler0, GLTexture sceneBrightnessSampler1,
                               GLTexture sceneBrightnessSampler2, GLTexture sceneBrightnessSampler3) {
        glActiveTexture(GL_TEXTURE0);
        sceneBrightnessSampler0.bind();
        setUniformi("sceneBrightnessSampler0", 0);
        glActiveTexture(GL_TEXTURE1);
        sceneBrightnessSampler1.bind();
        setUniformi("sceneBrightnessSampler1", 1);
        glActiveTexture(GL_TEXTURE2);
        sceneBrightnessSampler2.bind();
        setUniformi("sceneBrightnessSampler2", 2);
        glActiveTexture(GL_TEXTURE3);
        sceneBrightnessSampler3.bind();
        setUniformi("sceneBrightnessSampler3", 3);
    }
}
