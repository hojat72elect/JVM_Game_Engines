package org.oreon.gl.components.water;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

import static org.lwjgl.opengl.GL13.*;

public class WaterWireframeShader extends GLShaderProgram {

    private static WaterWireframeShader instance = null;


    protected WaterWireframeShader() {
        super();

        addVertexShader(ResourceLoader.loadShader("shaders/water/water.vert"));
        addTessellationControlShader(ResourceLoader.loadShader("shaders/water/water.tesc"));
        addTessellationEvaluationShader(ResourceLoader.loadShader("shaders/water/water.tese"));
        addGeometryShader(ResourceLoader.loadShader("shaders/water/water.wireframe.geom"));
        addFragmentShader(ResourceLoader.loadShader("shaders/water/water.wireframe.frag"));
        compileShader();

        addUniform("Dy");
        addUniform("Dx");
        addUniform("Dz");
        addUniform("motion");
        addUniform("wind");

        addUniformBlock("Camera");
    }

    public static WaterWireframeShader getInstance() {
        if (instance == null) {
            instance = new WaterWireframeShader();
        }
        return instance;
    }

    public void updateUniforms(Renderable object) {
        bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);

        Water water = (Water) object;

        setUniformf("motion", water.getT_motion());
        setUniform("wind", water.getConfig().getWindDirection());

        glActiveTexture(GL_TEXTURE0);
        water.getFft().getDy().bind();
        setUniformi("Dy", 0);
        glActiveTexture(GL_TEXTURE1);
        water.getFft().getDx().bind();
        setUniformi("Dx", 1);
        glActiveTexture(GL_TEXTURE2);
        water.getFft().getDz().bind();
        setUniformi("Dz", 2);
    }
}
