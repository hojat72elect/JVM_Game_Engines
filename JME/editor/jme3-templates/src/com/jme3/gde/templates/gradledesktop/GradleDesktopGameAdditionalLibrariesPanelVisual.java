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
import com.jme3.gde.templates.gradledesktop.options.TemplateLibrary;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.openide.WizardDescriptor;

/**
 * UI Component for the New Gradle Game additional libraries panel.
 *
 * @author peedeeboy
 */
public class GradleDesktopGameAdditionalLibrariesPanelVisual extends JPanel
        implements ListSelectionListener {

    private final int ROW_PADDING = 4;
    
    /**
     * Creates new form GradleDesktopGameAdditionalLibrariesPanelVisual
     */
    public GradleDesktopGameAdditionalLibrariesPanelVisual(
            GradleDesktopGameAdditionalLibrariesPanel panel) {
        initComponents();

        populateLibraryTable();
        additionalLibraryTable.getSelectionModel()
                .addListSelectionListener(this);
        additionalLibraryTable.setRowHeight(
                additionalLibraryTable.getRowHeight() + ROW_PADDING);
        additionalLibraryTable.setIntercellSpacing(new Dimension(0, 0));
        additionalLibraryTable.getParent().setBackground(
                additionalLibraryTable.getBackground());
        additionalLibraryTable.getColumnModel().getColumn(0).setMaxWidth(30);
        additionalLibraryTable.getTableHeader().setUI(null);
        additionalLibraryTable.repaint();
    }

    private void populateLibraryTable() {
        List<TemplateLibrary> libraries = CachedOptionsContainer.getInstance().getAdditionalLibraries();

        int noRows = libraries.size();
        Object[][] tableData = new Object[noRows][2];

        int row = 0;
        for (TemplateLibrary library : libraries) {
            tableData[row][0] = Boolean.FALSE;
            tableData[row][1] = library;
            row++;
        }

        String[] cols = {"", ""};
        AdditionalLibraryTableModel model = new AdditionalLibraryTableModel(tableData, cols);
        additionalLibraryTable.setModel(model);
        additionalLibraryTable.repaint();
    }

    @Override
    public void valueChanged(ListSelectionEvent event) {
        updateLibraryDescription();
    }

    private void updateLibraryDescription() {
        int selectedRow = additionalLibraryTable.getSelectedRow();

        if (selectedRow == -1) {
            libraryDescriptionTextArea.setText("");
        } else {
            TemplateLibrary selectedLibrary = (TemplateLibrary)                    additionalLibraryTable.getValueAt(selectedRow, 1);
            libraryDescriptionTextArea.setText(selectedLibrary
                    .getDescription());
        }
    }

    protected void store(WizardDescriptor d) {
        AdditionalLibraryTableModel model = (AdditionalLibraryTableModel)
                additionalLibraryTable.getModel();
        List<TemplateLibrary> selectedLibraries
                =                model.getSelectedLibraries();

        d.putProperty("additionalLibraries", selectedLibraries);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form EditorjmeVersion.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        additionalLibraryScrollPane = new JScrollPane();
        additionalLibraryTable = new JTable();
        jSeparator1 = new JSeparator();
        libraryDescriptionScrollPane = new JScrollPane();
        libraryDescriptionTextArea = new JTextArea();

        additionalLibraryTable.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        additionalLibraryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        additionalLibraryScrollPane.setViewportView(additionalLibraryTable);

        libraryDescriptionTextArea.setEditable(false);
        libraryDescriptionTextArea.setColumns(20);
        libraryDescriptionTextArea.setLineWrap(true);
        libraryDescriptionTextArea.setRows(3);
        libraryDescriptionTextArea.setWrapStyleWord(true);
        libraryDescriptionScrollPane.setViewportView(libraryDescriptionTextArea);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(additionalLibraryScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(libraryDescriptionScrollPane, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(additionalLibraryScrollPane, GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(libraryDescriptionScrollPane, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addGap(55, 55, 55))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane additionalLibraryScrollPane;
    private JTable additionalLibraryTable;
    private JSeparator jSeparator1;
    private JScrollPane libraryDescriptionScrollPane;
    private JTextArea libraryDescriptionTextArea;
    // End of variables declaration//GEN-END:variables

    private static final class AdditionalLibraryTableModel
            extends DefaultTableModel {

        public AdditionalLibraryTableModel(Object[][] data,
                Object[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return TemplateLibrary.class;
                default:
                    return super.getColumnClass(columnIndex);
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }

        public List<TemplateLibrary> getSelectedLibraries() {
            List<TemplateLibrary> selectedLibraries = new ArrayList<>();
            for (int i = 0; i < getRowCount(); i++) {
                if ((Boolean) getValueAt(i, 0)) {
                    selectedLibraries.add((TemplateLibrary) getValueAt(i, 1));
                }
            }

            return selectedLibraries;
        }
    }
}
