package org.oreon.gl.engine.antialiasing;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class SampleCoverageShader extends GLShaderProgram {

    private static SampleCoverageShader instance = null;

    protected SampleCoverageShader() {
        super();

        addComputeShader(ResourceLoader.loadShader("shaders/sampleCoverage.comp"));

        compileShader();

        addUniform("multisamples");
    }

    public static SampleCoverageShader getInstance() {
        if (instance == null) {
            instance = new SampleCoverageShader();
        }
        return instance;
    }

    public void updateUniforms() {

        setUniformi("multisamples", BaseContext.getConfig().getMultisampling_sampleCount());
    }

}
