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

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.utils.random.Distribution;
import com.jme3.gde.behaviortrees.BTreeDataObject;
import com.jme3.gde.behaviortrees.BTreeExporterUtils.AttrInfo;
import com.jme3.gde.core.errorreport.ExceptionUtils;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import org.openide.nodes.Node;

/**
 * This property handles parsing the AttribInfo and add a correct instance of 
 * the Property.
 * 
 * @author MeFisto94
 */
public class AttrInfoProperty<T> extends Node.Property<T> {
    Class<? extends PropertyEditor> propertyEditorClass;
    T instance;
    AttrInfo aInfo;
    BTreeDataObject obj;
    
    public AttrInfoProperty(T instance, AttrInfo aInfo, BTreeDataObject obj) {
        super(aInfo.f.getType());
        this.instance = instance;
        this.aInfo = aInfo;
        this.obj = obj;
        setName(aInfo.fieldName);
        setDisplayName((aInfo.required? "[!] " : "") + aInfo.name);
        if (aInfo.required) {
            setShortDescription("This is a mandatory attribute for the task");
        }
    }
    
    @Override
    public PropertyEditor getPropertyEditor() {
        if (propertyEditorClass != null) {
            try {
                return propertyEditorClass.newInstance();
            } catch (Exception ex) {
                ExceptionUtils.caughtException(ex, aInfo.toString());
            }
        }

        return super.getPropertyEditor();
    }

    public void setPropertyEditorClass(Class<? extends PropertyEditor> clazz) {
        propertyEditorClass = clazz;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public T getValue() throws IllegalAccessException, InvocationTargetException {
        try {
            aInfo.f.setAccessible(true);
            Object obj = aInfo.f.get(instance);
            
            if (obj != null) {
                return (T)obj;
            }
        } catch (Exception ex) {
            ExceptionUtils.caughtException(ex);
        }
        
        return null;
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public void setValue(T t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            aInfo.f.setAccessible(true);
            Object o = aInfo.f.get(instance);
            aInfo.f.set(instance, t);
            
            if (o != null && t != null) {
                if (!Objects.equals(o, t)) {
                    obj.setDirty();
                }
            }
        } catch (Exception e) {
            ExceptionUtils.caughtException(e);
        }
    }
    
    public static AttrInfoProperty createNew(AttrInfo a, Task instance, 
            BTreeDataObject obj) {
        Class c = a.f.getType();
        if (Distribution.class.isAssignableFrom(c)) {
            //return new DistributionProperty(a, instance);
            AttrInfoProperty ai = new AttrInfoProperty<>(instance, a, obj);
            ai.setPropertyEditorClass(DistributionEditor.class);
            return ai;
        } else {
            /*if (Float.class.isAssignableFrom(c)) {
                return new AttrInfoProperty<>((Float)instance, a);
                //ai.setPropertyEditorClass(PropertyEditorSupport.class);
            } else if (Double.class.isAssignableFrom(c)) {
                return new AttrInfoProperty<>((Double)instance, a);
                //ai.setPropertyEditorClass(PropertyEditorSupport.class);
            } else if (Integer.class.isAssignableFrom(c)) {
                return new AttrInfoProperty<>((Integer)instance, a);
                //ai.setPropertyEditorClass(PropertyEditorSupport.class);
            } else if (Character.class.isAssignableFrom(c)) {
                return new AttrInfoProperty<>((Character)instance, a);
            } else if (String.class.isAssignableFrom(c)) {
                return new AttrInfoProperty<>((String)instance, a);
            } else {*/
                // Unknown Property
                return new AttrInfoProperty<>(instance, a, obj);
            //}
        }
    }
}
