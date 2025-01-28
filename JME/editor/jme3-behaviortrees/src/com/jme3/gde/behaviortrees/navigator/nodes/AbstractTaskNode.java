/*
 *  Copyright (c) 2009-2018 jMonkeyEngine
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
package com.jme3.gde.behaviortrees.navigator.nodes;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.BTreeDataObject;
import com.jme3.gde.behaviortrees.BTreeExporterUtils;
import com.jme3.gde.behaviortrees.navigator.properties.AttrInfoProperty;
import com.jme3.gde.core.editor.icons.Icons;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;

/**
 *
 * @author MeFisto94
 */
public abstract class AbstractTaskNode extends AbstractNode {
    protected Lookup lookup;
    protected Task task;

    public AbstractTaskNode(Task task, Children children, Lookup lookup) {
        super(children, lookup);
        this.lookup = lookup;
        this.task = task;
        /*block.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (evt.getPropertyName().equals(TreeNodePanel.ADD_MAT_PARAM) ||
                    evt.getPropertyName().equals(TreeNodePanel.REMOVE_MAT_PARAM)) {
                setSheet(createSheet());
                firePropertySetsChange(null, null);
            }
        })*/
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{};
    }
      
    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        //Task task = lookup.lookup(Task.class);

        Sheet.Set set = new Sheet.Set();
        set.setName("TaskInfo");
        set.setDisplayName("Task Information");
        set.put(new PropertySupport.ReadOnly<String>("className", String.class, "Class Name", "The Name of the Task") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return task.getClass().getSimpleName();
            }
        });
        
        set.put(new PropertySupport.ReadOnly<String>("package", String.class, "Package", "The Package where the Task resides") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return task.getClass().getPackage().getName();
            }
        });
        
        sheet.put(set);
        
        Sheet.Set metadataSet = new Sheet.Set();
        metadataSet.setName("TaskSpecific");
        metadataSet.setDisplayName("Task specific settings");
        
        BTreeExporterUtils.findMetadata(task.getClass()).values()
            .forEach(a -> metadataSet.put(
                    AttrInfoProperty.createNew(a, task, 
                            lookup.lookup(BTreeDataObject.class)
                    )
            )
        );
        
        sheet.put(metadataSet);
        return sheet;
    }
    
    
    @Override
    public Image getIcon(int type) {
        return Icons.node.getImage();
    }

    @Override
    public Image getOpenedIcon(int type) {
        return Icons.node.getImage();
    }

    public String getKey() {
        return getName();
    }

    /*
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("name")){
            setName((String)evt.getNewValue());
            setDisplayName((String)evt.getNewValue());
        }
    }*/
}
