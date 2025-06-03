package org.oreon.examples.gl.oreonworlds.shaders;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.instanced.InstancedObject;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

import java.util.List;

public class InstancedWireframeShader extends GLShaderProgram {

    private static InstancedWireframeShader instance = null;

    protected InstancedWireframeShader() {
        super();

        addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/InstancedWireframe_Shader/instanced_wireframe.vert"));
        addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/InstancedWireframe_Shader/instanced_wireframe.geom"));
        addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/InstancedWireframe_Shader/instanced_wireframe.frag"));
        compileShader();

        addUniform("clipplane");
        addUniform("scalingMatrix");
        addUniform("isReflection");

        addUniformBlock("worldMatrices");
        addUniformBlock("modelMatrices");
        addUniformBlock("Camera");

        for (int i = 0; i < 100; i++) {
            addUniform("matrixIndices[" + i + "]");
        }
    }

    public static InstancedWireframeShader getInstance() {
        if (instance == null) {
            instance = new InstancedWireframeShader();
        }
        return instance;
    }

    public void updateUniforms(Renderable object) {
        bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
        setUniformi("isReflection", BaseContext.getConfig().isRenderReflection() ? 1 : 0);

        bindUniformBlock("worldMatrices", 0);
        bindUniformBlock("modelMatrices", 1);

        setUniform("clipplane", BaseContext.getConfig().getClipplane());
        setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));

        InstancedObject vParentNode = object.getParentObject();
        List<Integer> indices = vParentNode.getHighPolyIndices();

        for (int i = 0; i < indices.size(); i++) {
            setUniformi("matrixIndices[" + i + "]", indices.get(i));
        }
    }
}
