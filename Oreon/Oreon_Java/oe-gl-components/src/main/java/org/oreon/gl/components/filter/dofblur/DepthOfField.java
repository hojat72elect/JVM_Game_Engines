package org.oreon.gl.components.filter.dofblur;

import lombok.Getter;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.gl.wrapper.texture.TextureStorage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

public class DepthOfField {

    @Getter
    private final GLTexture horizontalBlurSceneTexture;
    @Getter
    private final GLTexture verticalBlurSceneTexture;

    private final DepthOfFieldHorizontalBlurShader horizontalBlurShader;
    private final DepthOfFieldVerticalBlurShader verticalBlurShader;

    private final GLFramebuffer lowResFbo;
    @SuppressWarnings("unused")
    private final FullScreenQuad fullScreenQuad;
    @Getter
    private final GLTexture downsampledSceneSampler;

    public DepthOfField() {

        horizontalBlurShader = DepthOfFieldHorizontalBlurShader.getInstance();
        verticalBlurShader = DepthOfFieldVerticalBlurShader.getInstance();

        horizontalBlurSceneTexture = new TextureImage2D(BaseContext.getConfig().getFrameWidth(),
                BaseContext.getConfig().getFrameHeight(), ImageFormat.RGBA16FLOAT,
                SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);

        verticalBlurSceneTexture = new TextureImage2D(BaseContext.getConfig().getFrameWidth(),
                BaseContext.getConfig().getFrameHeight(), ImageFormat.RGBA16FLOAT,
                SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);

        new TextureStorage2D(BaseContext.getConfig().getFrameWidth(),
                BaseContext.getConfig().getFrameHeight(), 1, ImageFormat.RGBA16FLOAT);

        downsampledSceneSampler = new TextureImage2D((int) (BaseContext.getConfig().getFrameWidth() / 2f),
                (int) (BaseContext.getConfig().getFrameWidth() / 2f), ImageFormat.RGBA16FLOAT,
                SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);

        fullScreenQuad = new FullScreenQuad();
        lowResFbo = new GLFramebuffer();
        lowResFbo.bind();
        lowResFbo.createColorTextureAttachment(downsampledSceneSampler.getHandle(), 0);
        lowResFbo.checkStatus();
        lowResFbo.unbind();
    }

    public void render(GLTexture depthmap, GLTexture sceneSampler) {

//		glFinish();
//		lowResFbo.bind();
//		fullScreenQuad.setTexture(sceneSampler);
//		glViewport(0,0,(int)(BaseContext.getConfig().getX_ScreenResolution()/2f),
//				(int)(BaseContext.getConfig().getY_ScreenResolution()/2f));
//		fullScreenQuad.render();
//		lowResFbo.unbind();
//		glViewport(0,0, BaseContext.getConfig().getX_ScreenResolution(), BaseContext.getConfig().getY_ScreenResolution());
        glFinish();

        horizontalBlurShader.bind();
        glBindImageTexture(0, horizontalBlurSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
        horizontalBlurShader.updateUniforms(depthmap, sceneSampler, downsampledSceneSampler);
        glDispatchCompute(BaseContext.getConfig().getFrameWidth() / 8, BaseContext.getConfig().getFrameHeight() / 8, 1);
        glFinish();

        verticalBlurShader.bind();
        glBindImageTexture(0, horizontalBlurSceneTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
        glBindImageTexture(1, verticalBlurSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
        verticalBlurShader.updateUniforms(depthmap);
        glDispatchCompute(BaseContext.getConfig().getFrameWidth() / 8, BaseContext.getConfig().getFrameHeight() / 8, 1);
        glFinish();
    }

}
