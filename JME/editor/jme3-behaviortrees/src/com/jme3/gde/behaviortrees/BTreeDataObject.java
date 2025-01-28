/*
 * Copyright (c) 2009-2018 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.behaviortrees;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.jme3.gde.behaviortrees.editor.BTreeNodeEditorElement;
import com.jme3.gde.behaviortrees.nodes.impl.RootNodePanel;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.errorreport.ExceptionUtils;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JEditorPane;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

@MIMEResolver.ExtensionRegistration(
    displayName = "LibGDX AI Behavior Tree",
mimeType = "text/gdx-ai-btree",
extension = {"behaviortree", "btree"})
@DataObject.Registration(
    mimeType = "text/gdx-ai-btree",
iconBase = "com/jme3/gde/core/editor/icons/matdef.png",
displayName = "LibGDX AI Behavior Tree",
position = 300)
@ActionReferences({
    @ActionReference(
        path = "Loaders/text/gdx-ai-btree/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
    position = 100,
    separatorAfter = 200),    
    @ActionReference(
        path = "Loaders/text/gdx-ai-btree/Actions",
    id =
    @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
    position = 300),
    @ActionReference(
        path = "Loaders/text/gdx-ai-btree/Actions",
    id =
    @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
    position = 400,
    separatorAfter = 500),
    @ActionReference(
        path = "Loaders/text/gdx-ai-btree/Actions",
    id =
    @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
    position = 600),
    @ActionReference(
        path = "Loaders/text/gdx-ai-btree/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
    position = 700,
    separatorAfter = 800),
    @ActionReference(
        path = "Loaders/text/gdx-ai-btree/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
    position = 900,
    separatorAfter = 1000),
    @ActionReference(
        path = "Loaders/text/gdx-ai-btree/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
    position = 1100,
    separatorAfter = 1200),
    @ActionReference(
        path = "Loaders/text/gdx-ai-btree/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
    position = 1300),
    @ActionReference(
        path = "Loaders/text/gdx-ai-btree/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
    position = 1400)
})
@Messages("LBL_BTREE_LOADER=LibGDX AI Behavior Tree")
public class BTreeDataObject extends MultiDataObject {
    protected final Lookup lookup;
    protected final InstanceContent lookupContents = new InstanceContent();
    private boolean loaded = false;
    protected BehaviorTree mainTree;
    protected RootNodePanel rootNodePanel;
    protected final BTreeParser<Object> parser;
    protected ProjectAssetManager manager;
    protected BTreeNodeEditorElement topComponent;

    @SuppressWarnings("LeakingThisInConstructor")
    public BTreeDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/gdx-ai-btree", true);
        manager = findAssetManager();
        lookup = new ProxyLookup(getCookieSet().getLookup(), new AbstractLookup(lookupContents));
        final BTreeMetaData metaData = new BTreeMetaData(this);
        lookupContents.add(metaData);
        lookupContents.add(this);
        pf.addFileChangeListener(new FileChangeAdapter() {
            @Override
            public void fileChanged(FileEvent fe) {
                super.fileChanged(fe);
                metaData.save();
                /*if (file.isDirty()) {
                    file.setLoaded(false);
                    file.setDirty(false);
                }*/
            }
        });
        parser = new BTreeParser<>(this, BTreeParser.DEBUG_HIGH);
        CookieSet cookies = getCookieSet();
        // cookies.add(new Opener());
    }

    public BTreeNodeEditorElement getTopComponent() {
        return topComponent;
    }

    public void setTopComponent(BTreeNodeEditorElement topComponent) {
        this.topComponent = topComponent;
    }
    
    private ProjectAssetManager findAssetManager() {
        FileObject file = getPrimaryFile();
        ProjectManager pm = ProjectManager.getDefault();
        while (file != null) {
            if (file.isFolder() && pm.isProject(file)) {
                try {
                    Project project = ProjectManager.getDefault().findProject(file);
                    if (project != null) {
                        ProjectAssetManager mgr = project.getLookup().lookup(ProjectAssetManager.class);
                        if (mgr != null) {
                            getLookupContents().add(mgr);
                            return mgr;
                        }
                    }
                } catch (IOException ex) {
                } catch (IllegalArgumentException ex) {
                }
            }
            file = file.getParent();
        }
        return null;
    }

    public ProjectAssetManager getManager() {
        return manager;
    }
    
    @Override
    protected int associateLookup() {
        return 1;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    public BehaviorTree getMainTree() {
        return mainTree;
    }

    public RootNodePanel getRootNodePanel() {
        return rootNodePanel;
    }

    public void setRootNodePanel(RootNodePanel rootNodePanel) {
        this.rootNodePanel = rootNodePanel;
    }

    public BTreeParser<Object> getParser() {
        return parser;
    }
       
    @MultiViewElement.Registration(
    displayName = "#LBL_BTREE_EDITOR",
    iconBase = "com/jme3/gde/core/editor/icons/vert.png",
    mimeType = "text/gdx-ai-btree",
    persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
    preferredID = "BehaviorTree",
    position = 1000)
    @Messages("LBL_BTREE_EDITOR=Code")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        final BTreeDataObject obj = lkp.lookup(BTreeDataObject.class);
        obj.refresh();
        
        // Open a regular text editor element
        MultiViewEditorElement ed = new MultiViewEditorElement(lkp) {
            // Mark as Modified when something has been typed
            KeyListener listener = new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    obj.setModified(true);
                }

                @Override
                public void keyReleased(KeyEvent e) {                   
                }
            };

            @Override
            public void componentActivated() {
                super.componentActivated();
                getEditorPane().addKeyListener(listener);                
            }

            @Override
            public void componentDeactivated() {
                super.componentDeactivated();
                JEditorPane editorPane = getEditorPane();
                if (editorPane != null) {
                    getEditorPane().removeKeyListener(listener);
                }
            }

            @Override
            public void componentClosed() {
                super.componentClosed();
                obj.unload();
            }
        };
        obj.getLookupContents().add(ed);
        return ed;
    }

    @Override
    protected void handleDelete() throws IOException {
        BTreeMetaData metaData = lookup.lookup(BTreeMetaData.class);
        if(metaData != null){
            metaData.cleanup();
        }
        super.handleDelete();
    }

    @Override
    protected FileObject handleRename(String name) throws IOException {
        BTreeMetaData metaData = lookup.lookup(BTreeMetaData.class);
        metaData.rename(null, name);
        return super.handleRename(name);
    }

    @Override
    protected FileObject handleMove(DataFolder df) throws IOException {
        BTreeMetaData metaData = lookup.lookup(BTreeMetaData.class);
        metaData.rename(df, null);
        return super.handleMove(df);
    }

    @Override
    protected DataObject handleCopy(DataFolder df) throws IOException {
        BTreeMetaData metaData = lookup.lookup(BTreeMetaData.class);
        metaData.duplicate(df, null);
        return super.handleCopy(df);
    }

    @Override
    protected DataObject handleCopyRename(DataFolder df, String name, String ext) throws IOException {
        BTreeMetaData metaData = lookup.lookup(BTreeMetaData.class);
        metaData.duplicate(df, name);
        return super.handleCopyRename(df, name, ext);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void unload() {
        if (loaded) {
            loaded = false;
        }
    }

    public InstanceContent getLookupContents() {
        return lookupContents;
    }
    
    public void refresh() {
        loaded = true;
        
        try {
            mainTree = getParser().parse(getPrimaryFile().getInputStream(), null);
        } catch (Exception e) {
            ExceptionUtils.caughtException(e, "This problem occured when trying to load a behavior tree from file " + getPrimaryFile().getPath());
        }
    }
    
    public void setDirty() {
        setModified(true);
        lookupContents.add(new MySavable(this));
    }
    
    public void setClean() {
        setModified(false);
        lookupContents.remove(lookup.lookup(MySavable.class));
    }
    
    private class MySavable extends AbstractSavable {
        private final BTreeDataObject obj;
        public MySavable(BTreeDataObject obj) {
            this.obj = obj;
            register();
        }

        @Override
        protected String findDisplayName() {
            return obj.getName();
        }

        @Override
        protected void handleSave() throws IOException {
            //@TODO: One would have to synchronize access to mainTree as well..            
            synchronized (obj) {
                FileObject fo = getPrimaryFile();
                try (OutputStream out = new BufferedOutputStream(fo.getOutputStream())) {    
                    BTreeExporter.exportTree(out, mainTree);
                }
            }
            obj.lookupContents.remove(this);
            setModified(false);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MySavable) {
                return obj.mainTree.equals(((MySavable) o).obj.mainTree);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return obj.mainTree.hashCode();
        }
    }
}
