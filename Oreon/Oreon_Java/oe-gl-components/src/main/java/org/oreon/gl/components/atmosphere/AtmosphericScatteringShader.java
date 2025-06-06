package org.oreon.gl.components.atmosphere;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.ResourceLoader;

public class AtmosphericScatteringShader extends GLShaderProgram {

    private static AtmosphericScatteringShader instance = null;

    protected AtmosphericScatteringShader() {
        super();

        addVertexShader(ResourceLoader.loadShader("shaders/atmosphere/atmospheric_scattering.vert"));
        addFragmentShader(ResourceLoader.loadShader("shaders/atmosphere/atmospheric_scattering.frag"));
        compileShader();

        addUniform("m_MVP");
        addUniform("m_Projection");
        addUniform("m_View");
        addUniform("v_Sun");
        addUniform("r_Sun");
        addUniform("width");
        addUniform("height");
        addUniform("isReflection");
        addUniform("horizonVerticalShift");
        addUniform("reflectionVerticalShift");
        addUniform("bloom");
    }

    public static AtmosphericScatteringShader getInstance() {
        if (instance == null) {
            instance = new AtmosphericScatteringShader();
        }
        return instance;
    }

    public void updateUniforms(Renderable object) {
        setUniform("m_MVP", object.getWorldTransform().getModelViewProjectionMatrix());
        setUniform("m_Projection", BaseContext.getCamera().getProjectionMatrix());
        setUniform("m_View", BaseContext.getCamera().getViewMatrix());
        setUniform("v_Sun", BaseContext.getConfig().getSunPosition().mul(-1));
        setUniformf("horizonVerticalShift", BaseContext.getConfig().getHorizonVerticalShift());
        setUniformf("reflectionVerticalShift", BaseContext.getConfig().getHorizonReflectionVerticalShift());
        setUniformf("r_Sun", BaseContext.getConfig().getSunRadius());
        setUniformi("width", BaseContext.getConfig().getFrameWidth());
        setUniformi("height", BaseContext.getConfig().getFrameHeight());
        setUniformi("isReflection", BaseContext.getConfig().isRenderReflection() ? 1 : 0);
        setUniformf("bloom", BaseContext.getConfig().getAtmosphereBloomFactor());
    }

}
