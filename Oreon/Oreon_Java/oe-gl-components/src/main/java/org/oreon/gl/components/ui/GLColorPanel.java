package org.oreon.gl.components.ui;

import org.oreon.common.ui.UIElement;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.pipeline.RenderParameter;
import org.oreon.core.gl.wrapper.parameter.AlphaBlending;
import org.oreon.core.math.Vec4f;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class GLColorPanel extends UIElement {

    private final GLShaderProgram shader;
    private final RenderParameter config;
    private final GUIVAO vao;
    private final Vec4f rgba;

    public GLColorPanel(Vec4f rgba, int xPos, int yPos, int xScaling, int yScaling,
                        GUIVAO panelMeshBuffer) {
        super(xPos, yPos, xScaling, yScaling);
        this.rgba = rgba;
        shader = UIColorPanelShader.getInstance();
        vao = panelMeshBuffer;
        config = new AlphaBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void render() {
        config.enable();
        shader.bind();
        shader.updateUniforms(getOrthographicMatrix());
        shader.updateUniforms(rgba);
        vao.draw();
        config.disable();
    }

}
