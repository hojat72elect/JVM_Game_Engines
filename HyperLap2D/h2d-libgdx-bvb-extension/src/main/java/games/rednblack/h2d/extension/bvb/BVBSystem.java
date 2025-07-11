package games.rednblack.h2d.extension.bvb;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import games.rednblack.h2d.extension.spine.SpineComponent;
import games.rednblack.h2d.extension.spine.SpineSystem;

@All({SpineComponent.class})
public class BVBSystem extends SpineSystem {
    protected ComponentMapper<BVBComponent> bvbCM;

    @Override
    protected void process(int entity) {
        super.process(entity);

        BVBComponent bvbComponent = bvbCM.get(entity);
        if (bvbComponent == null) return;

        bvbComponent.skeletonContainer.update(world.getDelta());
    }
}
