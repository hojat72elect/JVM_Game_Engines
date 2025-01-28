package com.badlogic.gdx.tests;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

public class IOSRobovmTests extends IOSApplication.Delegate {

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSRobovmTests.class);
        pool.close();
    }

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.useHaptics = true;
        return new IOSApplication(new IosTestWrapper(), config);
    }
}
