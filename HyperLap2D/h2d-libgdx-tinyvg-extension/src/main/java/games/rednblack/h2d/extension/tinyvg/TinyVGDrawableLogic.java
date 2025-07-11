package games.rednblack.h2d.extension.tinyvg;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import dev.lyze.gdxtinyvg.drawers.TinyVGShapeDrawer;
import games.rednblack.editor.renderer.SceneLoader;
import games.rednblack.editor.renderer.components.TintComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.systems.render.logic.DrawableLogic;
import games.rednblack.editor.renderer.utils.TransformMathUtils;

public class TinyVGDrawableLogic implements DrawableLogic {
    protected ComponentMapper<TinyVGComponent> tinyVGComponentMapper;
    protected ComponentMapper<TintComponent> tintComponentComponentMapper;
    protected ComponentMapper<TransformComponent> transformComponentMapper;

    protected SceneLoader sceneLoader;

    private TinyVGShapeDrawer drawer;

    public TinyVGDrawableLogic() { }

    @Override
    public void draw(Batch batch, int entity, float parentAlpha, RenderingType renderingType) {
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE);
        if (drawer == null)
            drawer = new CpuTinyVGShapeDrawer(batch, sceneLoader.getRm().getTextureRegion("white-pixel"));

        TintComponent tintComponent = tintComponentComponentMapper.get(entity);
        TinyVGComponent tinyVGComponent = tinyVGComponentMapper.get(entity);
        TransformComponent transformComponent = transformComponentMapper.get(entity);

        TransformMathUtils.computeTransform(transformComponent).mulLeft(batch.getTransformMatrix());
        TransformMathUtils.applyTransform(batch, transformComponent);

        drawer.setColor(tintComponent.color);
        tinyVGComponent.tinyVG.draw(drawer);

        TransformMathUtils.resetTransform(batch, transformComponent);
    }

    @Override
    public void beginPipeline() {

    }

    @Override
    public void endPipeline() {

    }
}
