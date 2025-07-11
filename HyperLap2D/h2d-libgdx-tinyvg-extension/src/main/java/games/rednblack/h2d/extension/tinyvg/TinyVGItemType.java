package games.rednblack.h2d.extension.tinyvg;

import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectSet;
import games.rednblack.editor.renderer.commons.IExternalItemType;
import games.rednblack.editor.renderer.factory.component.ComponentFactory;
import games.rednblack.editor.renderer.resources.IResourceRetriever;
import games.rednblack.editor.renderer.systems.render.logic.DrawableLogic;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.HyperJson;

import java.io.File;
import java.util.HashMap;

public class TinyVGItemType implements IExternalItemType {
    public static final int TINYVG_TYPE = 11;
    public String tinyvgPath = "tinyvg";

    private ComponentFactory factory;
    private DrawableLogic drawableLogic;

    public TinyVGItemType() {
        factory = new TinyVGComponentFactory();
        drawableLogic = new TinyVGDrawableLogic();

        HyperJson.getJson().addClassTag(TinyVGVO.class.getSimpleName(), TinyVGVO.class);
    }

    @Override
    public int getTypeId() {
        return TINYVG_TYPE;
    }

    @Override
    public DrawableLogic getDrawable() {
        return drawableLogic;
    }

    @Override
    public IteratingSystem getSystem() {
        return null;
    }

    @Override
    public ComponentFactory getComponentFactory() {
        return factory;
    }

    @Override
    public void injectMappers() {
        ComponentRetriever.addMapper(TinyVGComponent.class);
    }

    @Override
    public boolean hasResources() {
        return true;
    }

    @Override
    public void loadExternalTypesAsync(IResourceRetriever rm, ObjectSet<String> assetsToLoad, HashMap<String, Object> assets) {
        // empty existing ones that are not scheduled to load
        for (String key : assets.keySet()) {
            if (!assetsToLoad.contains(key)) {
                assets.remove(key);
            }
        }

        for (String name : assetsToLoad) {
            assets.put(name, TinyVGUtils.load(Gdx.files.internal(formatResourcePath(name))));
        }
    }

    @Override
    public void loadExternalTypesSync(IResourceRetriever rm, ObjectSet<String> assetsToLoad, HashMap<String, Object> assets) {

    }

    public String formatResourcePath(String resName) {
        return tinyvgPath + File.separator + resName;
    }
}
