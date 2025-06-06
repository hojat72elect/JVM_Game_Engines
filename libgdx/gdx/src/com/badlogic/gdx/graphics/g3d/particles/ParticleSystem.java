package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Singleton class which manages the particle effects. It's a utility class to ease particle batches management and particle
 * effects update.
 */
public final class ParticleSystem implements RenderableProvider {
    private static ParticleSystem instance;
    private final Array<ParticleBatch<?>> batches;
    private final Array<ParticleEffect> effects;

    public ParticleSystem() {
        batches = new Array<ParticleBatch<?>>();
        effects = new Array<ParticleEffect>();
    }

    /**
     * @deprecated Please directly use the constructor
     */
    @Deprecated
    public static ParticleSystem get() {
        if (instance == null) instance = new ParticleSystem();
        return instance;
    }

    public void add(ParticleBatch<?> batch) {
        batches.add(batch);
    }

    public void add(ParticleEffect effect) {
        effects.add(effect);
    }

    public void remove(ParticleEffect effect) {
        effects.removeValue(effect, true);
    }

    /**
     * Removes all the effects added to the system
     */
    public void removeAll() {
        effects.clear();
    }

    /**
     * Updates the simulation of all effects
     */
    public void update() {
        for (ParticleEffect effect : effects) {
            effect.update();
        }
    }

    public void updateAndDraw() {
        for (ParticleEffect effect : effects) {
            effect.update();
            effect.draw();
        }
    }

    public void update(float deltaTime) {
        for (ParticleEffect effect : effects) {
            effect.update(deltaTime);
        }
    }

    public void updateAndDraw(float deltaTime) {
        for (ParticleEffect effect : effects) {
            effect.update(deltaTime);
            effect.draw();
        }
    }

    /**
     * Must be called one time per frame before any particle effect drawing operation will occur.
     */
    public void begin() {
        for (ParticleBatch<?> batch : batches)
            batch.begin();
    }

    /**
     * Draws all the particle effects. Call {@link #begin()} before this method and {@link #end()} after.
     */
    public void draw() {
        for (ParticleEffect effect : effects) {
            effect.draw();
        }
    }

    /**
     * Must be called one time per frame at the end of all drawing operations.
     */
    public void end() {
        for (ParticleBatch<?> batch : batches)
            batch.end();
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        for (ParticleBatch<?> batch : batches)
            batch.getRenderables(renderables, pool);
    }

    public Array<ParticleBatch<?>> getBatches() {
        return batches;
    }
}
