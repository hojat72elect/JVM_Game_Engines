/*
 *  Copyright (c) 2009-2020 jMonkeyEngine
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
package com.jme3.gde.core.importantfiles;

import com.jme3.gde.core.icons.IconList;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author normenhansen
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ImportantFilesNode extends AbstractNode implements FileChangeListener {

    private static Image smallImage = IconList.important.getImage();

    public ImportantFilesNode(Project proj) throws DataObjectNotFoundException {
        super(new ImportantFilesChildren(proj));
        proj.getProjectDirectory().addRecursiveListener(this);
    }

    @Override
    public String getDisplayName() {
        return "Important Files";
    }

    @Override
    public Image getIcon(int type) {
        return smallImage;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return smallImage;
    }

    public void fileFolderCreated(FileEvent fe) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ((ImportantFilesChildren) getChildren()).addNotify();
            }
        });
    }

    public void fileDataCreated(FileEvent fe) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ((ImportantFilesChildren) getChildren()).addNotify();
            }
        });
    }

    public void fileChanged(FileEvent fe) {
    }

    public void fileDeleted(FileEvent fe) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ((ImportantFilesChildren) getChildren()).addNotify();
            }
        });
    }

    public void fileRenamed(FileRenameEvent fre) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ((ImportantFilesChildren) getChildren()).addNotify();
            }
        });
    }

    public void fileAttributeChanged(FileAttributeEvent fae) {
    }

//    public static class LookupProviderImpl implements LookupProvider {
//
//        public Lookup createAdditionalLookup(Lookup lookup) {
//
//            Project prj = lookup.lookup(Project.class);
//
//            return Lookups.fixed(new ImportantFilesLookupItem(prj));
//        }
//    }
    public static class ImportantFilesNodeFactoryImpl implements NodeFactory {

        public NodeList createNodes(Project project) {

//            ImportantFilesLookupItem item = project.getLookup().lookup(ImportantFilesLookupItem.class);
//            if (item != null) {
            try {
                ImportantFilesNode nd = new ImportantFilesNode(project);
                return NodeFactorySupport.fixedNodeList(nd);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
//            }

            return NodeFactorySupport.fixedNodeList();
        }
    }

//    public static class ImportantFilesLookupItem {
//
//        public ImportantFilesLookupItem(Project prj) {
//        }
//    }
    public static class ImportantFilesChildren extends Children.Keys<ImportantFiles> {

        private Project project;

        public ImportantFilesChildren(Project project) {
            this.project = project;
        }

        protected List<ImportantFiles> createKeys() {
            ArrayList<ImportantFiles> list = new ArrayList<ImportantFiles>();
            for (ImportantFiles di : Lookup.getDefault().lookupAll(ImportantFiles.class)) {
                if (di.hasFiles(project)) {
                    list.add(di);
                }
            }
            return list;
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(createKeys());
        }

        @Override
        protected Node[] createNodes(ImportantFiles key) {
            Node[] nodes = key.getNodes(project);
            return nodes;
        }
    }
}