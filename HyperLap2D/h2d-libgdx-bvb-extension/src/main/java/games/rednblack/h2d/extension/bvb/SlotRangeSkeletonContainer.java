package games.rednblack.h2d.extension.bvb;

import games.rednblack.h2d.extension.spine.SlotRange;
import games.rednblack.talos.runtime.bvb.SkeletonContainer;
import games.rednblack.talos.runtime.render.ParticleRenderer;

public class SlotRangeSkeletonContainer extends SkeletonContainer {

    private BVBSkeletonRenderSeparator skeletonRenderSeparator;
    private SlotRange slotRange;

    public SlotRangeSkeletonContainer(SkeletonContainer skeletonContainer) {
        super(skeletonContainer);
    }

    public void setSkeletonRenderSeparator(BVBSkeletonRenderSeparator skeletonRenderSeparator) {
        this.skeletonRenderSeparator = skeletonRenderSeparator;
    }

    public void setSlotRange(SlotRange slotRange) {
        this.slotRange = slotRange;
    }

    @Override
    protected void drawSkeletonAndVFXNested(ParticleRenderer particleRenderer) {
        if (slotRange != null && skeletonRenderSeparator != null)
            skeletonRenderSeparator.draw(particleRenderer, this, skeleton, slotRange);
    }
}
