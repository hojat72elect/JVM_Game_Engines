package com.badlogic.gdx.graphics.g3d.particles.renderers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.ColorInitializer;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.Rotation2dInitializer;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.ScaleInitializer;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.TextureRegionInitializer;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;

/**
 * A {@link ParticleControllerRenderer} which will render particles as billboards to a {@link BillboardParticleBatch} .
 */
public class BillboardRenderer extends ParticleControllerRenderer<BillboardControllerRenderData, BillboardParticleBatch> {

    public BillboardRenderer() {
        super(new BillboardControllerRenderData());
    }

    public BillboardRenderer(BillboardParticleBatch batch) {
        this();
        setBatch(batch);
    }

    @Override
    public void allocateChannels() {
        renderData.positionChannel = controller.particles.addChannel(ParticleChannels.Position);
        renderData.regionChannel = controller.particles.addChannel(ParticleChannels.TextureRegion, TextureRegionInitializer.get());
        renderData.colorChannel = controller.particles.addChannel(ParticleChannels.Color, ColorInitializer.get());
        renderData.scaleChannel = controller.particles.addChannel(ParticleChannels.Scale, ScaleInitializer.get());
        renderData.rotationChannel = controller.particles.addChannel(ParticleChannels.Rotation2D, Rotation2dInitializer.get());
    }

    @Override
    public ParticleControllerComponent copy() {
        return new BillboardRenderer(batch);
    }

    @Override
    public boolean isCompatible(ParticleBatch<?> batch) {
        return batch instanceof BillboardParticleBatch;
    }
}
