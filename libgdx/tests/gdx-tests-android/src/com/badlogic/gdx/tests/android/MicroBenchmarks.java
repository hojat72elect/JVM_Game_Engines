package com.badlogic.gdx.tests.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

public class MicroBenchmarks extends Activity {
    final int TRIES = 5;
    long start = 0;
    ScrollView sv;
    TextView tv;
    Thread testThread = new Thread(() -> {
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocateDirect(1024 * 1024 * Float.SIZE / 8);
        buffer.order(java.nio.ByteOrder.nativeOrder());
        java.nio.FloatBuffer floatBuffer = buffer.asFloatBuffer();
        java.nio.IntBuffer intBuffer = buffer.asIntBuffer();

        float[] floatArray = new float[1024 * 1024];
        int[] intArray = new int[1024 * 1024];

        // single put
        tic();
        for (int tries = 0; tries < TRIES; tries++) {
            for (float v : floatArray) floatBuffer.put(v);
            floatBuffer.clear();
        }
        toc("single put");

        // single indexed put
        tic();
        for (int tries = 0; tries < TRIES; tries++) {
            for (int i = 0; i < floatArray.length; i++)
                floatBuffer.put(i, floatArray[i]);
            floatBuffer.clear();
        }
        toc("single indexed put");

        // bulk put
        tic();
        for (int tries = 0; tries < TRIES; tries++) {
            floatBuffer.put(floatArray);
            floatBuffer.clear();
        }
        toc("vector put");

        // convert bulk put
        tic();
        for (int tries = 0; tries < TRIES; tries++) {
            for (int i = 0; i < floatArray.length; i++)
                intArray[i] = Float.floatToIntBits(floatArray[i]);
            intBuffer.put(intArray);
            intBuffer.clear();
        }
        toc("convert bulk put");

        // jni bulk put
        tic();
        for (int tries = 0; tries < TRIES; tries++) {
            com.badlogic.gdx.utils.BufferUtils.copy(floatArray, floatBuffer, floatArray.length, 0);
            floatBuffer.clear();
        }
        toc("jni bulk put");
    });

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        tv = new TextView(this);
        sv = new ScrollView(this);
        sv.addView(tv);
        setContentView(sv);

        testThread.start();
    }

    void tic() {
        start = System.nanoTime();
    }

    void toc(final String info) {
        final float time = (System.nanoTime() - start) / 1000000000.0f;

        tv.post(() -> tv.setText(new StringBuilder().append(tv.getText()).append(info).append(", ").append(time).append(" secs\n").toString()));

        Log.d("MicroBenchmarks", info + ", " + time);
    }
}
