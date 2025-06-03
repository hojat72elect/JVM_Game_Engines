package org.oreon.examples.gl.oreonworlds.shaders;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.instanced.InstancedObject;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

import java.util.List;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class InstancedBillboardShadowShader extends GLShaderProgram {

    private static InstancedBillboardShadowShader instance = null;

    protected InstancedBillboardShadowShader() {
        super();

        addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Billboard_Shader/billboard.vert"));
        addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Billboard_Shader/billboard_shadow.geom"));
        addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Billboard_Shader/billboard_shadow.frag"));
        compileShader();

        addUniformBlock("worldMatrices");
        addUniformBlock("Camera");
        addUniformBlock("LightViewProjections");
        addUniform("material.diffusemap");

        addUniformBlock("Camera");

        for (int i = 0; i < 100; i++) {
            addUniform("matrixIndices[" + i + "]");
        }
    }

    public static InstancedBillboardShadowShader getInstance() {
        if (instance == null) {
            instance = new InstancedBillboardShadowShader();
        }
        return instance;
    }

    public void updateUniforms(Renderable object) {

        bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
        bindUniformBlock("LightViewProjections", Constants.LightMatricesUniformBlockBinding);

        bindUniformBlock("worldMatrices", 0);

        Material material = object.getComponent(NodeComponentType.MATERIAL0);

        glActiveTexture(GL_TEXTURE0);
        material.getDiffusemap().bind();
        setUniformi("material.diffusemap", 0);

        InstancedObject vParentNode = object.getParentObject();
        List<Integer> indices = vParentNode.getLowPolyIndices();

        for (int i = 0; i < indices.size(); i++) {
            setUniformi("matrixIndices[" + i + "]", indices.get(i));
        }
    }
}
