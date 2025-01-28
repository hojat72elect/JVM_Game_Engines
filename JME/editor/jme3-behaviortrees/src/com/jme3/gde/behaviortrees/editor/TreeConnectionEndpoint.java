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
package com.jme3.gde.behaviortrees.editor;

import com.jme3.gde.core.editor.nodes.ConnectionEndpoint;
import com.jme3.gde.core.editor.icons.Icons;

/**
 * This is the input/ouput connector of a ShaderNode.
 * @author MeFisto94
 */
public class TreeConnectionEndpoint extends ConnectionEndpoint {
    
    @Override
    public boolean canConnect(ConnectionEndpoint pair) {
        // Feature: The Direction is clear here so we can drag them from both sides.
        // Note to myself: Connections are always formed FROM output TO Input
        if (pair != null) {
            if (paramType == ParamType.Input && 
                    (pair.getParamType() == ParamType.Output ||
                        pair.getParamType() == ParamType.Both)) {
                return pair.canConnect(this); // Invert, always call it from Output to Input
            }
        }
        
        // cannot connect to: nothing || from input panels || to output
        if (pair == null || getParamType() == ParamType.Input ||
                pair.getParamType() == ParamType.Output) {
            setIcon(Icons.imgOrange);
            return false;
        } else if (allowConnection(pair)) {
            setIcon(Icons.imgGreen);
            return true;
        } else {
            setIcon(Icons.imgRed);
            return false;
        }
    }
    
    @Override
    protected boolean allowConnection(ConnectionEndpoint pair) {
        return !isConnected();
        // @TODO: Prevent circular connection, that means if pair.getNode()
        // appears somewhere in "getConnectionsTo(this).getParent().getParent()...."
        //@TODO: Another feature is swapping: just exchange the froms of both connections
        // maybe with a confirmation dialog? and or a setting? Or Shift?
    }
    
    /**
     * This method checks if any of the both strings contain the same substring
     * seperated by \|
     * @param type1 The Typelist 1
     * @param type2 The Typelist 2
     * @return whether they contain the same substring
     */
    private boolean matches(String type1, String type2) {
        String[] s1 = type1.split("\\|");
        String[] s2 = type2.split("\\|");
        for (String string : s1) {
            for (String string1 : s2) {
                if (string.equals(string1)) {
                    return true;
                }
            }
        }
        return false;
    }
}
