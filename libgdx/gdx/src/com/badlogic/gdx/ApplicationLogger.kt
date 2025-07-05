package com.badlogic.gdx

/**
 * The ApplicationLogger provides an interface for a libGDX Application to log messages and exceptions.
 * A default implementations is provided for each backend, custom implementations can be provided and set using [Application.setApplicationLogger].
 */
interface ApplicationLogger {

    /**
     * Logs a message with a tag.
     */
    fun log(tag: String, message: String)

    /**
     * Logs a message and exception with a tag.
     */
    fun log(tag: String, message: String, exception: Throwable)

    /**
     * Logs an error message with a tag.
     */
    fun error(tag: String, message: String)

    /**
     * Logs an error message and exception with a tag.
     */
    fun error(tag: String, message: String, exception: Throwable)

    /**
     * Logs a debug message with a tag.
     */
    fun debug(tag: String, message: String)

    /**
     * Logs a debug message and exception with a tag.
     */
    fun debug(tag: String, message: String, exception: Throwable)

}