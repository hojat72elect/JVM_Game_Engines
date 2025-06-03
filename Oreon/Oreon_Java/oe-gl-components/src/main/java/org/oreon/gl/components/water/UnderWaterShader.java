package org.oreon.gl.components.water;

import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class UnderWaterShader extends GLShaderProgram {

    private static UnderWaterShader instance = null;

    protected UnderWaterShader() {
        super();

        addComputeShader(ResourceLoader.loadShader("shaders/water/underwater.comp"));

        compileShader();

        addUniform("sceneDepthMap");
        addUniform("waterRefractionColor");
    }

    public static UnderWaterShader getInstance() {
        if (instance == null) {
            instance = new UnderWaterShader();
        }
        return instance;
    }

    public void updateUniforms(GLTexture sceneDepthMap) {
        glActiveTexture(GL_TEXTURE0);
        sceneDepthMap.bind();
        setUniformi("sceneDepthMap", 0);
        setUniform("waterRefractionColor", GLContext.getResources().getWaterConfig().getBaseColor());
    }
}
