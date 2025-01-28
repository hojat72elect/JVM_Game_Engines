/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.autodesk;

import com.jme3.gde.core.assets.SpatialAssetDataObject;
import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.NbBundle;

@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_AutodeskFBX_loader_name",
        mimeType = "application/octet-stream",
        extension = {"fbx", "FBX"}
)
@DataObject.Registration(displayName = "Autodesk FBX", mimeType = "application/octet-stream", iconBase="com/jme3/gde/autodesk/autodesk.svg")
@ActionReferences(value = {
    @ActionReference(id =
    @ActionID(category = "jMonkeyPlatform", id = "com.jme3.gde.core.assets.actions.ConvertModel"), path = "Loaders/application/octet-stream/Actions", position = 10),
    @ActionReference(id =
    @ActionID(category = "jMonkeyPlatform", id = "com.jme3.gde.core.assets.actions.OpenModel"), path = "Loaders/application/octet-stream/Actions", position = 20),
    @ActionReference(id =
    @ActionID(category = "Edit", id = "org.openide.actions.CutAction"), path = "Loaders/application/octet-stream/Actions", position = 200, separatorBefore = 100),
    @ActionReference(id =
    @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"), path = "Loaders/application/octet-stream/Actions", position = 300, separatorAfter = 400),
    @ActionReference(id =
    @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"), path = "Loaders/application/octet-stream/Actions", position = 500),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.RenameAction"), path = "Loaders/application/octet-stream/Actions", position = 600, separatorAfter = 700),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"), path = "Loaders/application/octet-stream/Actions", position = 800, separatorAfter = 900),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"), path = "Loaders/application/octet-stream/Actions", position = 1000, separatorAfter = 1100),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.ToolsAction"), path = "Loaders/application/octet-stream/Actions", position = 1200),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"), path = "Loaders/application/octet-stream/Actions", position = 1300)
})
@NbBundle.Messages("LBL_AutodeskFBX_loader_name=Autodesk FBX Files")
public class AutodeskFBXDataObject extends SpatialAssetDataObject {

    public AutodeskFBXDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
    }

}
