/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.materials;

import com.jme3.gde.materials.multiview.MaterialEditorTopComponent;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.OpenSupport;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author rickard
 */
public class MaterialPreviewOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    public MaterialPreviewOpenSupport(JMEMaterialDataObject.Entry entry) {
        super(entry);
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        JMEMaterialDataObject dobj = (JMEMaterialDataObject) entry.getDataObject();
        MaterialEditorTopComponent tc = new MaterialEditorTopComponent(dobj);
        return tc;
    }
}
