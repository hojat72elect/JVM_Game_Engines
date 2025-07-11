package games.rednblack.h2d.extension.tinyvg;

import com.artemis.World;
import games.rednblack.editor.renderer.data.MainItemVO;
import games.rednblack.editor.renderer.factory.EntityFactory;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class TinyVGVO extends MainItemVO {
    public String imageName = "";

    public TinyVGVO() {
        super();
    }

    public TinyVGVO(TinyVGVO vo) {
        super(vo);
        imageName = vo.imageName;
    }

    @Override
    public void loadFromEntity(int entity, World engine, EntityFactory entityFactory) {
        super.loadFromEntity(entity, engine, entityFactory);

        TinyVGComponent tinyVGComponent = ComponentRetriever.get(entity, TinyVGComponent.class, engine);
        imageName = tinyVGComponent.imageName;
    }

    @Override
    public String getResourceName() {
        return imageName;
    }
}
