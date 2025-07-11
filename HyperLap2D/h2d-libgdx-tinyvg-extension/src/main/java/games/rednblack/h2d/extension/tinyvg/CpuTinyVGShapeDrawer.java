package games.rednblack.h2d.extension.tinyvg;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dev.lyze.gdxtinyvg.drawers.TinyVGShapeDrawer;
import dev.lyze.gdxtinyvg.enums.StyleType;
import dev.lyze.gdxtinyvg.styles.GradientStyle;
import dev.lyze.gdxtinyvg.utils.WhitePixelUtils;
import games.rednblack.editor.renderer.utils.CpuBatch;

public class CpuTinyVGShapeDrawer extends TinyVGShapeDrawer {

    public CpuTinyVGShapeDrawer(Batch batch, TextureRegion region) {
        super(batch, region);
    }

    public CpuTinyVGShapeDrawer(Batch batch) {
        super(batch, WhitePixelUtils.createWhitePixelTexture());
    }

    @Override
    public void applyShaderValues() {
        Batch batch = getBatch();
        if (getGradientStyle() != StyleType.FLAT && batch instanceof CpuBatch) {
            CpuBatch cpuBatch = (CpuBatch) batch;
            cpuBatch.flushAndSyncTransformMatrix();
        }
        super.applyShaderValues();
    }
}
