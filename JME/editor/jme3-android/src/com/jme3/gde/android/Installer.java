/*
 *  Copyright (c) 2009-2019 jMonkeyEngine
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
package com.jme3.gde.android;

import com.jme3.gde.core.errorreport.ExceptionUtils;
import java.net.MalformedURLException;
import java.net.URI;
import org.netbeans.api.autoupdate.UpdateUnitProvider;
import org.netbeans.api.autoupdate.UpdateUnitProviderFactory;
import org.openide.modules.ModuleInstall;
import org.openide.modules.Modules;

/**
 * The Installer is responsible for adding the correct version of NBAndroidV2 
 * to the Update Center. It uses the exact same mechanism as the
 * NBAndroidUpdatePlugin
 * 
 * @see https://github.com/NBANDROIDTEAM/NBANDROID-V2-Autoupdate-plugin/blob/master/src/main/java/org/netbeans/modules/android/update/loader/Installer.java
 * @author MeFisto94
 */
public class Installer extends ModuleInstall {
    
    @Override
    public void restored() {
            String implVers = Modules.getDefault().findCodeNameBase("org.netbeans.modules.projectui").getImplementationVersion();
            if (implVers != null) {
                try {
                    UpdateUnitProvider updateUnitProvider = UpdateUnitProviderFactory.getDefault()
                                .create("NBANDROID", "NBANDROID Update Center", URI.create(
                                        String.format("http://server.arsi.sk:8080/updates/%s-updates.xml", implVers)
                                ).toURL());
                    updateUnitProvider.setEnable(true);
                } catch (MalformedURLException ex) {
                    ExceptionUtils.caughtException(ex, "Note: This could be a problem related to your internet connection/firewall etc.");
                }
            }
    }
}
