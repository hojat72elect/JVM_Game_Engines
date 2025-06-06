package com.badlogic.gdx.backends.android.surfaceview;

/**
 * Will manipulate the GLSurfaceView. Gravity is always center. The width and height of the View will be determined by the
 * classes implementing {@link ResolutionStrategy}.
 */
public interface ResolutionStrategy {

    MeasuredDimension calcMeasures(final int widthMeasureSpec, final int heightMeasureSpec);

    class MeasuredDimension {
        public final int width;
        public final int height;

        public MeasuredDimension(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
