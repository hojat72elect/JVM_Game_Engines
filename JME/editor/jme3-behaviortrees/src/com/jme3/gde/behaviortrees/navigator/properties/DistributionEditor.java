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
package com.jme3.gde.behaviortrees.navigator.properties;

import com.badlogic.gdx.ai.utils.random.Distribution;
import com.jme3.gde.behaviortrees.DistributionStringConverter;
import java.awt.Component;
import java.beans.PropertyEditorSupport;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author MeFisto94
 */
public class DistributionEditor extends PropertyEditorSupport implements ExPropertyEditor {
    PropertyEnv env;
    AttrInfoProperty<Distribution> prop;
    DistributionProperties uiPanel;
    
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        /* It's important to pass prop, because attachEnv might be called multiple times
         * for different envs. See  http://bits.netbeans.org/7.4/javadoc/org-openide-explorer/org/openide/explorer/propertysheet/ExPropertyEditor.html#attachEnv(org.openide.explorer.propertysheet.PropertyEnv)
         */
        if (uiPanel == null) {
            uiPanel = new DistributionProperties(env, prop, this);
        }
        
        return uiPanel;
    }

    @Override
    public String getAsText() {
        return DistributionStringConverter.fromDistribution((Distribution)getValue());
    }

    @Override
    public void attachEnv(PropertyEnv pe) {
        this.env = pe;
        prop = (AttrInfoProperty<Distribution>)pe.getFeatureDescriptor();
    }

    @Override
    public Object getValue() {
        if (uiPanel == null) {
            return super.getValue();
        }
        
        Distribution dist = uiPanel.toDistribution();
        return dist;
    }
    
    //@TODO: maybe change setValue to update the property, or does that happen automagically?
}
