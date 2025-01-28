package com.badlogic.gdx.backends.iosrobovm;

import com.badlogic.gdx.Audio;

public interface IOSAudio extends Audio {
    public void didBecomeActive();

    public void willEnterForeground();

    public void willResignActive();

    public void willTerminate();
}
