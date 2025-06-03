package org.oreon.gl.components.filter.motionblur;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class MotionBlurShader extends GLShaderProgram {

    private static MotionBlurShader instance = null;

    protected MotionBlurShader() {
        super();

        addComputeShader(ResourceLoader.loadShader("shaders/filter/motion_blur/motionBlur.comp"));

        compileShader();

        addUniform("windowWidth");
        addUniform("windowHeight");
        addUniform("sceneSampler");
    }

    public static MotionBlurShader getInstance() {
        if (instance == null) {
            instance = new MotionBlurShader();
        }
        return instance;
    }

    public void updateUniforms(int width, int height, GLTexture sceneSampler) {
        setUniformf("windowWidth", width);
        setUniformf("windowHeight", height);

        glActiveTexture(GL_TEXTURE0);
        sceneSampler.bind();
        setUniformi("sceneSampler", 0);
    }

}
