package com.badlogic.gdx;

import com.badlogic.gdx.utils.Clipboard;

/**
 * <p>
 * An <code>Application</code> is the main entry point of your project. It sets up a window and rendering surface and manages the
 * different aspects of your application, namely {@link Graphics}, {@link Audio}, {@link Input} and {@link Files}. Think of an
 * Application being equivalent to Swing's <code>JFrame</code> or Android's <code>Activity</code>.
 * </p>
 *
 * <p>
 * An application can be an instance of any of the following:
 * <ul>
 * <li>a desktop application (see <code>JglfwApplication</code> found in gdx-backends-jglfw.jar)</li>
 * <li>an Android application (see <code>AndroidApplication</code> found in gdx-backends-android.jar)</li>
 * <li>an iOS application (see <code>IOSApplication</code> found in gdx-backends-robovm.jar)</li>
 * </ul>
 * Each application class has it's own startup and initialization methods. Please refer to their documentation for more
 * information.
 * </p>
 *
 * <p>
 * While game programmers are used to having a main loop, libGDX employs a different concept to accommodate the event based nature
 * of Android applications a little more. You application logic must be implemented in a {@link ApplicationListener} which has
 * methods that get called by the Application when the application is created, resumed, paused, disposed or rendered. As a
 * developer you will simply implement the ApplicationListener interface and fill in the functionality accordingly. The
 * ApplicationListener is provided to a concrete Application instance as a parameter to the constructor or another initialization
 * method. Please refer to the documentation of the Application implementations for more information. Note that the
 * ApplicationListener can be provided to any Application implementation. This means that you only need to write your program
 * logic once and have it run on different platforms by passing it to a concrete Application implementation.
 * </p>
 *
 * <p>
 * The Application interface provides you with a set of modules for graphics, audio, input and file i/o.
 * </p>
 *
 * <p>
 * {@link Graphics} offers you various methods to output visuals to the screen. This is achieved via OpenGL ES 2.0 or 3.0
 * depending on what's available an the platform. On the desktop the features of OpenGL ES 2.0 and 3.0 are emulated via desktop
 * OpenGL. On Android the functionality of the Java OpenGL ES bindings is used.
 * </p>
 *
 * <p>
 * {@link Audio} offers you various methods to output and record sound and music. This is achieved via the Java Sound API on the
 * desktop. On Android the Android media framework is used.
 * </p>
 *
 * <p>
 * {@link Input} offers you various methods to poll user input from the keyboard, touch screen, mouse and accelerometer.
 * Additionally you can implement an {@link InputProcessor} and use it with {@link Input#setInputProcessor(InputProcessor)} to
 * receive input events.
 * </p>
 *
 * <p>
 * {@link Files} offers you various methods to access internal and external files. An internal file is a file that is stored near
 * your application. On Android internal files are equivalent to assets. On the desktop the classpath is first scanned for the
 * specified file. If that fails then the root directory of your application is used for a look up. External files are resources
 * you create in your application and write to an external storage. On Android external files reside on the SD-card, on the
 * desktop external files are written to a users home directory. If you know what you are doing you can also specify absolute file
 * names. Absolute filenames are not portable, so take great care when using this feature.
 * </p>
 *
 * <p>
 * {@link Net} offers you various methods to perform network operations, such as performing HTTP requests, or creating server and
 * client sockets for more elaborate network programming.
 * </p>
 *
 * <p>
 * The <code>Application</code> also has a set of methods that you can use to query specific information such as the operating
 * system the application is currently running on and so forth. This allows you to have operating system dependent code paths. It
 * is however not recommended to use these facilities.
 * </p>
 *
 * <p>
 * The <code>Application</code> also has a simple logging method which will print to standard out on the desktop and to logcat on
 * Android.
 * </p>
 */
public interface Application {
    int LOG_NONE = 0;
    int LOG_DEBUG = 3;
    int LOG_INFO = 2;
    int LOG_ERROR = 1;

    /**
     * @return the {@link ApplicationListener} instance
     */
    ApplicationListener getApplicationListener();

    /**
     * @return the {@link Graphics} instance
     */
    Graphics getGraphics();

    /**
     * @return the {@link Audio} instance
     */
    Audio getAudio();

    /**
     * @return the {@link Input} instance
     */
    Input getInput();

    /**
     * @return the {@link Files} instance
     */
    Files getFiles();

    /**
     * @return the {@link Net} instance
     */
    Net getNet();

    /**
     * Logs a message to the console or logcat
     */
    void log(String tag, String message);

    /**
     * Logs a message to the console or logcat
     */
    void log(String tag, String message, Throwable exception);

    /**
     * Logs an error message to the console or logcat
     */
    void error(String tag, String message);

    /**
     * Logs an error message to the console or logcat
     */
    void error(String tag, String message, Throwable exception);

    /**
     * Logs a debug message to the console or logcat
     */
    void debug(String tag, String message);

    /**
     * Logs a debug message to the console or logcat
     */
    void debug(String tag, String message, Throwable exception);

    /**
     * Gets the log level.
     */
    int getLogLevel();

    /**
     * Sets the log level. {@link #LOG_NONE} will mute all log output. {@link #LOG_ERROR} will only let error messages through.
     * {@link #LOG_INFO} will let all non-debug messages through, and {@link #LOG_DEBUG} will let all messages through.
     *
     * @param logLevel {@link #LOG_NONE}, {@link #LOG_ERROR}, {@link #LOG_INFO}, {@link #LOG_DEBUG}.
     */
    void setLogLevel(int logLevel);

    /**
     * @return the current {@link ApplicationLogger}
     */
    ApplicationLogger getApplicationLogger();

    /**
     * Sets the current Application logger. Calls to {@link #log(String, String)} are delegated to this
     * {@link ApplicationLogger}
     */
    void setApplicationLogger(ApplicationLogger applicationLogger);

    /**
     * @return what {@link ApplicationType} this application has, e.g. Android or Desktop
     */
    ApplicationType getType();

    /**
     * @return the Android API level on Android, the major OS version on iOS (5, 6, 7, ..), or 0 on the desktop.
     */
    int getVersion();

    /**
     * @return the Java heap memory use in bytes
     */
    long getJavaHeap();

    /**
     * @return the Native heap memory use in bytes
     */
    long getNativeHeap();

    /**
     * Returns the {@link Preferences} instance of this Application. It can be used to store application settings across runs.
     *
     * @param name the name of the preferences, must be useable as a file name.
     * @return the preferences.
     */
    Preferences getPreferences(String name);

    Clipboard getClipboard();

    /**
     * Posts a {@link Runnable} on the main loop thread.
     * <p>
     * In a multi-window application, the {@linkplain Gdx#graphics} and {@linkplain Gdx#input} values may be unpredictable at the
     * time the Runnable is executed. If graphics or input are needed, they can be copied to a variable to be used in the Runnable.
     * For example:
     * <p>
     * <code> final Graphics graphics = Gdx.graphics;
     *
     * @param runnable the runnable.
     */
    void postRunnable(Runnable runnable);

    /**
     * Schedule an exit from the application. On android, this will cause a call to pause() and dispose() some time in the future,
     * it will not immediately finish your application. On iOS this should be avoided in production as it breaks Apples
     * guidelines
     */
    void exit();

    /**
     * Adds a new {@link LifecycleListener} to the application. This can be used by extensions to hook into the lifecycle more
     * easily. The {@link ApplicationListener} methods are sufficient for application level development.
     */
    void addLifecycleListener(LifecycleListener listener);

    /**
     * Removes the {@link LifecycleListener}.
     */
    void removeLifecycleListener(LifecycleListener listener);

    /**
     * Enumeration of possible {@link Application} types
     */
    enum ApplicationType {
        Android, Desktop, HeadlessDesktop, Applet, WebGL, iOS
    }
}
