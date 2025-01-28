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

package app.thelema.gl

import app.thelema.data.IByteData
import app.thelema.utils.LOG

interface IGLBuffer {
    var bufferHandle: Int

    var bytes: IByteData

    /** If true, [uploadBufferToGpu] will be called on next binding */
    var gpuUploadRequested: Boolean

    var usage: Int

    val target: Int

    fun requestBufferUploading() {
        gpuUploadRequested = true
    }

    fun bind() {
        if (bufferHandle == 0) bufferHandle = GL.glGenBuffer()
        GL.glBindBuffer(target, bufferHandle)
        if (gpuUploadRequested) uploadBufferToGpu()
    }

    fun uploadBufferToGpu() {
        if (bytes.limit > 0) {
            if (bufferHandle == 0) bufferHandle = GL.glGenBuffer()
            GL.glBindBuffer(target, bufferHandle)
            GL.glBufferData(target, bytes.limit, bytes, usage)
        } else {
            LOG.error("$this uploadBufferToGpu(): buffer limit = 0 ")
        }
        gpuUploadRequested = false
    }

    /** Release all resources (in CPU and GPU memory) */
    fun destroy() {
        bytes.destroy()
        if (bufferHandle != 0) {
            GL.glBindBuffer(target, 0)
            GL.glDeleteBuffer(bufferHandle)
            bufferHandle = 0
        }
    }
}