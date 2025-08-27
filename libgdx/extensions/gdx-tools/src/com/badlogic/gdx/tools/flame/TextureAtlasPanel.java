package com.badlogic.gdx.tools.flame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 */
public class TextureAtlasPanel extends JPanel {
    JPanel regionsPanel;
    TextureAtlas atlas;
    String atlasFilename;

    public TextureAtlasPanel() {
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new GridBagLayout());
        JButton backwardButton, forwardButton;

        add(backwardButton = new JButton("<"), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(regionsPanel = new JPanel(), new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(forwardButton = new JButton(">"), new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        regionsPanel.setLayout(new CustomCardLayout());

        backwardButton.addActionListener(arg0 -> {
            if (atlas == null) return;
            CustomCardLayout layout = (CustomCardLayout) regionsPanel.getLayout();
            layout.previous(regionsPanel);
        });

        forwardButton.addActionListener(arg0 -> {
            if (atlas == null) return;
            CustomCardLayout layout = (CustomCardLayout) regionsPanel.getLayout();
            layout.next(regionsPanel);
        });
    }

    public void setAtlas(TextureAtlas atlas, String atlasFilename) {
        if (atlas == this.atlas) return;
        regionsPanel.removeAll();
        Array<AtlasRegion> atlasRegions = atlas.getRegions();
        CustomCardLayout layout = (CustomCardLayout) regionsPanel.getLayout();
        Array<TextureRegion> regions = new Array<>();
        for (Texture texture : atlas.getTextures()) {
            regionsPanel.add(new TexturePanel(texture, getRegions(texture, atlasRegions, regions)));
        }
        layout.first(regionsPanel);
        this.atlas = atlas;
        this.atlasFilename = atlasFilename;
    }

    public String getAtlasName() {
        return atlasFilename;
    }

    protected Array<TextureRegion> getRegions(Texture texture, Array<AtlasRegion> atlasRegions, Array<TextureRegion> out) {
        out.clear();
        for (TextureRegion region : atlasRegions) {
            if (region.getTexture() == texture) out.add(region);
        }
        return out;
    }

    public TexturePanel getCurrentRegionPanel() {
        CustomCardLayout layout = (CustomCardLayout) regionsPanel.getLayout();
        return layout.getCurrentCard(regionsPanel);
    }

    public void clearSelection() {
        for (Component regionPanel : regionsPanel.getComponents())
            ((TexturePanel) regionPanel).clearSelection();
    }
}
