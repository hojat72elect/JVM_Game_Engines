package com.badlogic.gdx.backends.android;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * {@link AudioRecorder} implementation for the android system based on AudioRecord
 */
public class AndroidAudioRecorder implements AudioRecorder {
    /**
     * the audio track we read samples from
     **/
    private final AudioRecord recorder;

    @SuppressLint("MissingPermission")
    public AndroidAudioRecorder(int samplingRate, boolean isMono) {
        int channelConfig = isMono ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
        int minBufferSize = AudioRecord.getMinBufferSize(samplingRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, samplingRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize);
        if (recorder.getState() != AudioRecord.STATE_INITIALIZED)
            throw new GdxRuntimeException("Unable to initialize AudioRecorder.\nDo you have the RECORD_AUDIO permission?");
        recorder.startRecording();
    }

    @Override
    public void dispose() {
        recorder.stop();
        recorder.release();
    }

    @Override
    public void read(short[] samples, int offset, int numSamples) {
        int read = 0;
        while (read != numSamples) {
            read += recorder.read(samples, offset + read, numSamples - read);
        }
    }
}
