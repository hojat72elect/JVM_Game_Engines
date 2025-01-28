package com.badlogic.gdx.tools.flame;

import com.badlogic.gdx.graphics.GL20;

enum BlendFunction {
    ZERO(GL20.GL_ZERO), ONE(GL20.GL_ONE), SRC_ALPHA(GL20.GL_SRC_ALPHA), SRC_COLOR(GL20.GL_SRC_COLOR), DST_ALPHA(
            GL20.GL_DST_ALPHA), DST_COLOR(GL20.GL_DST_COLOR), ONE_MINUS_SRC_COLOR(GL20.GL_ONE_MINUS_SRC_COLOR), ONE_MINUS_SRC_ALPHA(
            GL20.GL_ONE_MINUS_SRC_ALPHA), ONE_MINUS_DST_COLOR(
            GL20.GL_ONE_MINUS_DST_COLOR), ONE_MINUS_DST_ALPHA(GL20.GL_ONE_MINUS_DST_ALPHA);

    public int blend;

    private BlendFunction(int blend) {
        this.blend = blend;
    }

    public static BlendFunction find(int function) {
        for (BlendFunction func : values()) {
            if (func.blend == function) return func;
        }
        return null;
    }
}
