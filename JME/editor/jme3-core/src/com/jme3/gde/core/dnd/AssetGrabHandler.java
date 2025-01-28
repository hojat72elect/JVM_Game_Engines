/*
 *  Copyright (c) 2009-2023 jMonkeyEngine
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
package com.jme3.gde.core.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

/**
 * Based on:
 * https://stackoverflow.com/questions/23225958/dragging-between-two-components-in-swing
 *
 * @author rickard
 * @param <T>
 */
public class AssetGrabHandler<T extends DataFlavor> extends TransferHandler {

    private static final long serialVersionUID = 1L;
    private final DataFlavor flavor;
    private final AssetNameHolder origin;

    public AssetGrabHandler(AssetNameHolder origin, T flavor) {
        this.origin = origin;
        this.flavor = flavor;
    }

    @Override
    public boolean canImport(TransferSupport info) {
        return info.isDataFlavorSupported(flavor);
    }

    @Override
    public boolean importData(TransferSupport transferSupport) {
        final Transferable t = transferSupport.getTransferable();
        try {
            return t.getTransferData(flavor) != null;
        } catch (UnsupportedFlavorException | IOException e) {
            Logger.getLogger(AssetGrabHandler.class.getName()).log(Level.WARNING, "Non-supported flavor {0}", t);
        }
        return false;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    @Override
    public Transferable createTransferable(JComponent source) {
        // We need the values from the list as an object array, otherwise the data flavor won't match in importData
        return new AssetTransferable(origin, flavor);
    }

}
