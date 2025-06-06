package org.oreon.core.gl.context;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.lwjgl.opengl.GL11;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.platform.GLWindow;
import org.oreon.core.gl.scenegraph.GLCamera;
import org.oreon.core.gl.util.GLUtil;

import static org.lwjgl.glfw.GLFW.glfwInit;

@Log4j2
public class GLContext extends BaseContext {

    @Getter
    private static GLResources resources;

    public static void create() {

        init();

        camera = new GLCamera();
        window = new GLWindow();
        resources = new GLResources();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // create OpenGL Context
        window.create();

        log.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
//		log.info("Max Geometry Uniform Blocks: " + GL11.glGetInteger(GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS));
//		log.info("Max Geometry Shader Invocations: " + GL11.glGetInteger(GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS));
//		log.info("Max Uniform Buffer Bindings: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS));
//		log.info("Max Uniform Block Size: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BLOCK_SIZE) + " bytes");
//		log.info("Max SSBO Block Size: " + GL11.glGetInteger(GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE) + " bytes");	
//		log.info("Max Image Bindings: " + GL11.glGetInteger(GL42.GL_MAX_IMAGE_UNITS));

        GLUtil.init();
    }

    public static GLCamera getCamera() {

        return (GLCamera) camera;
    }

    public static GLWindow getWindow() {

        return (GLWindow) window;
    }

    public static void updateGlobalShaderParameters() {

    }

}
