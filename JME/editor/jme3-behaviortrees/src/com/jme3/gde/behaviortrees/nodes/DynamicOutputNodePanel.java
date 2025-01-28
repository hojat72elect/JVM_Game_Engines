package com.jme3.gde.behaviortrees.nodes;

import com.badlogic.gdx.ai.btree.Task;
import com.jme3.gde.behaviortrees.editor.TreeNodePanel;
import com.jme3.gde.core.editor.nodes.Connection;
import com.jme3.gde.core.editor.nodes.ConnectionEndpoint;
import com.jme3.gde.core.editor.nodes.Diagram;
import java.util.ResourceBundle;
import javax.swing.JLabel;

/**
 * @author MeFisto94
 */
public abstract class DynamicOutputNodePanel extends TreeNodePanel {
    public static final ResourceBundle bundle = ResourceBundle.getBundle("com/jme3/gde/behaviortrees/nodes/Bundle");
    protected boolean dynamic = true;
    
    public DynamicOutputNodePanel(Diagram dia, Task task, NodeType nodeType, int numInputs, int numOutputs, String nameAndTitle) {
        super(task, dia, nodeType, numInputs, numOutputs, nameAndTitle);
        setSize(getPreferredSize()); // BUT WHY?
        revalidate();
        repaint();
    }
    
    public DynamicOutputNodePanel(Diagram dia, Task task, NodeType nodeType) {
        this(dia, task, nodeType, 1, 1, "DynamicOutputNodePanel");
    }

    @Override
    protected void onDisconnect(Connection conn) {
        super.onDisconnect(conn);
        if (dynamic && conn.getStart().getNode().equals(this)) {
            // Our goal is to find the first unconnected dot which is not the last one
            int idx = findFirstUnconnected();
            if (idx != -1) {
                outputDots.remove(idx);
                outputLabels.remove(idx);
                for (int i = idx; i < outputLabels.size(); i++) {
                    outputLabels.get(i).setText("" + (i + inputLabels.size()));
                }
            }
            
            initComponents();
            invalidate();
            setSize(getPreferredSize());
        }
    }

    @Override
    protected void onConnect(Connection conn) {
        super.onConnect(conn);
        if (dynamic && conn.getStart().getNode().equals(this)) {
            JLabel label = createLabel("" + num++, ConnectionEndpoint.ParamType.Output);
            ConnectionEndpoint dot = createConnectionEndpoint("", ConnectionEndpoint.ParamType.Output, "");
            //dot.setBorder(new LineBorder(Color.GREEN));
            outputLabels.add(label);
            outputDots.add(dot);

            initComponents();
            invalidate();
            setSize(getPreferredSize());
        }
    }
    
    protected int findFirstUnconnected() {
        // - 1 so the last entry is not iterated.
        for (int i = 0; i < outputDots.size() - 1; i++) {
            ConnectionEndpoint ce = outputDots.get(i);
            if (!ce.isConnected()) {
                return i;
            }
        }
        
        return -1;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isDynamic() {
        return dynamic;
    }

}
