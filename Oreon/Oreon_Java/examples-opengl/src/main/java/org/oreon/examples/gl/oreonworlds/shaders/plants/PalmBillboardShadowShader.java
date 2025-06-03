package org.oreon.examples.gl.oreonworlds.shaders.plants;

import org.oreon.core.gl.instanced.GLInstancedCluster;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

import java.util.List;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class PalmBillboardShadowShader extends GLShaderProgram {

    private static PalmBillboardShadowShader instance = null;

    protected PalmBillboardShadowShader() {
        super();

        addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Palm_Shader/Palm01_VS.glsl"));
        addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Palm_Shader/Palm01BillboardShadow_GS.glsl"));
        addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Palm_Shader/Palm01BillboardShadow_FS.glsl"));
        compileShader();

        addUniformBlock("InstancedMatrices");
        addUniformBlock("Camera");
        addUniformBlock("LightViewProjections");
        addUniform("material.diffusemap");

        for (int i = 0; i < 100; i++) {
            addUniform("matrixIndices[" + i + "]");
        }
    }

    public static PalmBillboardShadowShader getInstance() {
        if (instance == null) {
            instance = new PalmBillboardShadowShader();
        }
        return instance;
    }

    public void updateUniforms(Renderable object) {

        bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
        bindUniformBlock("LightViewProjections", Constants.LightMatricesUniformBlockBinding);

        ((GLInstancedCluster) object.getParentNode()).getWorldMatricesBuffer().bindBufferBase(0);
        bindUniformBlock("InstancedMatrices", 0);

        Material material = object.getComponent(NodeComponentType.MATERIAL0);

        glActiveTexture(GL_TEXTURE0);
        material.getDiffusemap().bind();
        setUniformi("material.diffusemap", 0);

        List<Integer> indices = ((InstancedCluster) object.getParentNode()).getLowPolyIndices();

        for (int i = 0; i < indices.size(); i++) {
            setUniformi("matrixIndices[" + i + "]", indices.get(i));
        }
    }
}
