package com.badlogic.gdx.tests.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class WindowedTest extends AndroidApplication implements ApplicationListener {
    Color color = new Color(1, 1, 1, 1);

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Button b1 = new Button(this);
        b1.setText(getResources().getString(R.string.change_color));
        b1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        Button b2 = new Button(this);
        b2.setText(getResources().getString(R.string.new_window));
        b2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        View view = initializeForView(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(b1);
        layout.addView(b2);
        layout.addView(view, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        setContentView(layout);

        b1.setOnClickListener(arg0 -> color.set((float) Math.random(), (float) Math.random(), (float) Math.random(), 1));

        b2.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(com.badlogic.gdx.tests.android.WindowedTest.this, com.badlogic.gdx.tests.android.WindowedTest.class);
            com.badlogic.gdx.tests.android.WindowedTest.this.startActivity(intent);
        });
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("WindowedTest", "destroying");
    }

    @Override
    public void create() {
    }

    @Override
    public void render() {
        ScreenUtils.clear(color);
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

    @Override
    public void resize(int width, int height) {
    }
}
