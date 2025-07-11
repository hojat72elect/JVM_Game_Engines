package games.rednblack.h2d.extension.bvb;

import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectSet;
import com.esotericsoftware.spine.SkeletonJson;
import games.rednblack.editor.renderer.factory.component.ComponentFactory;
import games.rednblack.editor.renderer.resources.IResourceRetriever;
import games.rednblack.editor.renderer.systems.render.logic.DrawableLogic;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.renderer.utils.HyperJson;
import games.rednblack.h2d.extension.spine.ResourceRetrieverAttachmentLoader;
import games.rednblack.h2d.extension.spine.SpineItemType;
import games.rednblack.talos.runtime.bvb.BVB;

import java.io.File;
import java.util.HashMap;

public class BVBItemType extends SpineItemType {
    private final ComponentFactory factory;
    private final IteratingSystem system;
    private final BVBDrawableLogic drawableLogic;

    public BVBItemType() {
        factory = new BVBComponentFactory();
        system = new BVBSystem();
        drawableLogic = new BVBDrawableLogic();
    }

    @Override
    public DrawableLogic getDrawable() {
        return drawableLogic;
    }

    @Override
    public IteratingSystem getSystem() {
        return system;
    }

    @Override
    public ComponentFactory getComponentFactory() {
        return factory;
    }

    @Override
    public void injectMappers() {
        super.injectMappers();
        ComponentRetriever.addMapper(BVBComponent.class);
    }

    @Override
    public void loadExternalTypesAsync(IResourceRetriever rm, ObjectSet<String> assetsToLoad, HashMap<String, Object> assets) {
        // empty existing ones that are not scheduled to load
        for (String key : assets.keySet()) {
            if (!assetsToLoad.contains(key)) {
                assets.remove(key);
            }
        }

        // load scheduled
        for (String name : assetsToLoad) {
            BVBDataObject bvbDataObject = new BVBDataObject();
            bvbDataObject.skeletonJson = new SkeletonJson(new ResourceRetrieverAttachmentLoader(name, rm, drawableLogic));
            bvbDataObject.skeletonData = bvbDataObject.skeletonJson.readSkeletonData(Gdx.files.internal(formatResourcePath(name)));
            FileHandle bvb = Gdx.files.internal(formatBVBResourcePath(name));
            if (bvb.exists())
                bvbDataObject.bvbData = HyperJson.getJson().fromJson(BVB.class, bvb);

            assets.put(name, bvbDataObject);
        }
    }

    protected String formatBVBResourcePath(String resName) {
        return spineAnimationsPath + File.separator + resName + File.separator + resName + "-bvb.json";
    }
}
