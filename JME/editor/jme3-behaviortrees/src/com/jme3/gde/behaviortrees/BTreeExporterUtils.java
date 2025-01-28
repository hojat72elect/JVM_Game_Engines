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
package com.jme3.gde.behaviortrees;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.btree.annotation.TaskConstraint;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;

/**
 * This class contains code which can be used by both: BTreeExporter and some
 * other spots in the SDK.
 * @author MeFisto94
 */
public class BTreeExporterUtils {
    public static ObjectMap<String, AttrInfo> findMetadata (Class<?> clazz) {
        Annotation tca = ClassReflection.getAnnotation(clazz, TaskConstraint.class);
        if (tca != null) {
            ObjectMap<String, AttrInfo> taskAttributes = new ObjectMap<>();
            Field[] fields = ClassReflection.getFields(clazz);
            for (Field f : fields) {
                Annotation a = f.getDeclaredAnnotation(TaskAttribute.class);
                if (a != null) {
                    AttrInfo ai = new AttrInfo(f.getName(), a.getAnnotation(TaskAttribute.class), f);
                    taskAttributes.put(ai.name, ai);
                }
            }
            return taskAttributes;
        } else {
            return null;
        }
    }
    
    public static class AttrInfo {
        public String name;
        public String fieldName;
        public boolean required;
        public Field f;

        AttrInfo (String fieldName, TaskAttribute annotation, Field f) {
            this(annotation.name(), fieldName, annotation.required(), f);
        }

        AttrInfo (String name, String fieldName, boolean required, Field f) {
            this.name = name == null || name.length() == 0 ? fieldName : name;
            this.fieldName = fieldName;
            this.required = required;
            this.f = f;
        }
    }
}
