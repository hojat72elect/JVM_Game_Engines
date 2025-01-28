package com.badlogic.gdx.tools.flame;

import com.badlogic.gdx.graphics.g3d.particles.ParticleShader.AlignMode;

enum AlignModeWrapper {
    Screen(AlignMode.Screen, "Screen"), ViewPoint(AlignMode.ViewPoint, "View Point");

    public String desc;
    public AlignMode mode;
    AlignModeWrapper(AlignMode mode, String desc) {
        this.mode = mode;
        this.desc = desc;
    }

    public static AlignModeWrapper find(AlignMode alignMode) {
        for (AlignModeWrapper wrapper : values()) {
            if (wrapper.mode == alignMode) return wrapper;
        }
        return null;
    }

    @Override
    public String toString() {
        return desc;
    }
}
