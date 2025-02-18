package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Modification of the {@link VertexBufferObjectSubData} class. Sets the glVertexAttribDivisor for every {@link VertexAttribute}
 * automatically.
 */
public class InstanceBufferObjectSubData implements InstanceData {

    final VertexAttributes attributes;
    final FloatBuffer buffer;
    final ByteBuffer byteBuffer;
    final boolean isDirect;
    final boolean isStatic;
    final int usage;
    int bufferHandle;
    boolean isDirty = false;
    boolean isBound = false;

    /**
     * Constructs a new interleaved InstanceBufferObject.
     *
     * @param isStatic           whether the vertex data is static.
     * @param numInstances       the maximum number of vertices
     * @param instanceAttributes the {@link VertexAttributes}.
     */
    public InstanceBufferObjectSubData(boolean isStatic, int numInstances, VertexAttribute... instanceAttributes) {
        this(isStatic, numInstances, new VertexAttributes(instanceAttributes));
    }

    /**
     * Constructs a new interleaved InstanceBufferObject.
     *
     * @param isStatic           whether the vertex data is static.
     * @param numInstances       the maximum number of vertices
     * @param instanceAttributes the {@link VertexAttribute}s.
     */
    public InstanceBufferObjectSubData(boolean isStatic, int numInstances, VertexAttributes instanceAttributes) {
        this.isStatic = isStatic;
        this.attributes = instanceAttributes;
        byteBuffer = BufferUtils.newByteBuffer(this.attributes.vertexSize * numInstances);
        isDirect = true;

        usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
        buffer = byteBuffer.asFloatBuffer();
        bufferHandle = createBufferObject();
        ((Buffer) buffer).flip();
        ((Buffer) byteBuffer).flip();
    }

    private int createBufferObject() {
        int result = Gdx.gl20.glGenBuffer();
        Gdx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, result);
        Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.capacity(), null, usage);
        Gdx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        return result;
    }

    @Override
    public VertexAttributes getAttributes() {
        return attributes;
    }

    /**
     * Effectively returns {@link #getNumInstances()}.
     *
     * @return number of instances in this buffer
     */
    @Override
    public int getNumInstances() {
        return buffer.limit() * 4 / attributes.vertexSize;
    }

    /**
     * Effectively returns {@link #getNumMaxInstances()}.
     *
     * @return maximum number of instances in this buffer
     */
    @Override
    public int getNumMaxInstances() {
        return byteBuffer.capacity() / attributes.vertexSize;
    }

    /**
     * @deprecated use {@link #getBuffer(boolean)} instead
     */
    @Override
    @Deprecated
    public FloatBuffer getBuffer() {
        isDirty = true;
        return buffer;
    }

    @Override
    public FloatBuffer getBuffer(boolean forWriting) {
        isDirty |= forWriting;
        return buffer;
    }

    private void bufferChanged() {
        if (isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(), null, usage);
            Gdx.gl20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, byteBuffer.limit(), byteBuffer);
            isDirty = false;
        }
    }

    @Override
    public void setInstanceData(float[] data, int offset, int count) {
        isDirty = true;
        if (isDirect) {
            BufferUtils.copy(data, byteBuffer, count, offset);
            ((Buffer) buffer).position(0);
            ((Buffer) buffer).limit(count);
        } else {
            ((Buffer) buffer).clear();
            buffer.put(data, offset, count);
            ((Buffer) buffer).flip();
            ((Buffer) byteBuffer).position(0);
            ((Buffer) byteBuffer).limit(buffer.limit() << 2);
        }

        bufferChanged();
    }

    @Override
    public void setInstanceData(FloatBuffer data, int count) {
        isDirty = true;
        if (isDirect) {
            BufferUtils.copy(data, byteBuffer, count);
            ((Buffer) buffer).position(0);
            ((Buffer) buffer).limit(count);
        } else {
            ((Buffer) buffer).clear();
            buffer.put(data);
            ((Buffer) buffer).flip();
            ((Buffer) byteBuffer).position(0);
            ((Buffer) byteBuffer).limit(buffer.limit() << 2);
        }

        bufferChanged();
    }

    @Override
    public void updateInstanceData(int targetOffset, float[] data, int sourceOffset, int count) {
        isDirty = true;
        if (isDirect) {
            final int pos = byteBuffer.position();
            ((Buffer) byteBuffer).position(targetOffset * 4);
            BufferUtils.copy(data, sourceOffset, count, byteBuffer);
            ((Buffer) byteBuffer).position(pos);
        } else
            throw new GdxRuntimeException("Buffer must be allocated direct."); // Should never happen

        bufferChanged();
    }

    @Override
    public void updateInstanceData(int targetOffset, FloatBuffer data, int sourceOffset, int count) {
        isDirty = true;
        if (isDirect) {
            final int pos = byteBuffer.position();
            ((Buffer) byteBuffer).position(targetOffset * 4);
            ((Buffer) data).position(sourceOffset * 4);
            BufferUtils.copy(data, byteBuffer, count);
            ((Buffer) byteBuffer).position(pos);
        } else
            throw new GdxRuntimeException("Buffer must be allocated direct."); // Should never happen

        bufferChanged();
    }

    /**
     * Binds this InstanceBufferObject for rendering via glDrawArraysInstanced or glDrawElementsInstanced
     *
     * @param shader the shader
     */
    @Override
    public void bind(final ShaderProgram shader) {
        bind(shader, null);
    }

    @Override
    public void bind(final ShaderProgram shader, final int[] locations) {
        final GL20 gl = Gdx.gl20;

        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
        if (isDirty) {
            ((Buffer) byteBuffer).limit(buffer.limit() * 4);
            gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);
            isDirty = false;
        }

        final int numAttributes = attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; i++) {
                final VertexAttribute attribute = attributes.get(i);
                final int location = shader.getAttributeLocation(attribute.alias);
                if (location < 0) continue;
                int unitOffset = +attribute.unit;
                shader.enableVertexAttribute(location + unitOffset);

                shader.setVertexAttribute(location + unitOffset, attribute.numComponents, attribute.type, attribute.normalized,
                        attributes.vertexSize, attribute.offset);
                Gdx.gl30.glVertexAttribDivisor(location + unitOffset, 1);
            }
        } else {
            for (int i = 0; i < numAttributes; i++) {
                final VertexAttribute attribute = attributes.get(i);
                final int location = locations[i];
                if (location < 0) continue;
                int unitOffset = +attribute.unit;
                shader.enableVertexAttribute(location + unitOffset);

                shader.setVertexAttribute(location + unitOffset, attribute.numComponents, attribute.type, attribute.normalized,
                        attributes.vertexSize, attribute.offset);
                Gdx.gl30.glVertexAttribDivisor(location + unitOffset, 1);
            }
        }
        isBound = true;
    }

    /**
     * Unbinds this InstanceBufferObject.
     *
     * @param shader the shader
     */
    @Override
    public void unbind(final ShaderProgram shader) {
        unbind(shader, null);
    }

    @Override
    public void unbind(final ShaderProgram shader, final int[] locations) {
        final GL20 gl = Gdx.gl20;
        final int numAttributes = attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; i++) {
                final VertexAttribute attribute = attributes.get(i);
                final int location = shader.getAttributeLocation(attribute.alias);
                if (location < 0) continue;
                int unitOffset = +attribute.unit;
                shader.disableVertexAttribute(location + unitOffset);
            }
        } else {
            for (int i = 0; i < numAttributes; i++) {
                final VertexAttribute attribute = attributes.get(i);
                final int location = locations[i];
                if (location < 0) continue;
                int unitOffset = +attribute.unit;
                shader.enableVertexAttribute(location + unitOffset);
            }
        }
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        isBound = false;
    }

    /**
     * Invalidates the InstanceBufferObject so a new OpenGL buffer handle is created. Use this in case of a context loss.
     */
    public void invalidate() {
        bufferHandle = createBufferObject();
        isDirty = true;
    }

    /**
     * Disposes of all resources this InstanceBufferObject uses.
     */
    @Override
    public void dispose() {
        GL20 gl = Gdx.gl20;
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glDeleteBuffer(bufferHandle);
        bufferHandle = 0;
    }

    /**
     * Returns the InstanceBufferObject handle
     *
     * @return the InstanceBufferObject handle
     */
    public int getBufferHandle() {
        return bufferHandle;
    }
}
