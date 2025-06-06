package com.badlogic.gdx.backends.lwjgl.audio;

import static org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED;
import static org.lwjgl.openal.AL10.AL_BUFFERS_QUEUED;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_INVALID_VALUE;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSource3f;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceQueueBuffers;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourceUnqueueBuffers;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 *
 */
public abstract class OpenALMusic implements Music {
    static private final int bufferSize = 4096 * 10;
    static private final int bufferCount = 3;
    static private final byte[] tempBytes = new byte[bufferSize];
    static private final ByteBuffer tempBuffer = BufferUtils.createByteBuffer(bufferSize);
    protected final FileHandle file;
    private final OpenALLwjglAudio audio;
    private final FloatArray renderedSecondsQueue = new FloatArray(bufferCount);
    private IntBuffer buffers;
    private int sourceID = -1;
    private int format, sampleRate;
    private boolean isLooping, isPlaying;
    private float volume = 1;
    private float pan = 0;
    private float renderedSeconds, maxSecondsPerBuffer;
    private OnCompletionListener onCompletionListener;

    public OpenALMusic(OpenALLwjglAudio audio, FileHandle file) {
        this.audio = audio;
        this.file = file;
        this.onCompletionListener = null;
    }

    /**
     * Prepare our music for playback!
     *
     * @param channels   The number of channels for the music. Most commonly 1 (for mono) or 2 (for stereo).
     * @param bitDepth   The number of bits in each sample. Normally 16. Can also be 8, 32 or sometimes 64.
     * @param sampleRate The number of samples to be played each second. Commonly 44100; can be anything within reason.
     */
    protected void setup(int channels, int bitDepth, int sampleRate) {
        this.format = channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
        if (bitDepth == 8) this.format--; // Use 8-bit AL_FORMAT instead.
        this.sampleRate = sampleRate;
        maxSecondsPerBuffer = (float) bufferSize / (bitDepth / 8 * channels * sampleRate);
    }

    public void play() {
        if (audio.noDevice) return;
        if (sourceID == -1) {
            sourceID = audio.obtainSource(true);
            if (sourceID == -1) return;

            audio.music.add(this);

            if (buffers == null) {
                buffers = BufferUtils.createIntBuffer(bufferCount);
                alGetError();
                alGenBuffers(buffers);
                int errorCode = alGetError();
                if (errorCode != AL_NO_ERROR)
                    throw new GdxRuntimeException("Unable to allocate audio buffers. AL Error: " + errorCode);
            }
            alSourcei(sourceID, AL_LOOPING, AL_FALSE);
            setPan(pan, volume);

            alGetError();

            boolean filled = false; // Check if there's anything to actually play.
            for (int i = 0; i < bufferCount; i++) {
                int bufferID = buffers.get(i);
                if (!fill(bufferID)) break;
                filled = true;
                alSourceQueueBuffers(sourceID, bufferID);
            }
            if (!filled && onCompletionListener != null) onCompletionListener.onCompletion(this);

            if (alGetError() != AL_NO_ERROR) {
                stop();
                return;
            }
        }
        if (!isPlaying) {
            alSourcePlay(sourceID);
            isPlaying = true;
        }
    }

    public void stop() {
        if (audio.noDevice) return;
        if (sourceID == -1) return;
        audio.music.removeValue(this, true);
        reset();
        audio.freeSource(sourceID);
        sourceID = -1;
        renderedSeconds = 0;
        renderedSecondsQueue.clear();
        isPlaying = false;
    }

    public void pause() {
        if (audio.noDevice) return;
        if (sourceID != -1) alSourcePause(sourceID);
        isPlaying = false;
    }

    public boolean isPlaying() {
        if (audio.noDevice) return false;
        if (sourceID == -1) return false;
        return isPlaying;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }

    public float getVolume() {
        return this.volume;
    }

    /**
     * @param volume Must be > 0.
     */
    public void setVolume(float volume) {
        if (volume < 0) throw new IllegalArgumentException("volume cannot be < 0: " + volume);
        this.volume = volume;
        if (audio.noDevice) return;
        if (sourceID != -1) alSourcef(sourceID, AL_GAIN, volume);
    }

    public void setPan(float pan, float volume) {
        this.volume = volume;
        this.pan = pan;
        if (audio.noDevice) return;
        if (sourceID == -1) return;
        alSource3f(sourceID, AL_POSITION, MathUtils.cos((pan - 1) * MathUtils.HALF_PI), 0,
                MathUtils.sin((pan + 1) * MathUtils.HALF_PI));
        alSourcef(sourceID, AL_GAIN, volume);
    }

    public float getPosition() {
        if (audio.noDevice) return 0;
        if (sourceID == -1) return 0;
        return renderedSeconds + alGetSourcef(sourceID, AL11.AL_SEC_OFFSET);
    }

    public void setPosition(float position) {
        if (audio.noDevice) return;
        if (sourceID == -1) return;
        boolean wasPlaying = isPlaying;
        isPlaying = false;
        alSourceStop(sourceID);
        alSourceUnqueueBuffers(sourceID, buffers);
        while (renderedSecondsQueue.size > 0) {
            renderedSeconds = renderedSecondsQueue.pop();
        }
        if (position <= renderedSeconds) {
            reset();
            renderedSeconds = 0;
        }
        while (renderedSeconds < (position - maxSecondsPerBuffer)) {
            int length = read(tempBytes);
            if (length <= 0) break;
            float currentBufferSeconds = maxSecondsPerBuffer * (float) length / (float) bufferSize;
            renderedSeconds += currentBufferSeconds;
        }
        renderedSecondsQueue.add(renderedSeconds);
        boolean filled = false;
        for (int i = 0; i < bufferCount; i++) {
            int bufferID = buffers.get(i);
            if (!fill(bufferID)) break;
            filled = true;
            alSourceQueueBuffers(sourceID, bufferID);
        }
        renderedSecondsQueue.pop();
        if (!filled) {
            stop();
            if (onCompletionListener != null) onCompletionListener.onCompletion(this);
        }
        alSourcef(sourceID, AL11.AL_SEC_OFFSET, position - renderedSeconds);
        if (wasPlaying) {
            alSourcePlay(sourceID);
            isPlaying = true;
        }
    }

    /**
     * Fills as much of the buffer as possible and returns the number of bytes filled. Returns <= 0 to indicate the end of the
     * stream.
     */
    abstract public int read(byte[] buffer);

    /**
     * Resets the stream to the beginning.
     */
    abstract public void reset();

    /**
     * By default, does just the same as reset(). Used to add special behaviour in Ogg.Music.
     */
    protected void loop() {
        reset();
    }

    public int getChannels() {
        return format == AL_FORMAT_STEREO16 ? 2 : 1;
    }

    public int getRate() {
        return sampleRate;
    }

    public void update() {
        if (audio.noDevice) return;
        if (sourceID == -1) return;

        boolean end = false;
        int buffers = alGetSourcei(sourceID, AL_BUFFERS_PROCESSED);
        while (buffers-- > 0) {
            int bufferID = alSourceUnqueueBuffers(sourceID);
            if (bufferID == AL_INVALID_VALUE) break;
            if (renderedSecondsQueue.size > 0) renderedSeconds = renderedSecondsQueue.pop();
            if (end) continue;
            if (fill(bufferID))
                alSourceQueueBuffers(sourceID, bufferID);
            else
                end = true;
        }
        if (end && alGetSourcei(sourceID, AL_BUFFERS_QUEUED) == 0) {
            stop();
            if (onCompletionListener != null) onCompletionListener.onCompletion(this);
        }

        // A buffer underflow will cause the source to stop.
        if (isPlaying && alGetSourcei(sourceID, AL_SOURCE_STATE) != AL_PLAYING) alSourcePlay(sourceID);
    }

    private boolean fill(int bufferID) {
        ((Buffer) tempBuffer).clear();
        int length = read(tempBytes);
        if (length <= 0) {
            if (isLooping) {
                loop();
                length = read(tempBytes);
                if (length <= 0) return false;
                if (renderedSecondsQueue.size > 0) {
                    renderedSecondsQueue.set(0, 0);
                }
            } else
                return false;
        }
        float previousLoadedSeconds = renderedSecondsQueue.size > 0 ? renderedSecondsQueue.first() : 0;
        float currentBufferSeconds = maxSecondsPerBuffer * (float) length / (float) bufferSize;
        renderedSecondsQueue.insert(0, previousLoadedSeconds + currentBufferSeconds);

        ((Buffer) tempBuffer.put(tempBytes, 0, length)).flip();
        alBufferData(bufferID, format, tempBuffer, sampleRate);
        return true;
    }

    public void dispose() {
        stop();
        if (audio.noDevice) return;
        if (buffers == null) return;
        alDeleteBuffers(buffers);
        buffers = null;
        onCompletionListener = null;
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        onCompletionListener = listener;
    }

    public int getSourceId() {
        return sourceID;
    }
}
