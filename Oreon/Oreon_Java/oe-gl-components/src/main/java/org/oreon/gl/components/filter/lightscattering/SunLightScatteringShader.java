package org.oreon.gl.components.filter.lightscattering;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class SunLightScatteringShader extends GLShaderProgram {

    private static SunLightScatteringShader instance = null;

    protected SunLightScatteringShader() {
        super();

        addComputeShader(ResourceLoader.loadShader("shaders/filter/light_scattering/lightScattering.comp"));

        compileShader();

        addUniform("sunWorldPosition");
        addUniform("windowWidth");
        addUniform("windowHeight");
        addUniform("viewProjectionMatrix");
        addUniform("num_samples");
        addUniform("decay");
    }

    public static SunLightScatteringShader getInstance() {
        if (instance == null) {
            instance = new SunLightScatteringShader();
        }
        return instance;
    }

    public void updateUniforms(int windowWidth, int windowHeight, Matrix4f viewProjectionMatrix) {

        setUniformf("windowWidth", windowWidth);
        setUniformf("windowHeight", windowHeight);
        setUniform("viewProjectionMatrix", viewProjectionMatrix);
        setUniform("sunWorldPosition", BaseContext.getConfig().getSunPosition().mul(-Constants.ZFAR));
        setUniformi("num_samples", BaseContext.getConfig().getLightscatteringSampleCount());
        setUniformf("decay", BaseContext.getConfig().getLightscatteringDecay());
    }
}
