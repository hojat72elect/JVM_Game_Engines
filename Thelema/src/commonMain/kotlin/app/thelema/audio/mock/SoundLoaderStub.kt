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


package app.thelema.audio.mock

import app.thelema.audio.ISoundLoader
import app.thelema.fs.IFile
import app.thelema.res.LoaderAdapter

/** The headless backend does its best to mock elements. This is intended to make code-sharing between
 * server and client as simple as possible.
 */
class SoundLoaderStub : ISoundLoader, LoaderAdapter() {
    override val duration: Float
        get() = 0f

    override val componentName: String
        get() = "SoundLoader"

    override fun loadBase(file: IFile) {}

    override fun play(volume: Float, pitch: Float, pan: Float, loop: Boolean): Int = 0
    override fun stopSound() {}
    override fun stopSound(soundId: Int) {}
    override fun pause() = Unit
    override fun resume() = Unit
    override fun destroy() = Unit
    override fun pause(soundId: Int) = Unit
    override fun resume(soundId: Int) = Unit
    override fun setLooping(soundId: Int, looping: Boolean) = Unit
    override fun setPitch(soundId: Int, pitch: Float) = Unit
    override fun setVolume(soundId: Int, volume: Float) = Unit
    override fun setPan(soundId: Int, pan: Float) = Unit
}