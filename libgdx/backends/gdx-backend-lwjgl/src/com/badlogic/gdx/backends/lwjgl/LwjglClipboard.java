package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.utils.Clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

/**
 * Clipboard implementation for desktop that uses the system clipboard via the default AWT {@link Toolkit}.
 */
public class LwjglClipboard implements Clipboard, ClipboardOwner {
    @Override
    public boolean hasContents() {
        String contents = getContents();
        return contents != null && !contents.isEmpty();
    }

    @Override
    public String getContents() {
        try {
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            if (contents != null) {
                if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        return (String) contents.getTransferData(DataFlavor.stringFlavor);
                    } catch (Throwable ex) {
                    }
                }
                if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        List<File> files = (List) contents.getTransferData(DataFlavor.javaFileListFlavor);
                        StringBuilder buffer = new StringBuilder(128);
                        for (int i = 0, n = files.size(); i < n; i++) {
                            if (buffer.length() > 0) buffer.append('\n');
                            buffer.append(files.get(i).toString());
                        }
                        return buffer.toString();
                    } catch (RuntimeException ex) {
                    }
                }
            }
        } catch (Throwable ignored) { // Ignore JDK crashes sorting data flavors.
        }
        return "";
    }

    @Override
    public void setContents(String content) {
        try {
            StringSelection stringSelection = new StringSelection(content);
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        } catch (Throwable ignored) { // Ignore JDK crashes sorting data flavors.
        }
    }

    @Override
    public void lostOwnership(java.awt.datatransfer.Clipboard arg0, Transferable arg1) {
    }
}
