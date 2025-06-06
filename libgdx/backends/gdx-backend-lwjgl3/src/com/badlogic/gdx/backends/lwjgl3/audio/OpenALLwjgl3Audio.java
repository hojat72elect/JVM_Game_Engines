package com.badlogic.gdx.backends.lwjgl3.audio;

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_PAUSED;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_STOPPED;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alDisable;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alListenerfv;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.EXTDisconnect.ALC_CONNECTED;
import static org.lwjgl.openal.EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.SOFTDirectChannels;
import org.lwjgl.openal.SOFTDirectChannelsRemix;
import org.lwjgl.openal.SOFTReopenDevice;
import org.lwjgl.openal.SOFTXHoldOnDisconnect;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class OpenALLwjgl3Audio implements Lwjgl3Audio {
    private final int deviceBufferSize;
    private final int deviceBufferCount;
    Array<OpenALMusic> music = new Array(false, 1, OpenALMusic.class);
    long device;
    long context;
    boolean noDevice = false;
    private IntArray idleSources, allSources;
    private LongMap<Integer> soundIdToSource;
    private IntMap<Long> sourceToSoundId;
    private long nextSoundId = 0;
    private final ObjectMap<String, Class<? extends OpenALSound>> extensionToSoundClass = new ObjectMap();
    private final ObjectMap<String, Class<? extends OpenALMusic>> extensionToMusicClass = new ObjectMap();
    private OpenALSound[] recentSounds;
    private int mostRecetSound = -1;
    private String preferredOutputDevice = null;
    private Thread observerThread;

    public OpenALLwjgl3Audio() {
        this(16, 9, 512);
    }

    public OpenALLwjgl3Audio(int simultaneousSources, int deviceBufferCount, int deviceBufferSize) {
        this.deviceBufferSize = deviceBufferSize;
        this.deviceBufferCount = deviceBufferCount;

        registerSound("ogg", Ogg.Sound.class);
        registerMusic("ogg", Ogg.Music.class);
        registerSound("wav", Wav.Sound.class);
        registerMusic("wav", Wav.Music.class);
        registerSound("mp3", Mp3.Sound.class);
        registerMusic("mp3", Mp3.Music.class);

        device = alcOpenDevice((ByteBuffer) null);
        if (device == 0L) {
            noDevice = true;
            return;
        }
        ALCCapabilities deviceCapabilities = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer) null);
        if (context == 0L) {
            alcCloseDevice(device);
            noDevice = true;
            return;
        }
        if (!alcMakeContextCurrent(context)) {
            noDevice = true;
            return;
        }
        AL.createCapabilities(deviceCapabilities);

        alGetError();
        allSources = new IntArray(false, simultaneousSources);
        for (int i = 0; i < simultaneousSources; i++) {
            int sourceID = alGenSources();
            if (alGetError() != AL_NO_ERROR) break;
            allSources.add(sourceID);
        }
        idleSources = new IntArray(allSources);
        soundIdToSource = new LongMap<>();
        sourceToSoundId = new IntMap<>();

        FloatBuffer orientation = BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
        ((Buffer) orientation).flip();
        alListenerfv(AL_ORIENTATION, orientation);
        FloatBuffer velocity = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
        ((Buffer) velocity).flip();
        alListenerfv(AL_VELOCITY, velocity);
        FloatBuffer position = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
        ((Buffer) position).flip();
        alListenerfv(AL_POSITION, position);

        alDisable(SOFTXHoldOnDisconnect.AL_STOP_SOURCES_ON_DISCONNECT_SOFT);
        observerThread = new Thread(new Runnable() {

            private String[] lastAvailableDevices = new String[0];

            @Override
            public void run() {
                while (true) {
                    boolean isConnected = alcGetInteger(device, ALC_CONNECTED) != 0;
                    if (!isConnected) {
                        // The device is at a state where it can't recover
                        // This is usually the windows path on removing a device
                        switchOutputDevice(null, false);
                        continue;
                    }
                    if (preferredOutputDevice != null) {
                        if (Arrays.asList(getAvailableOutputDevices()).contains(preferredOutputDevice)) {
                            if (!preferredOutputDevice.equals(alcGetString(device, ALC_ALL_DEVICES_SPECIFIER))) {
                                // The preferred output device is reconnected, let's switch back to it
                                switchOutputDevice(preferredOutputDevice);
                            }
                        } else {
                            // This is usually the mac/linux path
                            if (preferredOutputDevice.equals(alcGetString(device, ALC_ALL_DEVICES_SPECIFIER))) {
                                // The preferred output device is reconnected, let's switch back to it
                                switchOutputDevice(null, false);
                            }
                        }
                    } else {
                        String[] currentDevices = getAvailableOutputDevices();
                        // If a new device got added, re evaluate "auto" mode
                        if (!Arrays.equals(currentDevices, lastAvailableDevices)) {
                            switchOutputDevice(null);
                        }
                        // Update last available devices
                        lastAvailableDevices = currentDevices;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                        return;
                    }
                }
            }
        });
        observerThread.setDaemon(true);
        observerThread.start();

        recentSounds = new OpenALSound[simultaneousSources];
    }

    public void registerSound(String extension, Class<? extends OpenALSound> soundClass) {
        if (extension == null) throw new IllegalArgumentException("extension cannot be null.");
        if (soundClass == null) throw new IllegalArgumentException("soundClass cannot be null.");
        extensionToSoundClass.put(extension, soundClass);
    }

    public void registerMusic(String extension, Class<? extends OpenALMusic> musicClass) {
        if (extension == null) throw new IllegalArgumentException("extension cannot be null.");
        if (musicClass == null) throw new IllegalArgumentException("musicClass cannot be null.");
        extensionToMusicClass.put(extension, musicClass);
    }

    public OpenALSound newSound(FileHandle file) {
        String extension = file.extension().toLowerCase();
        return newSound(file, extension);
    }

    public OpenALSound newSound(FileHandle file, String extension) {
        if (file == null) throw new IllegalArgumentException("file cannot be null.");
        Class<? extends OpenALSound> soundClass = extensionToSoundClass.get(extension);
        if (soundClass == null) throw new GdxRuntimeException("Unknown file extension for sound: " + file);
        try {
            OpenALSound sound = soundClass.getConstructor(new Class[]{OpenALLwjgl3Audio.class, FileHandle.class}).newInstance(this,
                    file);
            if (sound.getType() != null && !sound.getType().equals(extension)) {
                return newSound(file, sound.getType());
            }
            return sound;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error creating sound " + soundClass.getName() + " for file: " + file, ex);
        }
    }

    public OpenALMusic newMusic(FileHandle file) {
        if (file == null) throw new IllegalArgumentException("file cannot be null.");
        Class<? extends OpenALMusic> musicClass = extensionToMusicClass.get(file.extension().toLowerCase());
        if (musicClass == null) throw new GdxRuntimeException("Unknown file extension for music: " + file);
        try {
            return musicClass.getConstructor(new Class[]{OpenALLwjgl3Audio.class, FileHandle.class}).newInstance(this, file);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error creating music " + musicClass.getName() + " for file: " + file, ex);
        }
    }

    @Override
    public boolean switchOutputDevice(String deviceIdentifier) {
        return switchOutputDevice(deviceIdentifier, true);
    }

    private boolean switchOutputDevice(String deviceIdentifier, boolean setPreferred) {
        if (setPreferred) {
            preferredOutputDevice = deviceIdentifier;
        }
        return SOFTReopenDevice.alcReopenDeviceSOFT(device, deviceIdentifier, (IntBuffer) null);
    }

    @Override
    public String[] getAvailableOutputDevices() {
        List<String> devices = ALUtil.getStringList(0, ALC_ALL_DEVICES_SPECIFIER);
        if (devices == null) return new String[0];
        return devices.toArray(new String[0]);
    }

    int obtainSource(boolean isMusic) {
        if (noDevice) return 0;
        for (int i = 0, n = idleSources.size; i < n; i++) {
            int sourceId = idleSources.get(i);
            int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
            if (state != AL_PLAYING && state != AL_PAUSED) {
                Long oldSoundId = sourceToSoundId.remove(sourceId);
                if (oldSoundId != null) soundIdToSource.remove(oldSoundId);
                if (isMusic) {
                    idleSources.removeIndex(i);
                } else {
                    long soundId = nextSoundId++;
                    sourceToSoundId.put(sourceId, soundId);
                    soundIdToSource.put(soundId, sourceId);
                }
                alSourceStop(sourceId);
                alSourcei(sourceId, AL_BUFFER, 0);
                AL10.alSourcef(sourceId, AL10.AL_GAIN, 1);
                AL10.alSourcef(sourceId, AL10.AL_PITCH, 1);
                AL10.alSource3f(sourceId, AL10.AL_POSITION, 0, 0, 1f);
                AL10.alSourcei(sourceId, SOFTDirectChannels.AL_DIRECT_CHANNELS_SOFT, SOFTDirectChannelsRemix.AL_REMIX_UNMATCHED_SOFT);
                return sourceId;
            }
        }
        return -1;
    }

    void freeSource(int sourceID) {
        if (noDevice) return;
        alSourceStop(sourceID);
        alSourcei(sourceID, AL_BUFFER, 0);
        Long soundId = sourceToSoundId.remove(sourceID);
        if (soundId != null) soundIdToSource.remove(soundId);
        idleSources.add(sourceID);
    }

    void freeBuffer(int bufferID) {
        if (noDevice) return;
        for (int i = 0, n = idleSources.size; i < n; i++) {
            int sourceID = idleSources.get(i);
            if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
                Long soundId = sourceToSoundId.remove(sourceID);
                if (soundId != null) soundIdToSource.remove(soundId);
                alSourceStop(sourceID);
                alSourcei(sourceID, AL_BUFFER, 0);
            }
        }
    }

    void stopSourcesWithBuffer(int bufferID) {
        if (noDevice) return;
        for (int i = 0, n = idleSources.size; i < n; i++) {
            int sourceID = idleSources.get(i);
            if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
                Long soundId = sourceToSoundId.remove(sourceID);
                if (soundId != null) soundIdToSource.remove(soundId);
                alSourceStop(sourceID);
            }
        }
    }

    void pauseSourcesWithBuffer(int bufferID) {
        if (noDevice) return;
        for (int i = 0, n = idleSources.size; i < n; i++) {
            int sourceID = idleSources.get(i);
            if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) alSourcePause(sourceID);
        }
    }

    void resumeSourcesWithBuffer(int bufferID) {
        if (noDevice) return;
        for (int i = 0, n = idleSources.size; i < n; i++) {
            int sourceID = idleSources.get(i);
            if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
                if (alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PAUSED) alSourcePlay(sourceID);
            }
        }
    }

    @Override
    public void update() {
        if (noDevice) return;
        for (int i = 0; i < music.size; i++)
            music.items[i].update();
    }

    public long getSoundId(int sourceId) {
        Long soundId = sourceToSoundId.get(sourceId);
        return soundId != null ? soundId : -1;
    }

    public int getSourceId(long soundId) {
        Integer sourceId = soundIdToSource.get(soundId);
        return sourceId != null ? sourceId : -1;
    }

    public void stopSound(long soundId) {
        Integer sourceId = soundIdToSource.get(soundId);
        if (sourceId != null) alSourceStop(sourceId);
    }

    public void pauseSound(long soundId) {
        Integer sourceId = soundIdToSource.get(soundId);
        if (sourceId != null) alSourcePause(sourceId);
    }

    public void resumeSound(long soundId) {
        int sourceId = soundIdToSource.get(soundId, -1);
        if (sourceId != -1 && alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PAUSED) alSourcePlay(sourceId);
    }

    public void setSoundGain(long soundId, float volume) {
        Integer sourceId = soundIdToSource.get(soundId);
        if (sourceId != null) AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
    }

    public void setSoundLooping(long soundId, boolean looping) {
        Integer sourceId = soundIdToSource.get(soundId);
        if (sourceId != null) alSourcei(sourceId, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    public void setSoundPitch(long soundId, float pitch) {
        Integer sourceId = soundIdToSource.get(soundId);
        if (sourceId != null) AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
    }

    public void setSoundPan(long soundId, float pan, float volume) {
        int sourceId = soundIdToSource.get(soundId, -1);
        if (sourceId != -1) {
            AL10.alSource3f(sourceId, AL10.AL_POSITION, MathUtils.cos((pan - 1) * MathUtils.HALF_PI), 0,
                    MathUtils.sin((pan + 1) * MathUtils.HALF_PI));
            AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
        }
    }

    public void dispose() {
        if (noDevice) return;
        observerThread.interrupt();
        for (int i = 0, n = allSources.size; i < n; i++) {
            int sourceID = allSources.get(i);
            int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
            if (state != AL_STOPPED) alSourceStop(sourceID);
            alDeleteSources(sourceID);
        }

        sourceToSoundId = null;
        soundIdToSource = null;

        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    public AudioDevice newAudioDevice(int sampleRate, final boolean isMono) {
        if (noDevice) return new AudioDevice() {
            @Override
            public void writeSamples(float[] samples, int offset, int numSamples) {
            }

            @Override
            public void writeSamples(short[] samples, int offset, int numSamples) {
            }

            @Override
            public void setVolume(float volume) {
            }

            @Override
            public boolean isMono() {
                return isMono;
            }

            @Override
            public int getLatency() {
                return 0;
            }

            @Override
            public void dispose() {
            }

            @Override
            public void pause() {
            }

            @Override
            public void resume() {
            }
        };
        return new OpenALAudioDevice(this, sampleRate, isMono, deviceBufferSize, deviceBufferCount);
    }

    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        if (noDevice) return new AudioRecorder() {
            @Override
            public void read(short[] samples, int offset, int numSamples) {
            }

            @Override
            public void dispose() {
            }
        };
        return new JavaSoundAudioRecorder(samplingRate, isMono);
    }

    /**
     * Retains a list of the most recently played sounds and stops the sound played least recently if necessary for a new sound to
     * play
     */
    protected void retain(OpenALSound sound, boolean stop) {
        // Move the pointer ahead and wrap
        mostRecetSound++;
        mostRecetSound %= recentSounds.length;

        if (stop) {
            // Stop the least recent sound (the one we are about to bump off the buffer)
            if (recentSounds[mostRecetSound] != null) recentSounds[mostRecetSound].stop();
        }

        recentSounds[mostRecetSound] = sound;
    }

    /**
     * Removes the disposed sound from the least recently played list
     */
    public void forget(OpenALSound sound) {
        for (int i = 0; i < recentSounds.length; i++) {
            if (recentSounds[i] == sound) recentSounds[i] = null;
        }
    }
}
