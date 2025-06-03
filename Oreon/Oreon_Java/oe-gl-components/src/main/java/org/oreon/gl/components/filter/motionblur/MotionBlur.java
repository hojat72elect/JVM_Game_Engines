package org.oreon.gl.components.filter.motionblur;

import lombok.Getter;
import org.oreon.core.context.BaseContext;
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

public class MotionBlur {

    @Getter
    private final GLTexture motionBlurSceneTexture;
    private final GLTexture pixelVelocityTexture;
    private final PixelVelocityShader pixelVelocityShader;
    private final MotionBlurShader motionBlurShader;

    public MotionBlur() {

        pixelVelocityTexture = new TextureImage2D(BaseContext.getConfig().getFrameWidth(),
                BaseContext.getConfig().getFrameHeight(),
                ImageFormat.RGBA16FLOAT, SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);
        motionBlurSceneTexture = new TextureImage2D(BaseContext.getConfig().getFrameWidth(),
                BaseContext.getConfig().getFrameHeight(),
                ImageFormat.RGBA16FLOAT, SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);

        pixelVelocityShader = PixelVelocityShader.getInstance();
        motionBlurShader = MotionBlurShader.getInstance();
    }

    public void render(GLTexture sceneSampler, GLTexture depthmap) {

        glFinish();
        pixelVelocityShader.bind();
        glBindImageTexture(0, pixelVelocityTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
        pixelVelocityShader.updateUniforms(BaseContext.getCamera().getProjectionMatrix(),
                BaseContext.getCamera().getViewProjectionMatrix().invert(),
                BaseContext.getCamera().getPreviousViewProjectionMatrix(),
                depthmap);
        glDispatchCompute(BaseContext.getConfig().getFrameWidth() / 8, BaseContext.getConfig().getFrameHeight() / 8, 1);
        glFinish();

        motionBlurShader.bind();
        glBindImageTexture(0, motionBlurSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
        glBindImageTexture(2, pixelVelocityTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
        motionBlurShader.updateUniforms(BaseContext.getConfig().getFrameWidth(), BaseContext.getConfig().getFrameHeight(), sceneSampler);
        glDispatchCompute(BaseContext.getConfig().getFrameWidth() / 8, BaseContext.getConfig().getFrameHeight() / 8, 1);
        glFinish();
    }

}
