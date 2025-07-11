package games.rednblack.h2d.extension.bvb;

import com.artemis.PooledComponent;

public class BVBComponent extends PooledComponent {

    public transient SlotRangeSkeletonContainer skeletonContainer = null;

    @Override
    protected void reset() {
        skeletonContainer = null;
    }
}
