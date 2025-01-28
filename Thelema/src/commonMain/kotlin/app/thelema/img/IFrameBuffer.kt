/*
 * Copyright 2020-2021 Anton Trushkov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.thelema.img

import app.thelema.ecs.ECS
import app.thelema.gl.*
import app.thelema.utils.LOG

/**
 * @author zeganstyl
 * */
interface IFrameBuffer {
    val width: Int
    val height: Int

    val attachments: List<IFrameBufferAttachment>

    var frameBufferHandle: Int

    var isBound: Boolean

    /** @param index attachment index */
    fun getTexture(index: Int) = attachments[index].texture!!

    fun addAttachment(attachment: IFrameBufferAttachment)

    fun removeAttachment(attachment: IFrameBufferAttachment)

    fun buildAttachments() {
        if (frameBufferHandle == 0) frameBufferHandle = GL.glGenFramebuffer()
        bind {
            attachments.forEach { attachment -> attachment.setup(this@IFrameBuffer) }

            // setup buffers order if MRT
            if (attachments.size > 1) {
                val attachment = attachments[1].attachment
                if (attachment != GL_DEPTH_ATTACHMENT &&
                        attachment != GL_STENCIL_ATTACHMENT &&
                        attachment != GL_DEPTH_STENCIL_ATTACHMENT) {
                    initBuffersOrder()
                }
            }
        }
    }

    /** You have to reimplement this and set width and height fields */
    fun setResolution(width: Int, height: Int) {
        if (frameBufferHandle != 0) {
            destroy()
            frameBufferHandle = GL.glGenFramebuffer()
            buildAttachments()
        }
    }

    /** Indices of [attachments]. For example (0, 1, 2)
     * Used for multi render target.
     * If no indices specified, all color attachments will be used in added order. */
    fun initBuffersOrder(vararg indices: Int) {
        if (frameBufferHandle == 0) throw RuntimeException("Frame buffer handle is 0")
        if (!isBound) throw RuntimeException("Frame buffer it not bound. You must use bind {}")

        if (indices.isEmpty()) {
            val indices2 = IntArray(attachments.size)
            var count = 0

            for (i in attachments.indices) {
                val attachment = attachments[i].attachment

                if (attachment != GL_DEPTH_ATTACHMENT && attachment != GL_STENCIL_ATTACHMENT && attachment != GL_DEPTH_STENCIL_ATTACHMENT) {
                    indices2[count] = attachment
                    count++
                }
            }

            if (count > 0) {
                GL.glDrawBuffers(count, indices2)
            }
        } else {
            val indicesBuffer = IntArray(indices.size)

            for (i in indices.indices) {
                indicesBuffer[i] = attachments[indices[i]].attachment
            }

            GL.glDrawBuffers(indices.size, indicesBuffer)
        }
    }

    /** Use for debug. */
    fun checkErrors() {
        val result = GL.glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (result != GL_FRAMEBUFFER_COMPLETE) {
            destroy()
            val errorText = "Frame buffer couldn't be constructed: ${when (result) {
                GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "incomplete attachment"
                GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS -> "incomplete dimensions"
                GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> "missing attachment"
                GL_FRAMEBUFFER_UNSUPPORTED -> "unsupported combination of formats"
                else -> "unknown error $result"
            }}"
            LOG.error(errorText)
        }
    }

    fun bind() {
        isBound = true
        GL.glBindFramebuffer(GL_FRAMEBUFFER, frameBufferHandle)
    }

    fun bindRead() {
        isBound = true
        GL.glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBufferHandle)
    }

    fun bindDraw() {
        isBound = true
        GL.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferHandle)
    }

    fun unbind() {
        GL.glBindFramebuffer(GL_FRAMEBUFFER, GL.mainFrameBufferHandle)
        isBound = false
    }

    fun renderBegin() {
        isBound = true
        GL.glBindFramebuffer(GL_FRAMEBUFFER, frameBufferHandle)
        GL.glViewport(0, 0, width, height)
    }

    fun renderEnd() {
        GL.glBindFramebuffer(GL_FRAMEBUFFER, GL.mainFrameBufferHandle)
        GL.glViewport(0, 0, GL.mainFrameBufferWidth, GL.mainFrameBufferHeight)
        isBound = false
    }

    /** Releases all resources associated with the FrameBuffer.  */
    fun destroy() {
        for (i in attachments.indices) {
            attachments[i].destroy()
        }

        GL.glDeleteFramebuffer(frameBufferHandle)
    }
}

/** Bind this, do [block] and unbind this.
 * Usually used for setting framebuffer parameters, attaching textures, etc.
 * For rendering you can use [render].
 * After all, it will bind default frame buffer. */
// Method implemented as extension for interface, so it can be inlined
inline fun IFrameBuffer.bind(block: IFrameBuffer.() -> Unit) {
    bind()
    block(this)
    unbind()
}

/** Like [bind], but also sets viewport. Before invoke [block], buffer will be cleared with glClear */
// Method implemented as extension for interface, so it can be inlined
inline fun IFrameBuffer.render(block: IFrameBuffer.() -> Unit) {
    renderBegin()
    GL.glClear()
    block(this)
    renderEnd()
}

fun IFrameBuffer.renderCurrentScene(shaderChannel: String? = null) {
    renderBegin()
    GL.glClear()
    ECS.render(shaderChannel)
    renderEnd()
}

/** Like [bind], but also sets viewport. No clearing with glClear, so you can do it b yourself. */
inline fun IFrameBuffer.renderNoClear(block: IFrameBuffer.() -> Unit) {
    renderBegin()
    block(this)
    renderEnd()
}