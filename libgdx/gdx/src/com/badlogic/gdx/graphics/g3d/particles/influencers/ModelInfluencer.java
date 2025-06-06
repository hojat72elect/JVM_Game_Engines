package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.ObjectChannel;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData.SaveData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * It's an {@link Influencer} which controls which {@link Model} will be assigned to the particles as {@link ModelInstance}.
 */
public abstract class ModelInfluencer extends Influencer {

    public Array<Model> models;
    ObjectChannel<ModelInstance> modelChannel;

    public ModelInfluencer() {
        this.models = new Array<Model>(true, 1, Model.class);
    }

    public ModelInfluencer(Model... models) {
        this.models = new Array<Model>(models);
    }

    public ModelInfluencer(ModelInfluencer influencer) {
        this(influencer.models.toArray(Model.class));
    }

    @Override
    public void allocateChannels() {
        modelChannel = controller.particles.addChannel(ParticleChannels.ModelInstance);
    }

    @Override
    public void save(AssetManager manager, ResourceData resources) {
        SaveData data = resources.createSaveData();
        for (Model model : models)
            data.saveAsset(manager.getAssetFileName(model), Model.class);
    }

    @Override
    public void load(AssetManager manager, ResourceData resources) {
        SaveData data = resources.getSaveData();
        AssetDescriptor descriptor;
        while ((descriptor = data.loadAsset()) != null) {
            Model model = (Model) manager.get(descriptor);
            if (model == null) throw new RuntimeException("Model is null");
            models.add(model);
        }
    }

    /**
     * Assigns the first model of {@link ModelInfluencer#models} to the particles.
     */
    public static class Single extends ModelInfluencer {

        public Single() {
            super();
        }

        public Single(Single influencer) {
            super(influencer);
        }

        public Single(Model... models) {
            super(models);
        }

        @Override
        public void init() {
            Model first = models.first();
            for (int i = 0, c = controller.emitter.maxParticleCount; i < c; ++i) {
                modelChannel.data[i] = new ModelInstance(first);
            }
        }

        @Override
        public Single copy() {
            return new Single(this);
        }
    }

    /**
     * Assigns a random model of {@link ModelInfluencer#models} to the particles.
     */
    public static class Random extends ModelInfluencer {
        ModelInstancePool pool;

        public Random() {
            super();
            pool = new ModelInstancePool();
        }

        public Random(Random influencer) {
            super(influencer);
            pool = new ModelInstancePool();
        }

        public Random(Model... models) {
            super(models);
            pool = new ModelInstancePool();
        }

        @Override
        public void init() {
            pool.clear();
        }

        @Override
        public void activateParticles(int startIndex, int count) {
            for (int i = startIndex, c = startIndex + count; i < c; ++i) {
                modelChannel.data[i] = pool.obtain();
            }
        }

        @Override
        public void killParticles(int startIndex, int count) {
            for (int i = startIndex, c = startIndex + count; i < c; ++i) {
                pool.free(modelChannel.data[i]);
                modelChannel.data[i] = null;
            }
        }

        @Override
        public Random copy() {
            return new Random(this);
        }

        private class ModelInstancePool extends Pool<ModelInstance> {
            public ModelInstancePool() {
            }

            @Override
            public ModelInstance newObject() {
                return new ModelInstance(models.random());
            }
        }
    }
}
