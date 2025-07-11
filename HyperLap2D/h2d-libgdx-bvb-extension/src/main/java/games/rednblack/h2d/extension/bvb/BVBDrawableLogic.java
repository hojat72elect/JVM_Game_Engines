package games.rednblack.h2d.extension.bvb;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import games.rednblack.editor.renderer.components.TintComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.h2d.extension.spine.SlotRange;
import games.rednblack.h2d.extension.spine.SpineComponent;
import games.rednblack.h2d.extension.spine.SpineDrawableLogic;
import games.rednblack.h2d.extension.talos.TalosRenderer;

public class BVBDrawableLogic extends SpineDrawableLogic {
    protected ComponentMapper<BVBComponent> bvbCM;
    private final BVBSkeletonRenderSeparator bvbSkeletonRenderSeparator = new BVBSkeletonRenderSeparator();
    private final TalosRenderer talosRenderer = new TalosRenderer();

    @Override
    public void draw(Batch batch, int entity, float parentAlpha, RenderingType renderingType) {
        BVBComponent bvbComponent = bvbCM.get(entity);
        if (bvbComponent == null || bvbComponent.skeletonContainer == null) {
            super.draw(batch, entity, parentAlpha, renderingType);
            return;
        }

        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE);
        normalMap = renderingType == RenderingType.NORMAL_MAP;

        SpineComponent spineObjectComponent = spineMapper.get(entity);
        TransformComponent curTransform = transformComponentMapper.get(entity);

        TintComponent tint = tintComponentMapper.get(entity);

        talosRenderer.setBatch(batch);
        talosRenderer.setEntityColor(tint.color, parentAlpha);

        spineObjectComponent.skeleton.getColor().set(tint.color);

        Color color = spineObjectComponent.skeleton.getColor();
        float oldAlpha = color.a;
        spineObjectComponent.skeleton.getColor().a *= parentAlpha;

        computeTransform(spineObjectComponent, curTransform).mulLeft(batch.getTransformMatrix());
        curTransform.oldTransform.set(batch.getTransformMatrix());
        batch.setTransformMatrix(curTransform.computedTransform);

        if (spineObjectComponent.splitRenderingRangeIndex < spineObjectComponent.splitRenderingRange.size) {
            SlotRange slotRange = spineObjectComponent.splitRenderingRange.get(spineObjectComponent.splitRenderingRangeIndex);

            bvbComponent.skeletonContainer.setSkeletonRenderSeparator(bvbSkeletonRenderSeparator);
            bvbComponent.skeletonContainer.setSlotRange(slotRange);
            bvbComponent.skeletonContainer.draw(talosRenderer);

            spineObjectComponent.splitRenderingRangeIndex++;
        }

        batch.setTransformMatrix(curTransform.oldTransform);

        color.a = oldAlpha;

        normalMap = false;
    }
}
