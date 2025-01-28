package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.tests.utils.GdxTest;

public class AudioRecorderTest extends GdxTest {
    short[] samples = new short[1024 * 4];
    AudioDevice device;
    AudioRecorder recorder;

    @Override
    public void create() {
        device = Gdx.audio.newAudioDevice(44100, true);
        recorder = Gdx.audio.newAudioRecorder(44100, true);

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    recorder.read(samples, 0, samples.length);
                    device.writeSamples(samples, 0, samples.length);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void pause() {
        device.dispose();
        recorder.dispose();
    }

    @Override
    public void resume() {
        device = Gdx.audio.newAudioDevice(44100, true);
        recorder = Gdx.audio.newAudioRecorder(44100, true);
    }
}
