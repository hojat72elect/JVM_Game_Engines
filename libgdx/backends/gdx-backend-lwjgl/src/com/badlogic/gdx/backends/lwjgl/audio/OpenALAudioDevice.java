package com.badlogic.gdx.backends.lwjgl.audio;

import static org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_INVALID_VALUE;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceQueueBuffers;
import static org.lwjgl.openal.AL10.alSourceUnqueueBuffers;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 *
 */
public class OpenALAudioDevice implements AudioDevice {
    static private final int bytesPerSample = 2;

    private final OpenALLwjglAudio audio;
    private final int channels;
    private final int bufferSize;
    private final int bufferCount;
    private final ByteBuffer tempBuffer;
    private IntBuffer buffers;
    private int sourceID = -1;
    private final int format;
    private final int sampleRate;
    private boolean isPlaying;
    private float volume = 1;
    private float renderedSeconds;
    private final float secondsPerBuffer;
    private byte[] bytes;

    public OpenALAudioDevice(OpenALLwjglAudio audio, int sampleRate, boolean isMono, int bufferSize, int bufferCount) {
        this.audio = audio;
        channels = isMono ? 1 : 2;
        this.bufferSize = bufferSize;
        this.bufferCount = bufferCount;
        this.format = channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
        this.sampleRate = sampleRate;
        secondsPerBuffer = (float) bufferSize / bytesPerSample / channels / sampleRate;
        tempBuffer = BufferUtils.createByteBuffer(bufferSize);
    }

    public void writeSamples(short[] samples, int offset, int numSamples) {
        if (bytes == null || bytes.length < numSamples * 2) bytes = new byte[numSamples * 2];
        int end = Math.min(offset + numSamples, samples.length);
        for (int i = offset, ii = 0; i < end; i++) {
            short sample = samples[i];
            bytes[ii++] = (byte) (sample & 0xFF);
            bytes[ii++] = (byte) ((sample >> 8) & 0xFF);
        }
        writeSamples(bytes, 0, numSamples * 2);
    }

    public void writeSamples(float[] samples, int offset, int numSamples) {
        if (bytes == null || bytes.length < numSamples * 2) bytes = new byte[numSamples * 2];
        int end = Math.min(offset + numSamples, samples.length);
        for (int i = offset, ii = 0; i < end; i++) {
            float floatSample = samples[i];
            floatSample = MathUtils.clamp(floatSample, -1f, 1f);
            int intSample = (int) (floatSample * 32767);
            bytes[ii++] = (byte) (intSample & 0xFF);
            bytes[ii++] = (byte) ((intSample >> 8) & 0xFF);
        }
        writeSamples(bytes, 0, numSamples * 2);
    }

    public void writeSamples(byte[] data, int offset, int length) {
        if (length < 0) throw new IllegalArgumentException("length cannot be < 0.");

        if (sourceID == -1) {
            sourceID = audio.obtainSource(true);
            if (sourceID == -1) return;
            if (buffers == null) {
                buffers = BufferUtils.createIntBuffer(bufferCount);
                alGetError();
                alGenBuffers(buffers);
                if (alGetError() != AL_NO_ERROR) throw new GdxRuntimeException("Unable to allocate audio buffers.");
            }
            alSourcei(sourceID, AL_LOOPING, AL_FALSE);
            alSourcef(sourceID, AL_GAIN, volume);
            // Fill initial buffers.
            for (int i = 0; i < bufferCount; i++) {
                int bufferID = buffers.get(i);
                int written = Math.min(bufferSize, length);
                ((Buffer) tempBuffer).clear();
                ((Buffer) tempBuffer.put(data, offset, written)).flip();
                alBufferData(bufferID, format, tempBuffer, sampleRate);
                alSourceQueueBuffers(sourceID, bufferID);
                length -= written;
                offset += written;
            }
            alSourcePlay(sourceID);
            isPlaying = true;
        }

        while (length > 0) {
            int written = fillBuffer(data, offset, length);
            length -= written;
            offset += written;
        }
    }

    /**
     * Blocks until some of the data could be buffered.
     */
    private int fillBuffer(byte[] data, int offset, int length) {
        int written = Math.min(bufferSize, length);

        outer:
        while (true) {
            int buffers = alGetSourcei(sourceID, AL_BUFFERS_PROCESSED);
            while (buffers-- > 0) {
                int bufferID = alSourceUnqueueBuffers(sourceID);
                if (bufferID == AL_INVALID_VALUE) break;
                renderedSeconds += secondsPerBuffer;

                ((Buffer) tempBuffer).clear();
                ((Buffer) tempBuffer.put(data, offset, written)).flip();
                alBufferData(bufferID, format, tempBuffer, sampleRate);

                alSourceQueueBuffers(sourceID, bufferID);
                break outer;
            }
            // Wait for buffer to be free.
            try {
                Thread.sleep((long) (1000 * secondsPerBuffer));
            } catch (InterruptedException ignored) {
            }
        }

        // A buffer underflow will cause the source to stop.
        if (!isPlaying || alGetSourcei(sourceID, AL_SOURCE_STATE) != AL_PLAYING) {
            alSourcePlay(sourceID);
            isPlaying = true;
        }

        return written;
    }

    public void stop() {
        if (sourceID == -1) return;
        audio.freeSource(sourceID);
        sourceID = -1;
        renderedSeconds = 0;
        isPlaying = false;
    }

    public boolean isPlaying() {
        if (sourceID == -1) return false;
        return isPlaying;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (sourceID != -1) alSourcef(sourceID, AL_GAIN, volume);
    }

    public float getPosition() {
        if (sourceID == -1) return 0;
        return renderedSeconds + alGetSourcef(sourceID, AL11.AL_SEC_OFFSET);
    }

    public void setPosition(float position) {
        renderedSeconds = position;
    }

    public int getChannels() {
        return format == AL_FORMAT_STEREO16 ? 2 : 1;
    }

    public int getRate() {
        return sampleRate;
    }

    public void dispose() {
        if (buffers == null) return;
        if (sourceID != -1) {
            audio.freeSource(sourceID);
            sourceID = -1;
        }
        alDeleteBuffers(buffers);
        buffers = null;
    }

    public boolean isMono() {
        return channels == 1;
    }

    public int getLatency() {
        return (int) ((float) bufferSize / bytesPerSample / channels * bufferCount);
    }

    @Override
    public void pause() {
        // A buffer underflow will cause the source to stop.
    }

    @Override
    public void resume() {
        // Automatically resumes when samples are written
    }
}
