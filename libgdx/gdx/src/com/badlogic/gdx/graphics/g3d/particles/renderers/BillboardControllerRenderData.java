package com.badlogic.gdx.graphics.g3d.particles.renderers;

import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;

/**
 * Render data used by billboard particle batches
 */
public class BillboardControllerRenderData extends ParticleControllerRenderData {
    public FloatChannel regionChannel, colorChannel, scaleChannel, rotationChannel;
}
