/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.core.dnd;

import com.jme3.gde.core.sceneviewer.SceneViewerTopComponent;
import com.jme3.math.Vector2f;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

/**
 *
 * @author rickard
 */
public class MaterialDropTargetListener implements DropTargetListener {

    private static final Cursor droppableCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private static final Cursor notDroppableCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    private final SceneViewerTopComponent rootPanel;

    public MaterialDropTargetListener(SceneViewerTopComponent rootPanel) {
        this.rootPanel = rootPanel;
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        if (!this.rootPanel.getCursor().equals(droppableCursor)) {
            this.rootPanel.setCursor(droppableCursor);
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        this.rootPanel.setCursor(notDroppableCursor);
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        this.rootPanel.setCursor(Cursor.getDefaultCursor());

        Object transferableObj = null;
        try {
            final DataFlavor dragAndDropPanelFlavor = new MaterialDataFlavor();

            final Transferable transferable = dtde.getTransferable();

            if (transferable.isDataFlavorSupported(dragAndDropPanelFlavor)) {
                transferableObj = dtde.getTransferable().getTransferData(dragAndDropPanelFlavor);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (transferableObj == null) {
            return;
        }
        final int dropYLoc = dtde.getLocation().y;
        final int dropXLoc = dtde.getLocation().x;

        rootPanel.applyMaterial(((AssetNameHolder) transferableObj).getAssetName(), new Vector2f(dropXLoc, dropYLoc));
    }

}
