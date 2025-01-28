package com.badlogic.gdx.backends.android.surfaceview;

/**
 * This {@link ResolutionStrategy} will place the GLSurfaceView with the given height and width in the center the screen.
 */
public class FixedResolutionStrategy implements ResolutionStrategy {

    private final int width;
    private final int height;

    public FixedResolutionStrategy(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public MeasuredDimension calcMeasures(int widthMeasureSpec, int heightMeasureSpec) {
        return new MeasuredDimension(width, height);
    }
}
