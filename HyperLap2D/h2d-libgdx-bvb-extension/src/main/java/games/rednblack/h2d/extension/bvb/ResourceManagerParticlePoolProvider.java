package games.rednblack.h2d.extension.bvb;

import games.rednblack.editor.renderer.resources.IResourceRetriever;
import games.rednblack.h2d.extension.talos.TalosItemType;
import games.rednblack.talos.runtime.ParticleEffectInstancePool;
import games.rednblack.talos.runtime.bvb.BVBParticleEffectPoolProvider;

public class ResourceManagerParticlePoolProvider implements BVBParticleEffectPoolProvider {
    protected final IResourceRetriever rm;

    public ResourceManagerParticlePoolProvider(IResourceRetriever resourceRetriever) {
        rm = resourceRetriever;
    }

    @Override
    public ParticleEffectInstancePool getPool(String effectName) {
        return (ParticleEffectInstancePool) rm.getExternalItemType(TalosItemType.TALOS_TYPE, effectName + ".p");
    }
}
