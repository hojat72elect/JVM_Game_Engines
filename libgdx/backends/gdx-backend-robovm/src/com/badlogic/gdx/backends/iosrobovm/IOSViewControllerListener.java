package com.badlogic.gdx.backends.iosrobovm;

/**
 * An IOSViewControllerListener can be added to an {@link IOSApplication} via
 * {@link IOSApplication#addViewControllerListener(IOSViewControllerListener)}. It will receive notification of view events.
 * </p>
 * <p>
 * The methods will be invoked on the UI thread.
 */
public interface IOSViewControllerListener {

    /**
     * Called when the {@link IOSApplication} root ViewController has appeared
     */
    public void viewDidAppear(boolean animated);

}
