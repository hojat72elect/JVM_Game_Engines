/*
 *  Copyright (c) 2009-2010 jMonkeyEngine
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
package com.jme3.gde.core.sceneexplorer.nodes.actions.impl;

import com.jme3.math.Vector3f;

/**
 * Utility Class to Convert a Vector3f from and to Strings.<br>
 * This is useful for Properties etc.
 * 
 * @author david.bernard.31
 * @author MeFisto94
 */

public class ConverterVector3f_String {
    
    public static void parseInto(String text, Vector3f res) throws IllegalArgumentException {
        text = text.replace('[', ' ');
        text = text.replace('(', ' ');
        text = text.replace(')', ' ');
        text = text.replace(']', ' ').trim();
        String[] a = text.split("\\s*(,|\\s)\\s*");

        if (a.length == 1) {
            if(text.trim().toLowerCase().equals("nan")) {
                res.set(Vector3f.NAN);
                return;
            }
            float f = Float.parseFloat(text);           
            res.set(f, f, f);
            return;
        }

        if (a.length == 3) {
            res.set(Float.parseFloat(a[0]), Float.parseFloat(a[1]), Float.parseFloat(a[2]));
            return;
        }
        throw new IllegalArgumentException("String not correct");
    }
    
    public static String Vector3fToString(Vector3f vector) {
        return "[" + vector.x + ", " + vector.y + ", " + vector.z + "]";
    }

    public static Vector3f StringToVector3f(String text) throws IllegalArgumentException {
        Vector3f vector = new Vector3f();
        parseInto(text, vector);
        return vector;
    }
}
