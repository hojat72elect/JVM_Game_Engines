package org.oreon.core.gl.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL13.GL_SAMPLE_ALPHA_TO_COVERAGE;
import static org.lwjgl.opengl.GL20.GL_POINT_SPRITE;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;
import static org.lwjgl.opengl.GL32.GL_SAMPLE_MASK;

public class GLUtil {

    public static void init() {

        glFrontFace(GL_CW);
        glEnable(GL_POINT_SPRITE);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_SAMPLE_MASK);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_FRAMEBUFFER_SRGB);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_PROGRAM_POINT_SIZE);
        glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        glEnable(GL_CLIP_DISTANCE0);
        glEnable(GL_CLIP_DISTANCE1);
        glEnable(GL_CLIP_DISTANCE2);
        glEnable(GL_CLIP_DISTANCE3);
        glEnable(GL_CLIP_DISTANCE4);
        glEnable(GL_CLIP_DISTANCE5);
    }

    public static void clearScreen() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClearDepth(1.0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

}
