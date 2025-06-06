package com.badlogic.gdx.backends.lwjgl.audio;

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
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alListener;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcei;

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
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import java.nio.Buffer;
import java.nio.FloatBuffer;

/**
 *
 */
public class OpenALLwjglAudio implements LwjglAudio {
    private final int deviceBufferSize;
    private final int deviceBufferCount;
    Array<OpenALMusic> music = new Array(false, 1, OpenALMusic.class);
    boolean noDevice = false;
    private IntArray idleSources, allSources;
    private LongMap<Integer> soundIdToSource;
    private IntMap<Long> sourceToSoundId;
    private long nextSoundId = 0;
    private final ObjectMap<String, Class<? extends OpenALSound>> extensionToSoundClass = new ObjectMap();
    private final ObjectMap<String, Class<? extends OpenALMusic>> extensionToMusicClass = new ObjectMap();
    private OpenALSound[] recentSounds;
    private int mostRecentSound = -1;

    public OpenALLwjglAudio() {
        this(16, 9, 512);
    }

    public OpenALLwjglAudio(int simultaneousSources, int deviceBufferCount, int deviceBufferSize) {
        this.deviceBufferSize = deviceBufferSize;
        this.deviceBufferCount = deviceBufferCount;

        registerSound("ogg", Ogg.Sound.class);
        registerMusic("ogg", Ogg.Music.class);
        registerSound("wav", Wav.Sound.class);
        registerMusic("wav", Wav.Music.class);
        registerSound("mp3", Mp3.Sound.class);
        registerMusic("mp3", Mp3.Music.class);

        try {
            AL.create();
        } catch (LWJGLException ex) {
            noDevice = true;
            ex.printStackTrace();
            return;
        }

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
        alListener(AL_ORIENTATION, orientation);
        FloatBuffer velocity = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
        ((Buffer) velocity).flip();
        alListener(AL_VELOCITY, velocity);
        FloatBuffer position = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
        ((Buffer) position).flip();
        alListener(AL_POSITION, position);

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
            OpenALSound sound = soundClass.getConstructor(new Class[]{OpenALLwjglAudio.class, FileHandle.class}).newInstance(this,
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
            return musicClass.getConstructor(new Class[]{OpenALLwjglAudio.class, FileHandle.class}).newInstance(this, file);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error creating music " + musicClass.getName() + " for file: " + file, ex);
        }
    }

    @Override
    public boolean switchOutputDevice(String deviceIdentifier) {
        return true;
    }

    @Override
    public String[] getAvailableOutputDevices() {
        return new String[0];
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
                return sourceId;
            }
        }
        return -1;
    }

    void freeSource(int sourceID) {
        if (noDevice) return;
        alGetError();
        alSourceStop(sourceID);
        int e = alGetError();
        if (e != AL_NO_ERROR) throw new GdxRuntimeException("AL Error: " + e);
        alSourcei(sourceID, AL_BUFFER, 0);
        e = alGetError();
        if (e != AL_NO_ERROR) throw new GdxRuntimeException("AL Error: " + e);
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

    @Override
    public void dispose() {
        if (noDevice) return;
        for (int i = 0, n = allSources.size; i < n; i++) {
            int sourceID = allSources.get(i);
            int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
            if (state != AL_STOPPED) alSourceStop(sourceID);
            alDeleteSources(sourceID);
        }

        sourceToSoundId = null;
        soundIdToSource = null;

        AL.destroy();
        while (AL.isCreated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
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
        mostRecentSound++;
        mostRecentSound %= recentSounds.length;

        if (stop) {
            // Stop the least recent sound (the one we are about to bump off the buffer)
            if (recentSounds[mostRecentSound] != null) recentSounds[mostRecentSound].stop();
        }

        recentSounds[mostRecentSound] = sound;
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
