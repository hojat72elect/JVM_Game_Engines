package org.oreon.gl.components.filter.bloom;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

import static org.lwjgl.opengl.GL13.*;

public class BloomAdditiveBlendShader extends GLShaderProgram {

    private static BloomAdditiveBlendShader instance = null;

    protected BloomAdditiveBlendShader() {
        super();

        addComputeShader(ResourceLoader.loadShader("shaders/filter/bloom/bloom_additiveBlending.comp"));

        compileShader();

        addUniform("bloomBlurSampler0");
        addUniform("bloomBlurSampler1");
        addUniform("bloomBlurSampler2");
        addUniform("bloomBlurSampler3");
        addUniform("width");
        addUniform("height");
    }

    public static BloomAdditiveBlendShader getInstance() {
        if (instance == null) {
            instance = new BloomAdditiveBlendShader();
        }
        return instance;
    }

    public void updateUniforms(GLTexture bloomBlurTexture0,
                               GLTexture bloomBlurTexture1, GLTexture bloomBlurTexture2,
                               GLTexture bloomBlurTexture3, int width, int height) {
        glActiveTexture(GL_TEXTURE1);
        bloomBlurTexture0.bind();
        bloomBlurTexture0.bilinearFilter();
        setUniformi("bloomBlurSampler0", 1);
        glActiveTexture(GL_TEXTURE2);
        bloomBlurTexture1.bind();
        bloomBlurTexture1.bilinearFilter();
        setUniformi("bloomBlurSampler1", 2);
        glActiveTexture(GL_TEXTURE3);
        bloomBlurTexture2.bind();
        bloomBlurTexture2.bilinearFilter();
        setUniformi("bloomBlurSampler2", 3);
        glActiveTexture(GL_TEXTURE0);
        bloomBlurTexture3.bind();
        bloomBlurTexture3.bilinearFilter();
        setUniformi("bloomBlurSampler3", 0);
        setUniformf("width", width);
        setUniformf("height", height);
    }
}
