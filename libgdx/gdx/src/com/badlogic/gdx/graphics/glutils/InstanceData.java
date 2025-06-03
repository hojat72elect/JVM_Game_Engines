package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.Disposable;

import java.nio.FloatBuffer;

/**
 * A InstanceData instance holds instance data for rendering with OpenGL. It is implemented as either a
 * {@link InstanceBufferObject} or a {@link InstanceBufferObjectSubData}. Both require Open GL 3.3+.
 */
public interface InstanceData extends Disposable {

    /**
     * @return the number of vertices this InstanceData stores
     */
    int getNumInstances();

    /**
     * @return the number of vertices this InstanceData can store
     */
    int getNumMaxInstances();

    /**
     * @return the {@link VertexAttributes} as specified during construction.
     */
    VertexAttributes getAttributes();

    /**
     * Sets the vertices of this InstanceData, discarding the old vertex data. The count must equal the number of floats per
     * vertex times the number of vertices to be copied to this VertexData. The order of the vertex attributes must be the same as
     * specified at construction time via {@link VertexAttributes}.
     * <p>
     * This can be called in between calls to bind and unbind. The vertex data will be updated instantly.
     *
     * @param data   the instance data
     * @param offset the offset to start copying the data from
     * @param count  the number of floats to copy
     */
    void setInstanceData(float[] data, int offset, int count);

    /**
     * Update (a portion of) the vertices. Does not resize the backing buffer.
     *
     * @param data         the instance data
     * @param sourceOffset the offset to start copying the data from
     * @param count        the number of floats to copy
     */
    void updateInstanceData(int targetOffset, float[] data, int sourceOffset, int count);

    /**
     * Sets the vertices of this InstanceData, discarding the old vertex data. The count must equal the number of floats per
     * vertex times the number of vertices to be copied to this InstanceData. The order of the vertex attributes must be the same
     * as specified at construction time via {@link VertexAttributes}.
     * <p>
     * This can be called in between calls to bind and unbind. The vertex data will be updated instantly.
     *
     * @param data  the instance data
     * @param count the number of floats to copy
     */
    void setInstanceData(FloatBuffer data, int count);

    /**
     * Update (a portion of) the vertices. Does not resize the backing buffer.
     *
     * @param data         the vertex data
     * @param sourceOffset the offset to start copying the data from
     * @param count        the number of floats to copy
     */
    void updateInstanceData(int targetOffset, FloatBuffer data, int sourceOffset, int count);

    /**
     * Returns the underlying FloatBuffer and marks it as dirty, causing the buffer contents to be uploaded on the next call to
     * bind. If you need immediate uploading use {@link #setInstanceData(float[], int, int)}; Any modifications made to the Buffer
     * *after* the call to bind will not automatically be uploaded.
     *
     * @return the underlying FloatBuffer holding the vertex data.
     * @deprecated use {@link #getBuffer(boolean)} instead
     */
    @Deprecated
    FloatBuffer getBuffer();

    /**
     * Returns the underlying FloatBuffer for reading or writing.
     *
     * @param forWriting when true, the underlying buffer will be uploaded on the next call to bind. If you need immediate
     *                   uploading use {@link #setInstanceData(float[], int, int)}.
     * @return the underlying FloatBuffer holding the vertex data.
     */
    FloatBuffer getBuffer(boolean forWriting);

    /**
     * Binds this InstanceData for rendering via glDrawArraysInstanced or glDrawElementsInstanced.
     */
    void bind(ShaderProgram shader);

    /**
     * Binds this InstanceData for rendering via glDrawArraysInstanced or glDrawElementsInstanced.
     *
     * @param locations array containing the attribute locations.
     */
    void bind(ShaderProgram shader, int[] locations);

    /**
     * Unbinds this InstanceData.
     */
    void unbind(ShaderProgram shader);

    /**
     * Unbinds this InstanceData.
     *
     * @param locations array containing the attribute locations.
     */
    void unbind(ShaderProgram shader, int[] locations);

    /**
     * Invalidates the InstanceData if applicable. Use this in case of a context loss.
     */
    void invalidate();

    /**
     * Disposes this InstanceData and all its associated OpenGL resources.
     */
    void dispose();
}
