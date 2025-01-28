/*
 *  Copyright (c) 2009-2020 jMonkeyEngine
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
package com.jme3.gde.core.sceneexplorer.nodes.actions.animation;

import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.gde.core.scene.SceneApplication;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Exceptions;

/**
 * The property implementation for {@link AnimClip}s, so those can be referred
 * to by their name in property editors.
 * 
 * @author MeFisto94
 */

public class AnimClipProperty extends PropertySupport.ReadWrite<String> {
    private final AnimComposer composer;
    private String anim = "null";
    /*private Map<String, Object> layers;
    private Object layer;
    private Field currentActionField;*/

    public AnimClipProperty(AnimComposer composer) {
        super("AnimationClip", String.class, "Animation Clip", "");
        this.composer = composer;
        
        /*try {
            Field layersField = AnimComposer.class.getDeclaredField("layers");
            layers = (Map<String, Object>)layersField.get(composer);
            layer = layers.get(AnimComposer.DEFAULT_LAYER);
            // @TODO: currentActionField, in order to properly read the current anim
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }*/
    }
    
    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        return anim;
        //@TODO: Read the currently active anim instead of using the cached String, as other ways than this property might set the anim
    }

    @Override
    public void setValue(String t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (composer != null) {
            try {
                SceneApplication.getApplication().enqueue(() -> {
                    anim = t;
                    
                    if ("null".equals(t) || t.isBlank()) {
                        composer.removeCurrentAction(AnimComposer.DEFAULT_LAYER);
                    } else {
                        composer.setCurrentAction(t);
                    }
                    return null;
                }).get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
