/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.assetbrowser.widgets;

import com.jme3.gde.core.dnd.AssetGrabHandler;
import com.jme3.gde.core.dnd.TextureDataFlavor;
import javax.swing.Icon;

/**
 *
 * @author rickard
 */
public class TexturePreview extends AssetPreviewWidget {

    public TexturePreview(PreviewInteractionListener listener, Icon icon) {
        super(listener, icon);
        setTransferHandler(new AssetGrabHandler(this, new TextureDataFlavor()));
    }

}
