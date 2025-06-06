package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData.SaveData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * It's an {@link Influencer} which assigns a region of a {@link Texture} to the particles.
 */
public abstract class RegionInfluencer extends Influencer {

    private final static String ASSET_DATA = "atlasAssetData";
    public Array<AspectTextureRegion> regions;
    public String atlasName;
    FloatChannel regionChannel;

    public RegionInfluencer(int regionsCount) {
        this.regions = new Array<AspectTextureRegion>(false, regionsCount, AspectTextureRegion.class);
    }

    public RegionInfluencer() {
        this(1);
        AspectTextureRegion aspectRegion = new AspectTextureRegion();
        aspectRegion.u = aspectRegion.v = 0;
        aspectRegion.u2 = aspectRegion.v2 = 1;
        aspectRegion.halfInvAspectRatio = 0.5f;
        regions.add(aspectRegion);
    }

    /**
     * All the regions must be defined on the same Texture
     */
    public RegionInfluencer(TextureRegion... regions) {
        setAtlasName(null);
        this.regions = new Array<AspectTextureRegion>(false, regions.length, AspectTextureRegion.class);
        add(regions);
    }

    public RegionInfluencer(Texture texture) {
        this(new TextureRegion(texture));
    }

    public RegionInfluencer(RegionInfluencer regionInfluencer) {
        this(regionInfluencer.regions.size);
        regions.ensureCapacity(regionInfluencer.regions.size);
        for (int i = 0; i < regionInfluencer.regions.size; ++i) {
            regions.add(new AspectTextureRegion(regionInfluencer.regions.get(i)));
        }
    }

    public void setAtlasName(String atlasName) {
        this.atlasName = atlasName;
    }

    public void add(TextureRegion... regions) {
        this.regions.ensureCapacity(regions.length);
        for (TextureRegion region : regions) {
            this.regions.add(new AspectTextureRegion(region));
        }
    }

    public void clear() {
        atlasName = null;
        regions.clear();
    }

    @Override
    public void load(AssetManager manager, ResourceData resources) {
        super.load(manager, resources);
        SaveData data = resources.getSaveData(ASSET_DATA);
        if (data == null) {
            return;
        }
        TextureAtlas atlas;
        atlas = (TextureAtlas) manager.get(data.loadAsset());
        for (AspectTextureRegion atr : regions) {
            atr.updateUV(atlas);
        }
    }

    @Override
    public void save(AssetManager manager, ResourceData resources) {
        super.save(manager, resources);
        if (atlasName != null) {
            SaveData data = resources.getSaveData(ASSET_DATA);
            if (data == null) {
                data = resources.createSaveData(ASSET_DATA);
            }
            data.saveAsset(atlasName, TextureAtlas.class);
        }
    }

    @Override
    public void allocateChannels() {
        regionChannel = controller.particles.addChannel(ParticleChannels.TextureRegion);
    }

    @Override
    public void write(Json json) {
        json.writeValue("regions", regions, Array.class, AspectTextureRegion.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        regions.clear();
        regions.addAll(json.readValue("regions", Array.class, AspectTextureRegion.class, jsonData));
    }

    /**
     * Assigns the first region of {@link RegionInfluencer#regions} to the particles.
     */
    public static class Single extends RegionInfluencer {
        public Single() {
        }

        public Single(Single regionInfluencer) {
            super(regionInfluencer);
        }

        public Single(TextureRegion textureRegion) {
            super(textureRegion);
        }

        public Single(Texture texture) {
            super(texture);
        }

        @Override
        public void init() {
            AspectTextureRegion region = regions.items[0];
            for (int i = 0,
                 c = controller.emitter.maxParticleCount * regionChannel.strideSize; i < c; i += regionChannel.strideSize) {
                regionChannel.data[i + ParticleChannels.UOffset] = region.u;
                regionChannel.data[i + ParticleChannels.VOffset] = region.v;
                regionChannel.data[i + ParticleChannels.U2Offset] = region.u2;
                regionChannel.data[i + ParticleChannels.V2Offset] = region.v2;
                regionChannel.data[i + ParticleChannels.HalfWidthOffset] = 0.5f;
                regionChannel.data[i + ParticleChannels.HalfHeightOffset] = region.halfInvAspectRatio;
            }
        }

        @Override
        public Single copy() {
            return new Single(this);
        }
    }

    /**
     * Assigns a random region of {@link RegionInfluencer#regions} to the particles.
     */
    public static class Random extends RegionInfluencer {
        public Random() {
        }

        public Random(Random regionInfluencer) {
            super(regionInfluencer);
        }

        public Random(TextureRegion textureRegion) {
            super(textureRegion);
        }

        public Random(Texture texture) {
            super(texture);
        }

        @Override
        public void activateParticles(int startIndex, int count) {
            for (int i = startIndex * regionChannel.strideSize,
                 c = i + count * regionChannel.strideSize; i < c; i += regionChannel.strideSize) {
                AspectTextureRegion region = regions.random();
                regionChannel.data[i + ParticleChannels.UOffset] = region.u;
                regionChannel.data[i + ParticleChannels.VOffset] = region.v;
                regionChannel.data[i + ParticleChannels.U2Offset] = region.u2;
                regionChannel.data[i + ParticleChannels.V2Offset] = region.v2;
                regionChannel.data[i + ParticleChannels.HalfWidthOffset] = 0.5f;
                regionChannel.data[i + ParticleChannels.HalfHeightOffset] = region.halfInvAspectRatio;
            }
        }

        @Override
        public Random copy() {
            return new Random(this);
        }
    }

    /**
     * Assigns a region to the particles using the particle life percent to calculate the current index in the
     * {@link RegionInfluencer#regions} array.
     */
    public static class Animated extends RegionInfluencer {
        FloatChannel lifeChannel;

        public Animated() {
        }

        public Animated(Animated regionInfluencer) {
            super(regionInfluencer);
        }

        public Animated(TextureRegion textureRegion) {
            super(textureRegion);
        }

        public Animated(Texture texture) {
            super(texture);
        }

        @Override
        public void allocateChannels() {
            super.allocateChannels();
            lifeChannel = controller.particles.addChannel(ParticleChannels.Life);
        }

        @Override
        public void update() {
            for (int i = 0, l = ParticleChannels.LifePercentOffset, c = controller.particles.size
                    * regionChannel.strideSize; i < c; i += regionChannel.strideSize, l += lifeChannel.strideSize) {
                AspectTextureRegion region = regions.get((int) (lifeChannel.data[l] * (regions.size - 1)));
                regionChannel.data[i + ParticleChannels.UOffset] = region.u;
                regionChannel.data[i + ParticleChannels.VOffset] = region.v;
                regionChannel.data[i + ParticleChannels.U2Offset] = region.u2;
                regionChannel.data[i + ParticleChannels.V2Offset] = region.v2;
                regionChannel.data[i + ParticleChannels.HalfWidthOffset] = 0.5f;
                regionChannel.data[i + ParticleChannels.HalfHeightOffset] = region.halfInvAspectRatio;
            }
        }

        @Override
        public Animated copy() {
            return new Animated(this);
        }
    }

    /**
     * It's a class used internally by the {@link RegionInfluencer} to represent a texture region. It contains the uv coordinates
     * of the region and the region inverse aspect ratio.
     */
    public static class AspectTextureRegion {
        public float u, v, u2, v2;
        public float halfInvAspectRatio;
        public String imageName;

        public AspectTextureRegion() {
        }

        public AspectTextureRegion(AspectTextureRegion aspectTextureRegion) {
            set(aspectTextureRegion);
        }

        public AspectTextureRegion(TextureRegion region) {
            set(region);
        }

        public void set(TextureRegion region) {
            this.u = region.getU();
            this.v = region.getV();
            this.u2 = region.getU2();
            this.v2 = region.getV2();
            this.halfInvAspectRatio = 0.5f * ((float) region.getRegionHeight() / region.getRegionWidth());
            if (region instanceof TextureAtlas.AtlasRegion) {
                this.imageName = ((TextureAtlas.AtlasRegion) region).name;
            }
        }

        public void set(AspectTextureRegion aspectTextureRegion) {
            u = aspectTextureRegion.u;
            v = aspectTextureRegion.v;
            u2 = aspectTextureRegion.u2;
            v2 = aspectTextureRegion.v2;
            halfInvAspectRatio = aspectTextureRegion.halfInvAspectRatio;
            imageName = aspectTextureRegion.imageName;
        }

        public void updateUV(TextureAtlas atlas) {
            if (imageName == null) {
                return;
            }
            TextureAtlas.AtlasRegion region = atlas.findRegion(imageName);
            this.u = region.getU();
            this.v = region.getV();
            this.u2 = region.getU2();
            this.v2 = region.getV2();
            this.halfInvAspectRatio = 0.5f * ((float) region.getRegionHeight() / region.getRegionWidth());
        }
    }
}
