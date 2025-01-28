package com.badlogic.gdx.tests.g3d.shadows.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;

/**
 * Nearfar Analyzer computes near and far plane of camera. It has to call camera.update() after setting values. Updated camera's
 * frustum should encompass all casting shadow objects.
 */
public interface NearFarAnalyzer {
    /**
     * Update near and far plane of camera.
     *
     * @param light               Current light
     * @param camera              Light's camera
     * @param renderableProviders Renderable providers
     */
    public <T extends RenderableProvider> void analyze(BaseLight light, Camera camera, Iterable<T> renderableProviders);
}
