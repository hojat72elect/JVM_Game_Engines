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

package app.thelema.audio

import app.thelema.audio.mock.AudioStub
import app.thelema.fs.IFile

/** This interface encapsulates the creation and management of audio resources. It allows you to get direct access to the audio
 * hardware via the [IAudioDevice] and [IAudioRecorder] interfaces, create sound effects via the [ISoundLoader] interface
 * and play music streams via the [IMusic] interface.
 *
 *
 *
 * All resources created via this interface have to be disposed as soon as they are no longer used.
 *
 *
 * @author mzechner
 */
interface IAudio {
    /** Creates a new [IAudioDevice] either in mono or stereo mode. The AudioDevice has to be disposed via its
     * [IAudioDevice.dispose] method when it is no longer used.
     *
     * @param samplingRate the sampling rate.
     * @param channelsNum for mono 1, for stereo 2
     * @return the AudioDevice
     *
     * @throws RuntimeException in case the device could not be created
     */
    fun newAudioDevice(samplingRate: Int, channelsNum: Int): IAudioDevice

    /** Creates a new [IAudioRecorder]. The AudioRecorder has to be disposed after it is no longer used.
     *
     * @param samplingRate the sampling rate in Hertz
     * @param isMono whether the recorder records in mono or stereo
     * @return the AudioRecorder
     *
     * @throws RuntimeException in case the recorder could not be created
     */
    fun newAudioRecorder(samplingRate: Int, isMono: Boolean): IAudioRecorder


    /** Creates a new [IMusic] instance which is used to play back a music stream from a file. Currently supported formats are
     * WAV, MP3 and OGG. The Music instance has to be disposed if it is no longer used via the [IMusic.destroy] method.
     *
     * @param file the FileHandle
     * @return the new Music or null if the Music could not be loaded
     * @throws RuntimeException in case the music could not be loaded
     */
    fun newMusic(file: IFile): IMusic

    fun getVersion(param: Int): String

    fun update()

    fun destroy()
}

var AL: IAudio = AudioStub()
