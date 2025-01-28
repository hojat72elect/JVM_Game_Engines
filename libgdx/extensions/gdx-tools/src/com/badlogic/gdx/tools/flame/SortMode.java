package com.badlogic.gdx.tools.flame;

import com.badlogic.gdx.graphics.g3d.particles.ParticleSorter;

enum SortMode {
    None("None", new ParticleSorter.None()), Distance("Distance", new ParticleSorter.Distance());

    public String desc;
    public ParticleSorter sorter;
    SortMode(String desc, ParticleSorter sorter) {
        this.sorter = sorter;
        this.desc = desc;
    }

    public static SortMode find(ParticleSorter sorter) {
        Class type = sorter.getClass();
        for (SortMode wrapper : values()) {
            if (wrapper.sorter.getClass() == type) return wrapper;
        }
        return null;
    }

    @Override
    public String toString() {
        return desc;
    }
}
