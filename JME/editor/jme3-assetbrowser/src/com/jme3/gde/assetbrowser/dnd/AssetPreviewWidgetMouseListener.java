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
package com.jme3.gde.assetbrowser.dnd;

import com.jme3.gde.assetbrowser.widgets.AssetPreviewWidget;
import com.jme3.gde.assetbrowser.widgets.PreviewInteractionListener;
import java.awt.event.MouseAdapter;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

/**
 * For handling drag and drop of assets.
 *
 * @author rickard
 */
public final class AssetPreviewWidgetMouseListener extends MouseAdapter {

    private final AssetPreviewWidget previewWidget;
    private final PreviewInteractionListener listener;
    private boolean pressed, moved;

    public AssetPreviewWidgetMouseListener(AssetPreviewWidget previewWidget, PreviewInteractionListener listener) {
        this.previewWidget = previewWidget;
        this.listener = listener;
    }

    @Override
    public void mouseClicked(final java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            evt.consume();
            if (previewWidget.isEditable()) {
                listener.openAsset(previewWidget);
            } else {
                JOptionPane.showMessageDialog(null, "Project dependencies can't be edited");
            }
        }
    }

    @Override
    public void mousePressed(final java.awt.event.MouseEvent evt) {
        pressed = true;

    }

    @Override
    public void mouseReleased(final java.awt.event.MouseEvent evt) {
        pressed = false;
        moved = false;
    }

    @Override
    public void mouseMoved(final java.awt.event.MouseEvent evt) {
    }

    @Override
    public void mouseDragged(final java.awt.event.MouseEvent evt) {
        if (pressed) {
            moved = true;
            TransferHandler handler = previewWidget.getTransferHandler();
            if (handler != null) {
                handler.exportAsDrag(previewWidget, evt, TransferHandler.COPY);
            }
        }
    }
}
