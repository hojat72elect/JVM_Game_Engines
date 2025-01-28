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
package com.jme3.gde.core.sceneexplorer.nodes.actions;

import com.jme3.gde.core.sceneexplorer.nodes.JmeControl;
import com.jme3.gde.core.sceneexplorer.nodes.JmeSpatial;
import com.jme3.gde.core.util.notify.MessageUtil;
import com.jme3.scene.Node;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.loaders.DataObject;
import org.openide.util.actions.Presenter;

/**
 * This class offers the Popupmenu for all SceneExplorer nodes which allows to start/stop all Controls under this Node.
 * @author MeFisto94
 */
public class ControlsPopup extends AbstractAction implements Presenter.Popup {

    protected JmeSpatial jmeSpatial;
    protected JmeControl jmeControl;
    protected Node node;
    protected DataObject dataObject;
    protected boolean isControl = false;

    public ControlsPopup(JmeSpatial node) {
        this.jmeSpatial = node;
        this.node = node.getLookup().lookup(Node.class);
        this.dataObject = node.getLookup().lookup(DataObject.class);
    }
    
    public ControlsPopup(JmeControl node) {
        this.jmeControl = node;
        this.node = node.getLookup().lookup(Node.class);
        this.dataObject = node.getLookup().lookup(DataObject.class);
        isControl = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu result;
        
        if (isControl) {
            result = new JMenu("Control");
            result.add(new JMenuItem(getStartAction(jmeControl)));
            result.add(new JMenuItem(getStopAction(jmeControl)));
        } else {
            result = new JMenu("Controls");
            result.add(new JMenuItem(getStartAction(jmeSpatial)));
            result.add(new JMenuItem(getStopAction(jmeSpatial)));
        }
        
        return result;
    }
    
    /**
     * Create the Action which will start all subsequent controls to this Spatial.
     * See {@link #getStopAction(com.jme3.gde.core.sceneexplorer.nodes.JmeSpatial) } and {@link #getStartAction(com.jme3.gde.core.sceneexplorer.nodes.JmeControl) }
     * @param Spatial The JmeSpatial to depend this Action on.
     * @return -
     */
    public static Action getStartAction(final JmeSpatial Spatial) {
        return new AbstractAction("Start All") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Spatial != null) {
                    Spatial.setControlsEnabled(true);
                }
            }
        };
    }
    
    /**
     * Create the Action which will start the given Control
     * See {@link #getStopAction(com.jme3.gde.core.sceneexplorer.nodes.JmeControl) } and {@link #getStartAction(com.jme3.gde.core.sceneexplorer.nodes.JmeSpatial) }
     * @param Control The JmeControl to depend this Action on.
     * @return -
     */
    public static Action getStartAction(final JmeControl Control) {
        return new AbstractAction("Start") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Control != null) {
                    if (Control.isEnableable())
                        Control.setEnabled(true);
                    else
                        MessageUtil.warn("Cannot Start this Control!\nStart/Stop only works for Controls which extend AbstractControl.\nImplementing Control isn't enough.");
                }
            }
        };
    }
    
    /**
     * Create the Action which will stop all subsequent controls to Spatial
     * See {@link #getStartAction(com.jme3.gde.core.sceneexplorer.nodes.JmeSpatial)} and {@link #getStopAction(com.jme3.gde.core.sceneexplorer.nodes.JmeControl) }
     * @param Spatial The JmeSpatial to depend this Action on.
     * @return -
     */
    public static Action getStopAction(final JmeSpatial Spatial) {
        return new AbstractAction("Stop All") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Spatial != null) {
                    Spatial.setControlsEnabled(false);
                }
            }
        };
    }
    
    /**
     * Create the Action which will stop the given Control.
     * See {@link #getStopAction(com.jme3.gde.core.sceneexplorer.nodes.JmeSpatial) } and {@link #getStartAction(com.jme3.gde.core.sceneexplorer.nodes.JmeControl) }
     * @param Control The JmeControl to depend this Action on.
     * @return -
     */
    public static Action getStopAction(final JmeControl Control) {
        return new AbstractAction("Stop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Control.isEnableable())
                        Control.setEnabled(false);
                    else
                        MessageUtil.warn("Cannot Stop this Control!\nStart/Stop only works for Controls which extend AbstractControl.\nImplementing Control isn't enough.");
            }
        };
    }
}
