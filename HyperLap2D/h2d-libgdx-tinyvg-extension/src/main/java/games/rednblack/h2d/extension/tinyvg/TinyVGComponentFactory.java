package games.rednblack.h2d.extension.tinyvg;

import com.artemis.ComponentMapper;
import com.artemis.EntityTransmuter;
import com.artemis.EntityTransmuterFactory;
import com.badlogic.gdx.physics.box2d.World;
import dev.lyze.gdxtinyvg.TinyVG;
import games.rednblack.editor.renderer.box2dLight.RayHandler;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.data.MainItemVO;
import games.rednblack.editor.renderer.data.ProjectInfoVO;
import games.rednblack.editor.renderer.data.ResolutionEntryVO;
import games.rednblack.editor.renderer.factory.component.ComponentFactory;
import games.rednblack.editor.renderer.resources.IResourceRetriever;

public class TinyVGComponentFactory extends ComponentFactory {

    protected ComponentMapper<TinyVGComponent> tinyVGCM;

    private EntityTransmuter transmuter;

    public TinyVGComponentFactory() {
        super();
    }

    @Override
    public void injectDependencies(com.artemis.World engine, RayHandler rayHandler, World world, IResourceRetriever rm) {
        super.injectDependencies(engine, rayHandler, world, rm);

        transmuter = new EntityTransmuterFactory(engine)
                .add(TinyVGComponent.class)
                .build();
    }

    @Override
    public void transmuteEntity(int entity) {
        transmuter.transmute(entity);
    }

    @Override
    public int getEntityType() {
        return TinyVGItemType.TINYVG_TYPE;
    }

    @Override
    public void setInitialData(int entity, Object data) {
        tinyVGCM.get(entity).imageName = (String) data;
    }

    @Override
    public Class<TinyVGVO> getVOType() {
        return TinyVGVO.class;
    }

    @Override
    public void initializeSpecialComponentsFromVO(int entity, MainItemVO voG) {
        TinyVGVO vo = (TinyVGVO) voG;
        TinyVGComponent tinyVGComponent = tinyVGCM.get(entity);
        tinyVGComponent.imageName = vo.imageName;
    }

    @Override
    protected void initializeTransientComponents(int entity) {
        super.initializeTransientComponents(entity);

        TinyVGComponent component = tinyVGCM.get(entity);
        component.tinyVG = (TinyVG) rm.getExternalItemType(getEntityType(), component.imageName);
    }

    @Override
    protected void initializeDimensionsComponent(int entity) {
        TinyVGComponent component = tinyVGCM.get(entity);
        DimensionsComponent dimension = dimensionsCM.get(entity);

        ResolutionEntryVO resolutionEntryVO = rm.getLoadedResolution();
        ProjectInfoVO projectInfoVO = rm.getProjectVO();
        float multiplier = resolutionEntryVO.getMultiplier(rm.getProjectVO().originalResolution);

        component.tinyVG.setScale(multiplier / projectInfoVO.pixelToWorld);

        dimension.width = component.tinyVG.getScaledWidth();
        dimension.height = component.tinyVG.getScaledHeight();
    }
}
