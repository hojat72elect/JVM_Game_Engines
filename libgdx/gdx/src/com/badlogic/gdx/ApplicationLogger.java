package com.badlogic.gdx;

/**
 * The ApplicationLogger provides an interface for a libGDX Application to log messages and exceptions. A default implementations
 * is provided for each backend, custom implementations can be provided and set using
 * {@link Application#setApplicationLogger(ApplicationLogger) }
 */
public interface ApplicationLogger {

    /**
     * Logs a message with a tag
     */
    public void log(String tag, String message);

    /**
     * Logs a message and exception with a tag
     */
    public void log(String tag, String message, Throwable exception);

    /**
     * Logs an error message with a tag
     */
    public void error(String tag, String message);

    /**
     * Logs an error message and exception with a tag
     */
    public void error(String tag, String message, Throwable exception);

    /**
     * Logs a debug message with a tag
     */
    public void debug(String tag, String message);

    /**
     * Logs a debug message and exception with a tag
     */
    public void debug(String tag, String message, Throwable exception);

}
