package games.rednblack.h2d.extension.tinyvg;

import com.artemis.PooledComponent;
import dev.lyze.gdxtinyvg.TinyVG;

public class TinyVGComponent extends PooledComponent {
    public transient TinyVG tinyVG = null;

    public String imageName = "";

    @Override
    protected void reset() {
        tinyVG = null;
        imageName = "";
    }
}
