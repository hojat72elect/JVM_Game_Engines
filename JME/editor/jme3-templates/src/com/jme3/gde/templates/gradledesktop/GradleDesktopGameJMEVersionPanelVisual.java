/*
 * Copyright (c) 2022 jMonkeyEngine
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

package com.jme3.gde.templates.gradledesktop;

import com.jme3.gde.templates.gradledesktop.options.CachedOptionsContainer;
import com.jme3.gde.templates.gradledesktop.options.JMEVersion;
import com.jme3.gde.templates.gradledesktop.options.LWJGLLibrary;
import com.jme3.gde.templates.gradledesktop.options.LibraryVersion;
import com.jme3.gde.templates.gradledesktop.options.TemplateLibrary;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.HyperlinkEvent;
import org.openide.WizardDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * UI Component for the New Gradle Game Wizard JME Version panel
 *
 * @author peedeeboy
 */
public class GradleDesktopGameJMEVersionPanelVisual extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(
            GradleDesktopGameJMEVersionPanel.class.getName());

    private boolean jmeVersionsInitialized = false;

    /**
     * Creates new form GradleDesktopGameJMEVersion
     */
    public GradleDesktopGameJMEVersionPanelVisual(
            GradleDesktopGameJMEVersionPanel panel) {
        initComponents();
        additionalComponentConfiguration();

        addAncestorListener(new AncestorListener() {

            @Override
            public void ancestorAdded(AncestorEvent event) {

                // Refresh the jME version selection
                Object selection = jmeVersionComboBox.getSelectedItem();
                jmeVersionComboBox.setModel(new DefaultComboBoxModel<>(CachedOptionsContainer.getInstance().getJmeVersions().toArray(LibraryVersion[]::new)));
                if (selection != null && jmeVersionsInitialized) {
                    jmeVersionComboBox.setSelectedItem(selection);
                }
                jmeVersionChanged();

                jmeVersionsInitialized = true;
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {

            }

            @Override
            public void ancestorMoved(AncestorEvent event) {

            }

        });

        loadPatchNotes();
        updateLWJGLdescription();
    }
    
    private void jmeVersionChanged() {
        loadPatchNotes();
    }

    private void additionalComponentConfiguration() {
        // Set the JME Version text pane to use system fonts so it displays
        // correctly on dark themes such as DarkMonkey
        jmeVersionDescriptionTextPane.putClientProperty(
                JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        // Set a URL handler on the JME Version text pane so patch notes will
        // open in browser
        jmeVersionDescriptionTextPane.addHyperlinkListener(
                (HyperlinkEvent e) -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (URISyntaxException ex) {
                        LOGGER.log(Level.WARNING, "Badly formatted URL: {0}",
                                e.toString());
                    } catch (IOException ex) {
                        LOGGER.log(Level.WARNING, "Could not open browser: {0}",
                                e.toString());
                    }
                }
            }
        });
    }

    private void loadPatchNotes() {
        LibraryVersion jmeVersionSelected = jmeVersionComboBox.getItemAt(jmeVersionComboBox.getSelectedIndex());
        try {
            URL patchNotesURL = GradleDesktopGameJMEVersionPanelVisual.class
                    .getResource(jmeVersionSelected.getPatchNotesPath());
            jmeVersionDescriptionTextPane.setPage(patchNotesURL);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not open patch notes for JME "
                    + "Version: {0}", jmeVersionSelected.toString());
            jmeVersionDescriptionTextPane.setText("");
            Exceptions.printStackTrace(ex);
        }
    }

    private void updateLWJGLdescription() {
        TemplateLibrary lwjglVersion = lwjglComboBox.getItemAt(lwjglComboBox.getSelectedIndex());
        lwjglTextArea.setText(lwjglVersion.getDescription());
    }

    protected void store(WizardDescriptor d) {
        LibraryVersion jmeVersion = jmeVersionComboBox.getItemAt(jmeVersionComboBox.getSelectedIndex());
        TemplateLibrary lwjglLibrary = lwjglComboBox.getItemAt(lwjglComboBox.getSelectedIndex());

        d.putProperty("jmeVersion", jmeVersion);
        d.putProperty("lwjglLibrary", lwjglLibrary);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jmeVersionLabel = new JLabel();
        jmeVersionComboBox = new JComboBox<>();
        jmeVersionDescriptionScrollPane = new JScrollPane();
        jmeVersionDescriptionTextPane = new JTextPane();
        jSeparator1 = new JSeparator();
        lwjglVersionLabel = new JLabel();
        lwjglComboBox = new JComboBox<>();
        lwjglDescriptionScrollPane = new JScrollPane();
        lwjglTextArea = new JTextArea();

        setPreferredSize(new Dimension(280, 360));

        jmeVersionLabel.setLabelFor(jmeVersionComboBox);
        Mnemonics.setLocalizedText(jmeVersionLabel, NbBundle.getMessage(GradleDesktopGameJMEVersionPanelVisual.class, "GradleDesktopGameJMEVersionPanelVisual.jmeVersionLabel.text")); // NOI18N

        jmeVersionComboBox.setModel(new DefaultComboBoxModel<LibraryVersion>(JMEVersion.values()));
        jmeVersionComboBox.setMaximumSize(new Dimension(100, 25));
        jmeVersionComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jmeVersionComboBoxActionPerformed(evt);
            }
        });

        jmeVersionDescriptionTextPane.setEditable(false);
        jmeVersionDescriptionTextPane.setContentType("text/html"); // NOI18N
        jmeVersionDescriptionTextPane.setText(NbBundle.getMessage(GradleDesktopGameJMEVersionPanelVisual.class, "GradleDesktopGameJMEVersionPanelVisual.jmeVersionDescriptionTextPane.text")); // NOI18N
        try {
            jmeVersionDescriptionTextPane.setPage(GradleDesktopGameJMEVersionPanelVisual.class.getResource("/com/jme3/gde/templates/files/patchnotes/341-stable.html"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        jmeVersionDescriptionScrollPane.setViewportView(jmeVersionDescriptionTextPane);

        lwjglVersionLabel.setLabelFor(lwjglComboBox);
        Mnemonics.setLocalizedText(lwjglVersionLabel, NbBundle.getMessage(GradleDesktopGameJMEVersionPanelVisual.class, "GradleDesktopGameJMEVersionPanelVisual.lwjglVersionLabel.text")); // NOI18N

        lwjglComboBox.setModel(new DefaultComboBoxModel<TemplateLibrary>(LWJGLLibrary.values()));
        lwjglComboBox.setMaximumSize(new Dimension(100, 25));
        lwjglComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                lwjglComboBoxActionPerformed(evt);
            }
        });

        lwjglTextArea.setEditable(false);
        lwjglTextArea.setColumns(20);
        lwjglTextArea.setLineWrap(true);
        lwjglTextArea.setRows(3);
        lwjglTextArea.setWrapStyleWord(true);
        lwjglDescriptionScrollPane.setViewportView(lwjglTextArea);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lwjglDescriptionScrollPane)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jmeVersionDescriptionScrollPane, GroupLayout.Alignment.TRAILING)
                            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jmeVersionLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jmeVersionComboBox, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1, GroupLayout.Alignment.TRAILING))
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lwjglVersionLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lwjglComboBox, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jmeVersionLabel)
                    .addComponent(jmeVersionComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jmeVersionDescriptionScrollPane, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lwjglVersionLabel)
                    .addComponent(lwjglComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(lwjglDescriptionScrollPane, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void lwjglComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_lwjglComboBoxActionPerformed
        updateLWJGLdescription();
    }//GEN-LAST:event_lwjglComboBoxActionPerformed

    private void jmeVersionComboBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jmeVersionComboBoxActionPerformed
        jmeVersionChanged();
    }//GEN-LAST:event_jmeVersionComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JSeparator jSeparator1;
    JComboBox<LibraryVersion> jmeVersionComboBox;
    JScrollPane jmeVersionDescriptionScrollPane;
    JTextPane jmeVersionDescriptionTextPane;
    JLabel jmeVersionLabel;
    JComboBox<TemplateLibrary> lwjglComboBox;
    JScrollPane lwjglDescriptionScrollPane;
    JTextArea lwjglTextArea;
    JLabel lwjglVersionLabel;
    // End of variables declaration//GEN-END:variables

}
