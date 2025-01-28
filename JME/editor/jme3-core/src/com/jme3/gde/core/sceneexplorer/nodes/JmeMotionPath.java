/*
 *  Copyright (c) 2009-2016 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.core.sceneexplorer.nodes;

import com.jme3.cinematic.MotionPath;
import com.jme3.gde.core.icons.IconList;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.sceneexplorer.nodes.actions.MotionPathPopup;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Spline;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Curve;
import java.awt.Image;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;

/**
 * This Class actually represents the MotionPath in the SceneComposer.<br>
 * It is added and managed by {@link JmeMotionPathChildren }
 *
 * @author MeFisto94
 */
@org.openide.util.lookup.ServiceProvider(service = SceneExplorerNode.class)
@SuppressWarnings({"unchecked", "rawtypes", "OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
public class JmeMotionPath extends AbstractSceneExplorerNode {

    private static Image smallImage = IconList.chimpSmile.getImage();
    private MotionPath motionPath;
    private JmeMotionEvent motionEvent;
    private float debugBoxExtents = 0.5f;
    private Spatial spatial;

    public JmeMotionPath() {
    }

    public JmeMotionPath(MotionPath motionPath, JmeMotionEvent parent, JmeVector3fChildren children) {
        super(children);

        this.motionPath = motionPath;
        getLookupContents().add(motionPath);
        getLookupContents().add(this);
        getLookupContents().add(children);
        super.setName("MotionPath");
        super.setDisplayName("Motion Path");
        children.setJmeMotionPath(this);
        motionEvent = parent;

        updateSpline(false);
    }

    //<editor-fold desc="Some Overrides for the Node">
    @Override
    public Image getIcon(int type) {
        return smallImage;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return smallImage;
    }

    @Override
    public Action[] getActions(boolean context) {
        MotionPathPopup m = new MotionPathPopup(this);
        return new Action[]{
            m.getAddAction(),
            m,
            SystemAction.get(PropertiesAction.class),
            SystemAction.get(DeleteAction.class)
        };
    }
    //</editor-fold>

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("Motion Path");
        set.setName(MotionPath.class.getName());
        set.setShortDescription("These are the Properties of the Motion Event's Motion Path");

        if (motionPath == null) {
            return sheet;
        }

        Property<?> prop = makeEmbedProperty(this, getExplorerNodeClass(), motionPath.getPathSplineType().getClass(), "getPathSplineType", "setPathSplineType", "PathSplineType");
        prop.setShortDescription("Sets the Type of the Paths' Spline. This will define how the single waypoints are interpolated (linear, curvy)");
        set.put(prop);

        prop = makeEmbedProperty(this, getExplorerNodeClass(), float.class, "getCurveTension", "setCurveTension", "Curve Tension");
        prop.setShortDescription("Sets the Curves' Tension. This defines how \"Curvy\" a curve will be. A tension of 0 would be completely linear.");
        set.put(prop);

        prop = makeProperty(motionPath, boolean.class, "isCycle", "setCycle", "Cycle?");
        prop.setShortDescription("Should the Path be a Cycle? This essentially means it will be looped. (Starting from the beginning after we're finished)");
        set.put(prop);

        prop = makeProperty(motionPath, int.class, "getLength", null, "Path Length");
        prop.setShortDescription("This is the total length this path has");
        set.put(prop);

        prop = makeEmbedProperty(motionPath, motionPath.getClass(), int.class, "getNbWayPoints", null, "Number of Waypoints");
        prop.setShortDescription("Shows the Number of Waypoints this Path consists of");
        set.put(prop);

        sheet.put(set);

        set = Sheet.createPropertiesSet();
        set.setDisplayName("Motion Path SDK");
        set.setName("MotionPathSDK");
        set.setShortDescription("These are SDK-dependent Settings which have nothing to do with MotionEvent or MotionPath in the first place.");

        prop = makeEmbedProperty(this, JmeMotionPath.class, float.class, "getDebugBoxExtents", "setDebugBoxExtents", "DebugBox Extents");
        prop.setShortDescription("The DebugBox Extents defines how big the Debug Boxes (i.e. the Boxes you see for each Waypoint) are. Note: The BoxSize is 2 * extents");
        set.put(prop);
        sheet.put(set);

        return sheet;
    }

    public MotionPath getMotionPath() {
        return motionPath;
    }

    public JmeMotionEvent getMotionEvent() {
        return motionEvent;
    }

    //<editor-fold desc="Properties Getter/Setter">
    public float getDebugBoxExtents() {
        return debugBoxExtents;
    }

    public void setDebugBoxExtents(float extents) {
        debugBoxExtents = extents;

        if (getChildren() != null) {
            for (Node n : getChildren().getNodes()) {
                if (n instanceof JmeVector3f) {
                    ((JmeVector3f) n).updateBox();
                } else {
                    Logger.getLogger(JmeMotionPath.class.getName()).log(Level.WARNING, "JmeMotionPath has some unknown Children...");
                }
            }
        }
    }

    public Spline.SplineType getPathSplineType() {
        return motionPath.getPathSplineType();
    }

    public void setPathSplineType(Spline.SplineType sType) {
        if (sType == Spline.SplineType.Nurb) {
            Logger.getLogger(JmeMotionPath.class.getName()).log(Level.SEVERE, "Nurb Curves aren't possible at the moment (they require additional helper points). Reverting to Catmull..");
            setPathSplineType(Spline.SplineType.CatmullRom);
            return;
        } else if (sType == Spline.SplineType.Bezier) {
            Logger.getLogger(JmeMotionPath.class.getName()).log(Level.SEVERE, "Bezier Curves are bugged and crash the SDK. Reverting to Catmull..");
            setPathSplineType(Spline.SplineType.CatmullRom);
            return;
        }

        motionPath.setPathSplineType(sType);
        updateSpline(true);
    }

    public float getCurveTension() {
        return motionPath.getCurveTension();
    }

    public void setCurveTension(float f) {
        motionPath.setCurveTension(f);
        updateSpline(true);
    }
    //</editor-fold>

    @Override
    public Class getExplorerObjectClass() {
        return MotionPath.class;
    }

    @Override
    public Class getExplorerNodeClass() {
        return JmeMotionPath.class;
    }

    @Override
    public org.openide.nodes.Node[] createNodes(Object key, DataObject key2, boolean cookie) {
        return null;
    }

    public void refreshChildren() {
        ((JmeVector3fChildren) this.jmeChildren).refreshChildren(true);
        updateSpline(false);
    }

    @Override
    public void destroy() throws IOException {
        for (Node n : getChildren().getNodes()) {
            ((JmeVector3f) n).destroy();
        }
        super.destroy();
        ((AbstractSceneExplorerNode) getParentNode()).refresh(true);
    }

    public void enableDebugShapes() {
        for (Node n : getChildren().getNodes()) {
            if (n instanceof JmeVector3f) {
                ((JmeVector3f) n).attachBox(((JmeVector3f) n).spatial, this);
            }
        }

        updateSpline(false);
    }

    public void disableDebugShapes() {
        for (Node n : getChildren().getNodes()) {
            if (n instanceof JmeVector3f) {
                ((JmeVector3f) n).detachBox(((JmeVector3f) n).spatial);
            }
        }

        if (spatial != null) {
            final Spatial spat = spatial;
            SceneApplication.getApplication().enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    spat.removeFromParent();
                    return null;
                }
            });
        }

    }

    /**
     * Call this to update the visual Spline.
     *
     * @param wasModified If the Spatial was Modified and hence the dirty-safe
     * flag should be triggered (only false for the Constructors first
     * initiation)
     */
    public void updateSpline(boolean wasModified) {
        if (spatial != null) {
            final Spatial spat = spatial;
            SceneApplication.getApplication().enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    spat.removeFromParent();
                    return null;
                }
            });
        }

        Material m = new Material(SceneApplication.getApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", ColorRGBA.Red);
        m.getAdditionalRenderState().setLineWidth(4f);

        switch (motionPath.getPathSplineType()) {
            case CatmullRom:
                Geometry geo = new Geometry("Curve", new Curve(motionPath.getSpline(), 10));
                geo.setMaterial(m);
                spatial = geo;
                break;

            case Linear:
                geo = new Geometry("Curve", new Curve(motionPath.getSpline(), 0));
                geo.setMaterial(m);
                spatial = geo;
                break;

            default:
                geo = new Geometry("Curve", new Curve(motionPath.getSpline(), 10));
                geo.setMaterial(m);
                spatial = geo;
                break;
        }

        final Spatial spat = spatial;
        SceneApplication.getApplication().enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SceneApplication.getApplication().getRootNode().attachChild(spat);
                return null;
            }
        });

        if (wasModified) {
            motionEvent.setModified(true);
        }
    }
}
