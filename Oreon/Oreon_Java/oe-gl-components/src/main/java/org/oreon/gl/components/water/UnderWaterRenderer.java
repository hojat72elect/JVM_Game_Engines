package org.oreon.gl.components.water;

import lombok.Getter;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

public class UnderWaterRenderer {

    @Getter
    private final GLTexture underwaterSceneTexture;
    private final UnderWaterShader underWaterShader;

    private final GLTexture dudvMap;
    private final GLTexture causticsMap;

    public UnderWaterRenderer() {
        underWaterShader = UnderWaterShader.getInstance();

        underwaterSceneTexture = new TextureImage2D(BaseContext.getConfig().getFrameWidth(),
                BaseContext.getConfig().getFrameHeight(),
                ImageFormat.RGBA16FLOAT, SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);

        dudvMap = new TextureImage2D("textures/water/dudv/dudv1.jpg", SamplerFilter.Trilinear);
        causticsMap = new TextureImage2D("textures/water/caustics/caustics.jpg", SamplerFilter.Trilinear);

        GLContext.getResources().setUnderwaterCausticsMap(causticsMap);
        GLContext.getResources().setUnderwaterDudvMap(dudvMap);
    }

    public void render(GLTexture sceneTexture, GLTexture sceneDepthMap) {

        underWaterShader.bind();
        glBindImageTexture(0, sceneTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
        glBindImageTexture(1, underwaterSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
        underWaterShader.updateUniforms(sceneDepthMap);
        glDispatchCompute(BaseContext.getConfig().getFrameWidth() / 8, BaseContext.getConfig().getFrameHeight() / 8, 1);
        glFinish();
    }

}
