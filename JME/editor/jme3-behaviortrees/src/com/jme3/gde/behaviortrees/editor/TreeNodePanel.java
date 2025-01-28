/*
 *  Copyright (c) 2009-2018 jMonkeyEngine
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
package com.jme3.gde.behaviortrees.editor;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.InputMappingBlock;
import com.jme3.gde.behaviortrees.OutputMappingBlock;
import com.jme3.gde.core.editor.nodes.ConnectionEndpoint;
import com.jme3.gde.core.editor.nodes.NodePanel;
import com.jme3.gde.core.editor.icons.Icons;
import com.jme3.gde.core.editor.nodes.Connection;
import com.jme3.gde.core.editor.nodes.Diagram;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * The TreeNodePanel is the ShaderNode specific implementation of 
 {@link NodePanel}.
 * @author MeFisto94
 */
public class TreeNodePanel extends NodePanel implements InOut, 
        PropertyChangeListener {
    private NodeType type = NodeType.LeafTask;
    // Required for the Editor
    protected List<String> filePaths = new ArrayList<>();
    protected int num = 0;
    protected Task task;
    
    // Guards:
    // The Panel which represents THIS nodes Guard (may be null)
    protected TreeNodePanel guardPanel;
    // The Panel to which THIS Guard belongs to (may be null)
    protected TreeNodePanel guardedPanel;
    
    public enum NodeType {
        LeafTask(new Color(220, 220, 70)), // yellow
        Root(new Color(220, 70, 70)), // red
        Sequential(new Color(114, 200, 220)), // blue
        Parallel(new Color(70, 220, 70)),//green
        BuiltinLeafTask(new Color(170, 40, 220)), // purple
        Decorator(new Color(255, 114, 40)); // orange
        
        private Color color;

        private NodeType() {
        }

        private NodeType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    public TreeNodePanel(Task task, Diagram dia, NodeType type, int numIns, int numOuts, String nameAndTitle, TreeNodePanel guardedPanel) {
        super();
        this.type = type;
        this.diagram = dia; // required by our swing stuff which we have in the constructor
        this.task = task;
        this.guardedPanel = guardedPanel;
        this.name = nameAndTitle;
               
        if (task.getGuard() != null) {
            setGuardPanel(BTreeNodeEditorElement.taskToPanel(diagram, task.getGuard(), this));
            guardPanel.setGuardedPanel(this);
        }
        
        /*node.addPropertyChangeListener(WeakListeners.propertyChange(this, node));
        this.addPropertyChangeListener(WeakListeners.propertyChange(node, this));*/
        
        /*
        //setToolbar(new ShaderNodeToolBar(this));
        refresh(node);
        
        filePaths.addAll(def.getShadersPath());
        String defPath = ((DefinitionBlock)node.getContents().get(0)).getPath();
        filePaths.add(defPath);    */
        init(numIns, numOuts);

    }

    public TreeNodePanel(Task task, Diagram dia, NodeType type, int numIns, int numOuts, String nameAndTitle) {
        this(task, dia, type, numIns, numOuts, nameAndTitle, null);
    }
    
    public TreeNodePanel(Task task, Diagram dia, NodeType type, int numIns, int numOuts) {
        this(task, dia, type, numIns, numOuts, null);
    }
    
    public List<TreeNodePanel> getChildren() {
        return outputDots.stream()
            .map(ConnectionEndpoint::getConnection)
            // check for null (unconnected outputs)
            .filter(c -> c != null)
            .map(Connection::getEnd)
            .map(ConnectionEndpoint::getNode)
            .filter(dp -> dp instanceof TreeNodePanel)
            .map(dp -> (TreeNodePanel)dp)
            .collect(Collectors.toList());
    }

    public Task getTask() {
        return task;
    }

    /* All Guard related Setters need a call to init() to refresh the UI */
    public TreeNodePanel getGuardPanel() {
        return guardPanel;
    }
    
    public void setGuardPanel(TreeNodePanel guardPanel) {
        this.guardPanel = guardPanel;
    }

    public boolean isGuard() {
        return guardedPanel != null;
    }

    public void setGuardedPanel(TreeNodePanel guardedPanel) {
        this.guardedPanel = guardedPanel;
        if (isGuard()) {
            color = new Color(40, 40, 255);
            backgroundColor = color;            
        } else {
            updateType();
        }
    }
    
    public TreeNodePanel getGuardedPanel() {
        return guardedPanel;
    }
    
    public Task getGuard() {
        return task.getGuard();
    }
    
    public boolean hasGuard() {
        return getGuard() != null;
    }
    
    public void attachGuard(TreeNodePanel guardPanel) {
        setGuardPanel(guardPanel);
        getGuardPanel().setGuardedPanel(this);
        
        ((BTreeNodeEditorElement)diagram.getEditorParent()).onAttachGuard(this, guardPanel);
    }
    
    public void detachGuard() {
        getGuardPanel().setGuardedPanel(null);
        setGuardPanel(null);
        ((BTreeNodeEditorElement)diagram.getEditorParent()).onDetachGuard(this, guardPanel);
    }
    
    /**
     * Creates a new Input Terminal for this Node (takes care of creating the 
     * labels etc).
     * @param reloadUI Whether the UI should be updated automatically or not.
     * Set this to false when bulk adding and call initComponents manually
     * @return 
     */
    public ConnectionEndpoint createInput(boolean reloadUI) {
        /* We need to add labels or override NodePanel#initComponents, so we
         * turn our bug into a feature and use the label to enumerate the 
         * connectors
        */
        String numStr = "" + num++;
        JLabel label = createLabel(numStr, ConnectionEndpoint.ParamType.Input);
        //label.setBorder(new LineBorder(Color.CYAN));
        ConnectionEndpoint dot = createConnectionEndpoint("", ConnectionEndpoint.ParamType.Input, numStr);
        //dot.setBorder(new LineBorder(Color.GREEN));
        inputLabels.add(label);
        inputDots.add(dot);
        
        if (reloadUI) {
            initComponents();
        }
        
        return dot;
    }
    
    public ConnectionEndpoint createOutput(boolean reloadUI) {
        String numStr = "" + num++;
        JLabel label = createLabel(numStr, ConnectionEndpoint.ParamType.Output);
        //label.setBorder(new LineBorder(Color.CYAN));
        ConnectionEndpoint dot = createConnectionEndpoint("", ConnectionEndpoint.ParamType.Output, numStr);
        //dot.setBorder(new LineBorder(Color.GREEN));
        outputLabels.add(label);
        outputDots.add(dot);
        
        if (reloadUI) {
            initComponents();
        }
        
        return dot;
    }
    
    private void init(int numInputs, int numOutputs) {
        //setBounds(0, 0, 100, 30 + inputs.size() * 20 + outputs.size() * 20);
        setBounds(0, 0, 100, 30);

        for (int i = 0; i < numInputs; i++) {
            createInput(false);
        }
        
        for (int i = 0; i < numOutputs; i++) {
            createOutput(false);
        }

        initComponents();
        setOpaque(false);
    }

    @Override
    protected void initComponents() {
        String oldHeaderText = null;
        Icon oldHeaderIcon = null;
        
        // Support calling initComponents as refresher
        if (getLayout() instanceof GroupLayout) {
            removeAll();
            oldHeaderText = header.getText();
            oldHeaderIcon = header.getIcon();
        }
        
        header = new JLabel(Icons.vert);
        header.setForeground(Color.BLACK);
        header.addMouseListener(labelMouseMotionListener);
        header.addMouseMotionListener(labelMouseMotionListener);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(new Font("Tahoma", Font.BOLD, 11));
        //header.setBorder(new LineBorder(Color.RED));
        
        // Support calling initComponents as refresher
        if (getLayout() instanceof GroupLayout) {
            header.setText(oldHeaderText);
            header.setIcon(oldHeaderIcon);
        } else {
            if (name != null) {
                header.setText(name);
            }
            
            updateType();
        }
        
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        
        GroupLayout.SequentialGroup hHeader = layout.createSequentialGroup()
            .addGap(16) // Support Padding
            .addComponent(header)
            .addGap(16);
        
        if (inputDots.size() != inputLabels.size() || outputDots.size() != outputLabels.size()) {
            throw new IllegalArgumentException("Dots and Labels don't match");
        }
        
        final int DOT_SIZE = 10;
        
        if (isGuard()) {
            layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGap(16)
                    .addComponent(header)
                    .addGap(16)
            );

            layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addGroup(GroupLayout.Alignment.CENTER, hHeader)
            );            
        } else {        
            GroupLayout.ParallelGroup vInputGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
            if (inputDots.isEmpty()) {
                vInputGroup.addGroup(GroupLayout.Alignment.CENTER,
                    layout.createSequentialGroup()
                        .addGap(16)
                );
            } else {
                for (int i = 0; i < inputDots.size(); i++) {
                    vInputGroup.addGroup(GroupLayout.Alignment.CENTER, 
                        layout.createSequentialGroup()
                            .addGap(8) // Padding from the top
                            .addComponent(inputDots.get(i), GroupLayout.PREFERRED_SIZE,
                                    DOT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(2)
                            .addComponent(inputLabels.get(i))
                            .addGap(8) // Padding the bottom
                    );
                }
            }

            GroupLayout.SequentialGroup hInputGroup = layout.createSequentialGroup();
            hInputGroup.addGap(16);
            for (int i = 0; i < inputDots.size(); i++) {
                hInputGroup.addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(inputDots.get(i), GroupLayout.PREFERRED_SIZE,
                                DOT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(inputLabels.get(i), GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                );
                hInputGroup.addGap(16);
            }

            // Output
            GroupLayout.ParallelGroup vOutputGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
            if (outputDots.isEmpty()) {
                vOutputGroup.addGroup(GroupLayout.Alignment.CENTER,
                    layout.createSequentialGroup()
                        .addGap(16)
                );
            } else {
                for (int i = 0; i < outputDots.size(); i++) {
                    vOutputGroup.addGroup(GroupLayout.Alignment.CENTER, 
                        layout.createSequentialGroup()
                            .addGap(8) // Padding from the top
                            .addComponent(outputLabels.get(i))
                            .addGap(2)
                            .addComponent(outputDots.get(i), GroupLayout.PREFERRED_SIZE,
                                    DOT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(8) // Padding the bottom
                    );
                }
            }

            GroupLayout.SequentialGroup hOutputGroup = layout.createSequentialGroup();
            hOutputGroup.addGap(16);
            for (int i = 0; i < outputDots.size(); i++) {
                hOutputGroup.addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(outputLabels.get(i), GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(outputDots.get(i), GroupLayout.PREFERRED_SIZE,
                                DOT_SIZE, GroupLayout.PREFERRED_SIZE)
                );
                hOutputGroup.addGap(16);
            }
            
            // Maybe add some padding to achieve a minimum size (clever padding?)
            if (guardPanel != null) {
                layout.setVerticalGroup(
                    layout.createSequentialGroup()
                        .addGroup(vInputGroup)
                        .addComponent(header)
                        .addComponent(guardPanel)
                        .addGroup(vOutputGroup)
                );

                layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(hInputGroup)
                        .addGroup(GroupLayout.Alignment.CENTER, hHeader)
                        .addComponent(guardPanel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(hOutputGroup)
                );
            } else { // Neither HAVING a Guard nor BEING a Guard
                layout.setVerticalGroup(
                    layout.createSequentialGroup()
                        .addGroup(vInputGroup)
                        .addComponent(header)
                        .addGroup(vOutputGroup)
                );

                layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(hInputGroup)
                        .addGroup(GroupLayout.Alignment.CENTER, hHeader)
                        .addGroup(hOutputGroup)
                );            
            }
        }
    }

    @Override
    protected void paintTitleBar(Graphics2D g) {
        // Purposely NO-OP
    }
    
    public boolean isOrphan() {
        return !(inputDots.stream().anyMatch(d -> d.isConnected()) ||
            outputDots.stream().anyMatch(d -> d.isConnected()));
    }
    

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        
        if (svdx != getLocation().x) {
            //firePropertyChange(ShaderNodeBlock.POSITION, svdx, getLocation().x);
        }
        
        diagram.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
        if (isGuard()) { // has to be before super call or else svdx might get overwritten
            if (getParent() != null) {
                Dimension parentSize = getParent().getSize();
                Dimension thisSize = getSize();

                // If not we don't have enough data available it seems.
                if (parentSize.height > 0 && parentSize.width > 0 && thisSize.height > 0 && thisSize.width > 0) {
                    
                    /* The Usage of LocationOnScreen is probably wrong in the whole DraggablePanel Class,
                     * as it does not matter on which monitor you have this open, it would've been better
                     * to have it relative to the parent. Because since I use the SDK on Display 2, I always
                     * have an x-offset of 1920...
                     */
                    int newX = svdx + e.getLocationOnScreen().x - svdex;
                    int newY = svdy + e.getLocationOnScreen().y - svdey;
                    
                    if (newX < 0 || newX > parentSize.width - thisSize.width || newY < 0 || newY > parentSize.height - thisSize.height) {
                        // Remove the Guard and make a real node out of it
                        int newPosX = getGuardedPanel().getLocation().x + e.getPoint().x;
                        int newPosY = getGuardedPanel().getLocation().y + e.getPoint().y;
                        
                        setLocation(newPosX, newPosY);    
                        TreeNodePanel guardedPnl = getGuardedPanel();
                        getGuardedPanel().detachGuard();
                        // here, getGuardedPanel is invalid, so we cached it
                        guardedPnl.initComponents();
                        guardedPnl.setSize(guardedPnl.getPreferredSize());
                        initComponents();
                        setSize(getPreferredSize());
                        diagram.addNode(this); // Add as a regular node
                        ((TreeDiagram)diagram).refreshNavigator(true);
                    }
                }
            }
        } else {
            List<NodePanel> list = ((TreeDiagram)diagram).getPanelsAt(e.getX() + getLocation().x, e.getY() + getLocation().y);
            
            if (!list.isEmpty() && list.get(0) != this) {
                diagram.removeNode(this);
                ((TreeNodePanel)list.get(0)).attachGuard(this);
                initComponents();
                setSize(getPreferredSize());
                getGuardedPanel().initComponents();
                getGuardedPanel().setSize(getGuardedPanel().getPreferredSize());
                ((TreeDiagram)diagram).refreshNavigator(true);
            }
        }
    }
    
    @Override
    protected void movePanel(int xoffset, int yoffset) {
        if (isGuard()) { // has to be before super call or else svdx might get overwritten
            if (getParent() != null) {
                Dimension parentSize = getParent().getSize();
                Dimension thisSize = getSize();

                // If not we don't have enough data available it seems.
                if (parentSize.height > 0 && parentSize.width > 0 && thisSize.height > 0 && thisSize.width > 0) {
                    int newX = svdx + xoffset;
                    int newY = svdy + yoffset;
                    
                    if (newX < 0 || newX > parentSize.width - thisSize.width || newY < 0 || newY > parentSize.height - thisSize.height) {
                        diagram.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        diagram.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }
        }
        
        // @TODO: we could use TreeDiagram#getNodesAt() to disallow stacking nodes and make them magnetically avoided.
        
        super.movePanel(xoffset, yoffset);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("name")) {
            //refresh((ShaderNodeBlock) evt.getSource());
        }
    }
    
    @Override
    /**
     * Keys are important for Selectable, they have to be unique, so we take toString
     */
    public String getKey() {
        return toString();
    }
    
    public NodeType getType() {
        return type;
    }
    
    
    /**
     * This methods is responsible for setting the correct icon and text
     * in the header-bar of the node. Call this from your constructor,
     * _after_ components have been inited.
     */
    protected void updateType() {
        /*switch (type) {
            case MatParam:
                header.setIcon(Icons.mat);
                setNameAndTitle("MatParam");
                break;
        }*/
        
        color = type.getColor();
        backgroundColor = color;
    }
    
    /**
     * Utility method to update the node text when the underlying ShaderNode
     * has been changed (called by PropertyChangeListeners)
     * @param node The source shadernode
     */
    /*protected final void refresh(ShaderNodeBlock node) {
        setNameAndTitle(node.getName());
    }*/
    
    @Override
    protected boolean canEdit() {
        return false; // We don't use Edit Dialogs yet
    }
    
    /**
     * Create a Label for a given ShaderNodes' Input/Output
     * @param type Whether this is the input or output
     * @return The IO Label
     */
    @Override
    protected JLabel createLabel(String txt, ConnectionEndpoint.ParamType type) {
        JLabel label = super.createLabel(txt, type);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setToolTipText(txt);
        return label;
    }
    
    @Override
    public ConnectionEndpoint createConnectionEndpoint(String type, 
            ConnectionEndpoint.ParamType paramType, String paramName) {
        TreeConnectionEndpoint con = new TreeConnectionEndpoint();
        con.setNode(this);
        con.setText(paramName);
        con.setParamType(paramType);
        con.setType(type);
        return con;
    }
    
    // Callbacks when Connections are formed and released
    @Override
    public void addInputMapping(InputMappingBlock block) { }

    @Override
    public void removeInputMapping(InputMappingBlock block) { }

    @Override
    public void addOutputMapping(OutputMappingBlock block) { }

    @Override
    public void removeOutputMapping(OutputMappingBlock block) { }

    public ConnectionEndpoint getOutputByIndex(int idx) {
        return outputDots.get(idx);
    }
    
    public ConnectionEndpoint getInputByIndex(int idx) {
        return inputDots.get(idx);
    }

}
