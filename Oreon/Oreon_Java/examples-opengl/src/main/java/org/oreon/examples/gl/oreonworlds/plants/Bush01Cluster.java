package org.oreon.examples.gl.oreonworlds.plants;

import org.oreon.common.terrain.TerrainHelper;
import org.oreon.core.gl.instanced.GLInstancedCluster;
import org.oreon.core.gl.memory.GLUniformBuffer;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.BufferUtil;
import org.oreon.gl.components.terrain.GLTerrain;

import java.nio.FloatBuffer;
import java.util.List;

public class Bush01Cluster extends GLInstancedCluster {

    public Bush01Cluster(int instances, Vec3f pos, List<Renderable> objects) {

        setCenter(pos);
        int buffersize = Float.BYTES * 16 * instances;

        for (int i = 0; i < instances; i++) {

            float s = (float) (Math.random() * 0.2 + 0.6);
            Vec3f translation = new Vec3f((float) (Math.random() * 100) - 50 + getCenter().getX(), 0, (float) (Math.random() * 100) - 50 + getCenter().getZ());
            Vec3f scaling = new Vec3f(s, s, s);
            Vec3f rotation = new Vec3f(0, (float) Math.random() * 360f, 0);

            float terrainHeight = TerrainHelper.getTerrainHeight(GLTerrain.getConfig(), translation.getX(), translation.getZ());
            terrainHeight -= 1;
            translation.setY(terrainHeight);

            Matrix4f translationMatrix = new Matrix4f().Translation(translation);
            Matrix4f rotationMatrix = new Matrix4f().Rotation(rotation);
            Matrix4f scalingMatrix = new Matrix4f().Scaling(scaling);

            getWorldMatrices().add(translationMatrix.mul(scalingMatrix.mul(rotationMatrix)));
            getModelMatrices().add(rotationMatrix);
            getHighPolyIndices().add(i);
        }

        setModelMatricesBuffer(new GLUniformBuffer());
        getModelMatricesBuffer().allocate(buffersize);

        setWorldMatricesBuffer(new GLUniformBuffer());
        getWorldMatricesBuffer().allocate(buffersize);

        /**
         * init matrices UBO's
         */
        int size = Float.BYTES * 16 * instances;

        FloatBuffer worldMatricesFloatBuffer = BufferUtil.createFloatBuffer(size);
        FloatBuffer modelMatricesFloatBuffer = BufferUtil.createFloatBuffer(size);

        for (Matrix4f matrix : getWorldMatrices()) {
            worldMatricesFloatBuffer.put(BufferUtil.createFlippedBuffer(matrix));
        }
        for (Matrix4f matrix : getModelMatrices()) {
            modelMatricesFloatBuffer.put(BufferUtil.createFlippedBuffer(matrix));
        }

        getWorldMatricesBuffer().updateData(worldMatricesFloatBuffer, size);
        getModelMatricesBuffer().updateData(modelMatricesFloatBuffer, size);

        for (Renderable object : objects) {
            addChild(object);
        }

    }

    public void update() {
        super.update();
//		
//		if (CoreSystem.getInstance().getRenderEngine().isGrid()){
//			for (Node child : getChildren()){
//				((Renderer) ((Renderable) child).getComponent("Renderer")).getRenderInfo().setShader(InstancingGridShader.getInstance());
//			}
//		}
//		else{
//			((Renderer) ((Renderable) getChildren().get(0)).getComponent("Renderer")).getRenderInfo().setShader(BushShader.getInstance());
//			((Renderer) ((Renderable) getChildren().get(1)).getComponent("Renderer")).getRenderInfo().setShader(BushShader.getInstance());
//		}
    }
}
