package com.badlogic.gdx.tools.flame;

import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PointSpriteBatchPanel extends EditorPanel<PointSpriteParticleBatch> {

    JComboBox sortCombo;
    JComboBox srcBlendFunction, destBlendFunction;

    public PointSpriteBatchPanel(FlameMain particleEditor3D, PointSpriteParticleBatch renderer) {
        super(particleEditor3D, "Point sprite Batch", "Renderer used to draw point sprite particles.");
        initializeComponents(renderer);
        setValue(renderer);
    }

    private void initializeComponents(PointSpriteParticleBatch renderer) {

        // Sort
        sortCombo = new JComboBox();
        sortCombo.setModel(new DefaultComboBoxModel(SortMode.values()));
        sortCombo.setSelectedItem(SortMode.find(renderer.getSorter()));
        sortCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                SortMode mode = (SortMode) sortCombo.getSelectedItem();
                editor.getPointSpriteBatch().setSorter(mode.sorter);
            }
        });

        // Blending source
        srcBlendFunction = new JComboBox();
        srcBlendFunction.setModel(new DefaultComboBoxModel(BlendFunction.values()));
        srcBlendFunction.setSelectedItem(BlendFunction.find(renderer.getBlendingAttribute().sourceFunction));
        srcBlendFunction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                BlendFunction blend = (BlendFunction) srcBlendFunction.getSelectedItem();
                editor.getPointSpriteBatch().getBlendingAttribute().sourceFunction = blend.blend;
            }
        });

        // Blending destination
        destBlendFunction = new JComboBox();
        destBlendFunction.setModel(new DefaultComboBoxModel(BlendFunction.values()));
        destBlendFunction.setSelectedItem(BlendFunction.find(renderer.getBlendingAttribute().destFunction));
        destBlendFunction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                BlendFunction blend = (BlendFunction) destBlendFunction.getSelectedItem();
                editor.getPointSpriteBatch().getBlendingAttribute().destFunction = blend.blend;
            }
        });

        int i = 0;
        Insets insets = new Insets(3, 0, 0, 0);
        contentPanel.add(new JLabel("Sort"),
                new GridBagConstraints(0, i, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        contentPanel.add(sortCombo,
                new GridBagConstraints(1, i++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        contentPanel.add(new JLabel("Blending Src"),
                new GridBagConstraints(0, i, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        contentPanel.add(srcBlendFunction,
                new GridBagConstraints(1, i++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        contentPanel.add(new JLabel("Blending Dest"),
                new GridBagConstraints(0, i, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        contentPanel.add(destBlendFunction,
                new GridBagConstraints(1, i++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    }

}
