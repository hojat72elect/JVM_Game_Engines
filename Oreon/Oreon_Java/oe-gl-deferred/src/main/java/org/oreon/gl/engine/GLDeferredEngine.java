package org.oreon.gl.engine;

import lombok.Setter;
import org.lwjgl.glfw.GLFW;
import org.oreon.core.RenderEngine;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.picking.TerrainPicking;
import org.oreon.core.gl.shadow.ParallelSplitShadowMapsFbo;
import org.oreon.core.gl.surface.FullScreenMultisampleQuad;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.util.GLUtil;
import org.oreon.core.instanced.InstancedHandler;
import org.oreon.core.light.LightHandler;
import org.oreon.core.scenegraph.RenderList;
import org.oreon.core.target.FrameBufferObject.Attachment;
import org.oreon.gl.components.filter.bloom.Bloom;
import org.oreon.gl.components.filter.contrast.ContrastController;
import org.oreon.gl.components.filter.dofblur.DepthOfField;
import org.oreon.gl.components.filter.lensflare.LensFlare;
import org.oreon.gl.components.filter.lightscattering.SunLightScattering;
import org.oreon.gl.components.filter.motionblur.MotionBlur;
import org.oreon.gl.components.filter.ssao.SSAO;
import org.oreon.gl.components.terrain.GLTerrain;
import org.oreon.gl.components.ui.GLGUI;
import org.oreon.gl.components.water.UnderWaterRenderer;
import org.oreon.gl.engine.antialiasing.FXAA;
import org.oreon.gl.engine.antialiasing.SampleCoverage;
import org.oreon.gl.engine.deferred.DeferredLighting;
import org.oreon.gl.engine.transparency.OpaqueTransparencyBlending;

import static org.lwjgl.opengl.GL11.*;

public class GLDeferredEngine extends RenderEngine {

    private RenderList opaqueSceneRenderList;
    private RenderList transparencySceneRenderList;

    private GLFrameBufferObject primarySceneFbo;
    private GLFrameBufferObject secondarySceneFbo;

    private FullScreenQuad fullScreenQuad;
    private FullScreenMultisampleQuad fullScreenQuadMultisample;
    private SampleCoverage sampleCoverage;
    private FXAA fxaa;

    private InstancedHandler instancingObjectHandler;

    private DeferredLighting deferredLighting;
    private OpaqueTransparencyBlending opaqueTransparencyBlending;

    @Setter
    private GLGUI gui;

    private ParallelSplitShadowMapsFbo pssmFbo;

    // post processing effects
    private MotionBlur motionBlur;
    private DepthOfField depthOfField;
    private Bloom bloom;
    private SunLightScattering sunlightScattering;
    private LensFlare lensFlare;
    private SSAO ssao;
    private UnderWaterRenderer underWaterRenderer;
    private ContrastController contrastController;

    private boolean renderAlbedoBuffer = false;
    private boolean renderNormalBuffer = false;
    private boolean renderPositionBuffer = false;
    private boolean renderSampleCoverageMask = false;
    private boolean renderDeferredLightingScene = false;
    private boolean renderSSAOBuffer = false;
    private boolean renderPostProcessingEffects = true;


    @Override
    public void init() {

        super.init();

        sceneGraph.addObject(new GLDirectionalLight());

        opaqueSceneRenderList = new RenderList();
        transparencySceneRenderList = new RenderList();

        instancingObjectHandler = InstancedHandler.getInstance();

        primarySceneFbo = new OffScreenFbo(config.getFrameWidth(),
                config.getFrameHeight(), config.getMultisampling_sampleCount());
        secondarySceneFbo = new TransparencyFbo(config.getFrameWidth(),
                config.getFrameHeight());
        GLContext.getResources().setPrimaryFbo(primarySceneFbo);

        fullScreenQuad = new FullScreenQuad();
        fullScreenQuadMultisample = new FullScreenMultisampleQuad();
        pssmFbo = new ParallelSplitShadowMapsFbo();
        sampleCoverage = new SampleCoverage(config.getFrameWidth(), config.getFrameHeight());
        fxaa = new FXAA();

        deferredLighting = new DeferredLighting(config.getFrameWidth(),
                config.getFrameHeight());
        opaqueTransparencyBlending = new OpaqueTransparencyBlending(config.getFrameWidth(),
                config.getFrameHeight());

        motionBlur = new MotionBlur();
        depthOfField = new DepthOfField();
        bloom = new Bloom();
        sunlightScattering = new SunLightScattering();
        lensFlare = new LensFlare();
        ssao = new SSAO(config.getFrameWidth(), config.getFrameHeight());
        underWaterRenderer = new UnderWaterRenderer();
        contrastController = new ContrastController();

        GLContext.getResources().setSceneDepthMap(primarySceneFbo.getAttachmentTexture(Attachment.DEPTH));

        if (gui != null) {
            gui.init();
        }

        glFinish();
    }

    @Override
    public void render() {

        //----------------------------------//
        //        clear render buffer       //
        //----------------------------------//

        GLUtil.clearScreen();
        primarySceneFbo.bind();
        GLUtil.clearScreen();
        secondarySceneFbo.bind();
        GLUtil.clearScreen();
        pssmFbo.getFbo().bind();
        glClear(GL_DEPTH_BUFFER_BIT);
        pssmFbo.getFbo().unbind();


        //----------------------------------//
        //      Record Render Objects       //
        //----------------------------------//

        sceneGraph.record(opaqueSceneRenderList);


        //----------------------------------//
        //        render shadow maps        //
        //----------------------------------//

        if (BaseContext.getConfig().isShadowsEnable()) {
            pssmFbo.getFbo().bind();
            pssmFbo.getConfig().enable();
            glViewport(0, 0, BaseContext.getConfig().getShadowMapResolution(), BaseContext.getConfig().getShadowMapResolution());
            opaqueSceneRenderList.getValues().forEach(object ->
            {
                object.renderShadows();
            });
            glViewport(0, 0, config.getFrameWidth(), config.getFrameHeight());
            pssmFbo.getConfig().disable();
            pssmFbo.getFbo().unbind();
        }


        //----------------------------------------------//
        //   render opaque scene into primary gbuffer   //
        //----------------------------------------------//

        primarySceneFbo.bind();

        opaqueSceneRenderList.getValues().forEach(object ->
        {
            if (BaseContext.getConfig().isRenderWireframe())
                object.renderWireframe();
            else
                object.render();
        });

        primarySceneFbo.unbind();


        //------------------------------------------------------//
        //    render transparent scene into secondary gbuffer   //
        //------------------------------------------------------//

        secondarySceneFbo.bind();
        sceneGraph.recordTransparentObjects(transparencySceneRenderList);
        transparencySceneRenderList.getValues().forEach(object -> object.render());
        secondarySceneFbo.unbind();


        //-----------------------------------//
        //         render ssao buffer        //
        //-----------------------------------//

        if (BaseContext.getConfig().isSsaoEnabled()) {
            ssao.render(primarySceneFbo.getAttachmentTexture(Attachment.POSITION),
                    primarySceneFbo.getAttachmentTexture(Attachment.NORMAL));
        }


        //---------------------------------------------------//
        //         render sample coverage mask buffer        //
        //---------------------------------------------------//

        if (BaseContext.getConfig().getMultisampling_sampleCount() > 1) {
            sampleCoverage.render(primarySceneFbo.getAttachmentTexture(Attachment.POSITION),
                    primarySceneFbo.getAttachmentTexture(Attachment.LIGHT_SCATTERING),
                    primarySceneFbo.getAttachmentTexture(Attachment.SPECULAR_EMISSION_DIFFUSE_SSAO_BLOOM));
        }


        //-----------------------------------------------------//
        //         render multisample deferred lighting        //
        //-----------------------------------------------------//


        deferredLighting.render(sampleCoverage.getSampleCoverageMask(),
                ssao.getSsaoBlurSceneTexture(),
                pssmFbo.getDepthMap(),
                primarySceneFbo.getAttachmentTexture(Attachment.COLOR),
                primarySceneFbo.getAttachmentTexture(Attachment.POSITION),
                primarySceneFbo.getAttachmentTexture(Attachment.NORMAL),
                primarySceneFbo.getAttachmentTexture(Attachment.SPECULAR_EMISSION_DIFFUSE_SSAO_BLOOM));


        //-----------------------------------------------//
        //         blend opaque/transparent scene        //
        //-----------------------------------------------//

        if (transparencySceneRenderList.getObjectList().size() > 0) {
            opaqueTransparencyBlending.render(deferredLighting.getDeferredLightingSceneTexture(),
                    primarySceneFbo.getAttachmentTexture(Attachment.DEPTH),
                    sampleCoverage.getLightScatteringMaskSingleSample(),
                    secondarySceneFbo.getAttachmentTexture(Attachment.COLOR),
                    secondarySceneFbo.getAttachmentTexture(Attachment.DEPTH),
                    secondarySceneFbo.getAttachmentTexture(Attachment.ALPHA),
                    secondarySceneFbo.getAttachmentTexture(Attachment.LIGHT_SCATTERING));
        }

        // update Terrain Quadtree
        if (camera.isCameraMoved()) {
            if (sceneGraph.hasTerrain()) {
                ((GLTerrain) sceneGraph.getTerrain()).getQuadtree().signal();
            }
        }

        GLTexture prePostprocessingScene = transparencySceneRenderList.getObjectList().size() > 0 ?
                opaqueTransparencyBlending.getBlendedSceneTexture() : deferredLighting.getDeferredLightingSceneTexture();
        GLTexture currentScene = prePostprocessingScene;


        GLTexture lightScatteringMask = BaseContext.getConfig().getMultisampling_sampleCount() > 1 ?
                sampleCoverage.getLightScatteringMaskSingleSample() : primarySceneFbo.getAttachmentTexture(Attachment.LIGHT_SCATTERING);
        GLTexture specularEmissionDiffuseSsaoBloomMask = BaseContext.getConfig().getMultisampling_sampleCount() > 1 ?
                sampleCoverage.getSpecularEmissionBloomMaskSingleSample() : primarySceneFbo.getAttachmentTexture(Attachment.SPECULAR_EMISSION_DIFFUSE_SSAO_BLOOM);

        boolean doMotionBlur = camera.getPreviousPosition().sub(camera.getPosition()).length() > 0.04f
                || camera.getForward().sub(camera.getPreviousForward()).length() > 0.01f;
        boolean doFXAA = !camera.isCameraMoved() && !camera.isCameraRotated();


        //-----------------------------------------------//
        //                  render FXAA                  //
        //-----------------------------------------------//

        if (doFXAA && BaseContext.getConfig().isFxaaEnabled()) {
            fxaa.render(currentScene);
            currentScene = fxaa.getFxaaSceneTexture();
        }


        //-----------------------------------------------//
        //         render postprocessing effects         //
        //-----------------------------------------------//

        if (renderPostProcessingEffects) {

            //--------------------------------------------//
            //                    Bloom                   //
            //--------------------------------------------//

            if (BaseContext.getConfig().isBloomEnabled()) {
                bloom.render(prePostprocessingScene, currentScene, specularEmissionDiffuseSsaoBloomMask);
                currentScene = bloom.getBloomSceneTexture();
            }

            //--------------------------------------------//
            //             Light Scattering               //
            //--------------------------------------------//
            if (BaseContext.getConfig().isLightScatteringEnabled()) {
                sunlightScattering.render(currentScene, lightScatteringMask);
                currentScene = sunlightScattering.getSunLightScatteringSceneTexture();
            }

            //--------------------------------------------//
            //            depth of field blur             //
            //--------------------------------------------//

            if (BaseContext.getConfig().isDepthOfFieldBlurEnabled()) {
                depthOfField.render(primarySceneFbo.getAttachmentTexture(Attachment.DEPTH), currentScene);
                currentScene = depthOfField.getVerticalBlurSceneTexture();
            }

            //--------------------------------------------//
            //                  Underwater                //
            //--------------------------------------------//

            if (BaseContext.getConfig().isRenderUnderwater()) {
                underWaterRenderer.render(currentScene,
                        primarySceneFbo.getAttachmentTexture(Attachment.DEPTH));
                currentScene = underWaterRenderer.getUnderwaterSceneTexture();
            }


            //--------------------------------------------//
            //                Motion Blur                 //
            //--------------------------------------------//

            if (doMotionBlur && BaseContext.getConfig().isMotionBlurEnabled()) {
                motionBlur.render(currentScene,
                        primarySceneFbo.getAttachmentTexture(Attachment.DEPTH));
                currentScene = motionBlur.getMotionBlurSceneTexture();
            }
        }

        glViewport(0, 0, BaseContext.getConfig().getWindowWidth(), BaseContext.getConfig().getWindowHeight());

        if (BaseContext.getConfig().isRenderWireframe()
                || renderAlbedoBuffer) {
            if (BaseContext.getConfig().getMultisampling_sampleCount() > 1) {
                fullScreenQuadMultisample.setTexture(primarySceneFbo.getAttachmentTexture(Attachment.COLOR));
                fullScreenQuadMultisample.render();
            } else {
                fullScreenQuad.setTexture(primarySceneFbo.getAttachmentTexture(Attachment.COLOR));
                fullScreenQuad.render();
            }
        }
        if (renderNormalBuffer) {
            fullScreenQuadMultisample.setTexture(primarySceneFbo.getAttachmentTexture(Attachment.NORMAL));
            fullScreenQuadMultisample.render();
        }
        if (renderPositionBuffer) {
            fullScreenQuadMultisample.setTexture(primarySceneFbo.getAttachmentTexture(Attachment.POSITION));
            fullScreenQuadMultisample.render();
        }
        if (renderSampleCoverageMask) {
            fullScreenQuad.setTexture(sampleCoverage.getSampleCoverageMask());
            fullScreenQuad.render();
        }
        if (renderSSAOBuffer) {
            fullScreenQuad.setTexture(ssao.getSsaoBlurSceneTexture());
            fullScreenQuad.render();
        }
        if (renderDeferredLightingScene) {
            fullScreenQuad.setTexture(deferredLighting.getDeferredLightingSceneTexture());
            fullScreenQuad.render();
        }

        fullScreenQuad.setTexture(currentScene);
        fullScreenQuad.render();

        if (BaseContext.getConfig().isLensFlareEnabled()
                && !renderDeferredLightingScene && !renderSSAOBuffer
                && !renderSampleCoverageMask && !renderPositionBuffer
                && !renderNormalBuffer && !renderAlbedoBuffer) {

            primarySceneFbo.bind();
            LightHandler.doOcclusionQueries();
            primarySceneFbo.unbind();
            lensFlare.render();
        }

        if (gui != null) {
            gui.render();
        }

        glViewport(0, 0, config.getFrameWidth(), config.getFrameHeight());
    }

    @Override
    public void update() {

        super.update();

        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_G)) {
            BaseContext.getConfig().setRenderWireframe(!BaseContext.getConfig().isRenderWireframe());
        }

        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_1)) {
            if (renderAlbedoBuffer) {
                renderAlbedoBuffer = false;
            } else {
                renderAlbedoBuffer = true;
                renderNormalBuffer = false;
                renderPositionBuffer = false;
                renderSampleCoverageMask = false;
                renderSSAOBuffer = false;
                renderDeferredLightingScene = false;
            }
        }
        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_2)) {
            if (renderNormalBuffer) {
                renderNormalBuffer = false;
            } else {
                renderNormalBuffer = true;
                renderAlbedoBuffer = false;
                renderPositionBuffer = false;
                renderSampleCoverageMask = false;
                renderSSAOBuffer = false;
                renderDeferredLightingScene = false;
            }
        }
        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_3)) {
            if (renderPositionBuffer) {
                renderPositionBuffer = false;
            } else {
                renderPositionBuffer = true;
                renderAlbedoBuffer = false;
                renderNormalBuffer = false;
                renderSampleCoverageMask = false;
                renderSSAOBuffer = false;
                renderDeferredLightingScene = false;
            }
        }
        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_4)) {
            if (renderSampleCoverageMask) {
                renderSampleCoverageMask = false;
            } else {
                renderSampleCoverageMask = true;
                renderAlbedoBuffer = false;
                renderNormalBuffer = false;
                renderPositionBuffer = false;
                renderSSAOBuffer = false;
                renderDeferredLightingScene = false;
            }
        }
        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_5)) {
            if (renderSSAOBuffer) {
                renderSSAOBuffer = false;
            } else {
                renderSSAOBuffer = true;
                renderAlbedoBuffer = false;
                renderNormalBuffer = false;
                renderPositionBuffer = false;
                renderSampleCoverageMask = false;
                renderDeferredLightingScene = false;
            }
        }
        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_6)) {
            if (renderDeferredLightingScene) {
                renderDeferredLightingScene = false;
            } else {
                renderDeferredLightingScene = true;
                renderAlbedoBuffer = false;
                renderNormalBuffer = false;
                renderPositionBuffer = false;
                renderSampleCoverageMask = false;
                renderSSAOBuffer = false;
            }
        }
        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_7)) {
            BaseContext.getConfig().setLensFlareEnabled(!BaseContext.getConfig().isLensFlareEnabled());
        }
        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_8)) {
            BaseContext.getConfig().setSsaoEnabled(!BaseContext.getConfig().isSsaoEnabled());
        }
        if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_9)) {
            renderPostProcessingEffects = !renderPostProcessingEffects;
        }

        if (gui != null) {
            gui.update();
        }

        contrastController.update();

        if (sceneGraph.hasTerrain()) {
            TerrainPicking.getInstance().getTerrainPosition();
        }
    }

    @Override
    public void shutdown() {

        super.shutdown();

        instancingObjectHandler.signalAll();
        if (sceneGraph.hasTerrain()) {
            ((GLTerrain) sceneGraph.getTerrain()).getQuadtree().signal();
        }
    }

}
