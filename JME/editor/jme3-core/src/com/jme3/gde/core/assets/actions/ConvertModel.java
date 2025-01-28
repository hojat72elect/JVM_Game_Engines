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
package com.jme3.gde.core.assets.actions;

import com.jme3.export.Savable;
import com.jme3.gde.core.assets.BinaryModelDataObject;
import com.jme3.gde.core.assets.SpatialAssetDataObject;
import com.jme3.gde.core.util.notify.MessageUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;

public final class ConvertModel implements ActionListener {
    protected static final Logger logger = Logger.getLogger(ConvertModel.class.getName());
    private final List<SpatialAssetDataObject> context;

    public ConvertModel(List<SpatialAssetDataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                ProgressHandle progressHandle = ProgressHandle.createHandle("Converting Model");
                progressHandle.start();
                for (SpatialAssetDataObject spatialAssetDataObject : context) {
                    if (!(spatialAssetDataObject instanceof BinaryModelDataObject)) {
                        try {
                            Savable sav = spatialAssetDataObject.loadAsset();
                            if (sav != null) {
                                spatialAssetDataObject.saveAsset();
                                spatialAssetDataObject.closeAsset();
                            }
                        } catch (Exception ex) {
                            //Exceptions.printStackTrace(ex); // does only print the stacktrace when launching the sdk in debug mode
                            //bad for user reports, so we use error() and loggers
                            MessageUtil.error("Unable to convert the model: An Exception has occured.\n"
                                    + "Please look into the Output Window and report that Exception\n"
                                    + "(including the full stacktrace) to us at \n"
                                    + "https://github.com/jMonkeyEngine/sdk/issues");
                            logger.log(Level.SEVERE, "An Exception has occured.", ex);
                        }
                    }
                }
                progressHandle.finish();
            }
        };
        new Thread(run).start();
    }
}
