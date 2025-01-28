/*
 *  Copyright (c) 2016 jMonkeyEngine
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
package org.jme3.netbeans.plaf.darkmonkey;

import java.awt.Color;
import java.util.prefs.Preferences;
import org.netbeans.swing.plaf.LFCustoms;
import org.openide.util.NbPreferences;

/**
 * This class is responsible for the customization of many details (such as the color of the diff tool) which aren't
 * possible to define by the LaF. As such you can change the Font and Color for each detail.
 * @author MeFisto94
 */
public class DarkMonkeyLFCustoms extends LFCustoms {
    
    DarkMonkeyLookAndFeel LaF;
    
    public DarkMonkeyLFCustoms(DarkMonkeyLookAndFeel LaF) {
        this.LaF = LaF;
    }
    @Override
    public Object[] createLookAndFeelCustomizationKeysAndValues() {
        // Fonts belong here it seems
        Preferences pref = NbPreferences.root().node("laf");
        
        Object[] result = {
        
        };
        
        return result;
    }
    
    @Override
    public Object[] createApplicationSpecificKeysAndValues() {
        Object[] result = {
            // git
            "nb.versioning.added.color", Color.decode(LaF.getThemeProperty("nb.versioning.added.color", LaF.getDefaultThemeProperty("nb.versioning.added.color", "#FF0000"))), // Red to symbolize that there is an error
            "nb.versioning.modified.color", Color.decode(LaF.getThemeProperty("nb.versioning.modified.color", LaF.getDefaultThemeProperty("nb.versioning.modified.color", "#FF0000"))),
            "nb.versioning.deleted.color", Color.decode(LaF.getThemeProperty("nb.versioning.deleted.color", LaF.getDefaultThemeProperty("nb.versioning.deleted.color", "#FF0000"))),
            "nb.versioning.conflicted.color", Color.decode(LaF.getThemeProperty("nb.versioning.conflicted.color", LaF.getDefaultThemeProperty("nb.versioning.conflicted.color", "#FF0000"))),
            "nb.versioning.ignored.color", Color.decode(LaF.getThemeProperty("nb.versioning.ignored.color", LaF.getDefaultThemeProperty("nb.versioning.ignored.color", "#FF0000"))),
            
            // diff
            "nb.diff.added.color", Color.decode(LaF.getThemeProperty("nb.diff.added.color", LaF.getDefaultThemeProperty("nb.diff.added.color", "#FF0000"))),
            "nb.diff.changed.color", Color.decode(LaF.getThemeProperty("nb.diff.changed.color", LaF.getDefaultThemeProperty("nb.diff.changed.color", "#FF0000"))),
            "nb.diff.deleted.color", Color.decode(LaF.getThemeProperty("nb.diff.deleted.color", LaF.getDefaultThemeProperty("nb.diff.deleted.color", "#FF0000"))),
            "nb.diff.applied.color", Color.decode(LaF.getThemeProperty("nb.diff.applied.color", LaF.getDefaultThemeProperty("nb.diff.applied.color", "#FF0000"))),
            "nb.diff.notapplied.color", Color.decode(LaF.getThemeProperty("nb.diff.notapplied.color", LaF.getDefaultThemeProperty("nb.diff.notapplied.color", "#FF0000"))),
            "nb.diff.unresolved.color", Color.decode(LaF.getThemeProperty("nb.diff.unresolved.color", LaF.getDefaultThemeProperty("nb.diff.unresolved.color", "#FF0000"))),
            "nb.diff.sidebar.deleted.color", Color.decode(LaF.getThemeProperty("nb.diff.sidebar.deleted.color", LaF.getDefaultThemeProperty("nb.diff.sidebar.deleted.color", "#FF0000"))),
            "nb.diff.sidebar.changed.color", Color.decode(LaF.getThemeProperty("nb.diff.sidebar.changed.color", LaF.getDefaultThemeProperty("nb.diff.sidebar.changed.color", "#FF0000")))
        };
        
        return result;
    }
}