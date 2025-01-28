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

import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.openide.loaders.DataObject;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * This Class is responsible for the management of all underlying Vector3fs as
 * Nodes<br>
 * (In fact we currently only have one possible MotionPath but we keep this to
 * be consistent with JmeBoneChildren).<br>
 * You have to ensure that you set the appropriate JmeMotionEvent for this class
 * (this happens when JmeMotionEvent is creating it's Nodes)<br>
 * It will use this class as Children (which are JmeMotionPaths)<br>
 *
 * @author MeFisto94
 */
public class JmeVector3fChildren extends Index.ArrayChildren {

    protected JmeMotionPath jmeMotionPath;
    protected boolean readOnly = true;
    private DataObject dataObject;

    public JmeVector3fChildren() {
    }

    public JmeVector3fChildren(JmeMotionPath jmeMotionPath) {
        this.jmeMotionPath = jmeMotionPath;
    }

    public void refreshChildren(boolean immediate) {
        refresh();
        for (Node n : getNodes()) {
            n.setDisplayName("Waypoint " + indexOf(n));
        }
    }

    public void setReadOnly(boolean cookie) {
        this.readOnly = cookie;
    }

    protected List<Vector3f> createKeys() {
        try {
            return SceneApplication.getApplication().enqueue(new Callable<List<Vector3f>>() {

                @Override
                public List<Vector3f> call() throws Exception {
                    List<Vector3f> keys = new LinkedList<Vector3f>();
                    if (jmeMotionPath.getMotionPath() != null) {
                        for (int i = 0; i < jmeMotionPath.getMotionPath().getNbWayPoints(); i++) {
                            keys.add(jmeMotionPath.getMotionPath().getWayPoint(i));
                        }
                    }

                    return keys;
                }
            }).get();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    /**
     * This is called when Index.ArrayChildren initiates it's list. It's similar
     * to createNodes and createKeys.
     *
     * @return
     */
    @Override
    protected List<Node> initCollection() {
        List<Vector3f> keyList = createKeys();
        ArrayList<Node> nodeList = new ArrayList<Node>(keyList.size());
        int i = 0;

        for (Vector3f v : keyList) {
            Node n = new JmeVector3f(this, jmeMotionPath, v).setReadOnly(readOnly);
            n.setDisplayName("Waypoint " + i);
            nodeList.add(n);
            i++;
        }

        return nodeList;
    }

    @Override
    public boolean remove(Node[] arr) {
        for (Node n : arr) {
            jmeMotionPath.getMotionPath().removeWayPoint(((JmeVector3f) n).getVector3f());
        }

        return super.remove(arr);
    }

    @Override
    public boolean add(Node[] arr) {
        for (Node n : arr) {
            //DON'T USE THIS -> jmeMotionPath.getMotionPath().addWayPoint(v);
            //It will clone v and as such we're not able to change it anymore.
            //jmeMotionPath.getMotionPath().getSpline().getControlPoints().add(((JmeVector3f)n).getVector3f());
            addControlPoint(jmeMotionPath.getMotionPath().getSpline(), ((JmeVector3f) n).getVector3f());
        }

        return super.add(arr);
    }

    /* Code taken from Spline.java, removed the .clone() part */
    private void addControlPoint(Spline spline, Vector3f controlPoint) {
        List<Vector3f> controlPoints = spline.getControlPoints();
        if (controlPoints.size() > 2 && spline.isCycle()) {
            controlPoints.remove(controlPoints.size() - 1);
        }
        controlPoints.add(controlPoint);
        if (controlPoints.size() >= 2 && spline.isCycle()) {
            controlPoints.add(controlPoints.get(0).clone());
        }
        if (controlPoints.size() > 1) {
            spline.setCurveTension(spline.getCurveTension()); // this calls spline.computeTotalLength();
        }
    }

    /**
     * This Method is used because before createNodes takes place we are called
     * by the default constructor and have to pass things...
     *
     * @param jmeMotionPath The JmeMotionPath instance which is this nodes
     * parent.
     */
    public void setJmeMotionPath(JmeMotionPath jmeMotionPath) {
        this.jmeMotionPath = jmeMotionPath;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    /**
     * Basically we should override everything from Index.ArrayChildren, but
     * it's no fun and since we don't support dragging them (which could need
     * exchange() we just stick with this way.
     */
    /**
     * Move an element up
     *
     * @param i index of element to move up
     */
    @Override
    public void moveUp(int i) {
        super.moveUp(i);

        List<Vector3f> controlPoints = jmeMotionPath.getMotionPath().getSpline().getControlPoints();

        if (i < 1 || i > controlPoints.size()) {
            throw new IndexOutOfBoundsException();
        }

        Vector3f element = controlPoints.get(i);
        Vector3f over_element = controlPoints.get(i - 1);

        controlPoints.set(i - 1, element);
        controlPoints.set(i, over_element);

        jmeMotionPath.getMotionPath().getSpline().setType(jmeMotionPath.getMotionPath().getSpline().getType()); // retrigger some internal computations

        refreshChildren(true);
        jmeMotionPath.updateSpline(true);
    }

    /**
     * Move an element down
     *
     * @param i index of element to move down
     */
    @Override
    public void moveDown(int i) {
        super.moveDown(i);
        List<Vector3f> controlPoints = jmeMotionPath.getMotionPath().getSpline().getControlPoints();

        if (i < 0 || i >= controlPoints.size()) {
            throw new IndexOutOfBoundsException();
        }

        Vector3f element = controlPoints.get(i);
        Vector3f under_element = controlPoints.get(i + 1);

        controlPoints.set(i + 1, element);
        controlPoints.set(i, under_element);

        jmeMotionPath.getMotionPath().getSpline().setType(jmeMotionPath.getMotionPath().getSpline().getType()); // retrigger some internal computations

        refreshChildren(true);
        jmeMotionPath.updateSpline(true);
    }
}
