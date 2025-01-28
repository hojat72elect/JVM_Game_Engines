package com.badlogic.gdx.net;

import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * A server socket that accepts new incoming connections, returning {@link Socket} instances. The {@link #accept(SocketHints)}
 * method should preferably be called in a separate thread as it is blocking.
 */
public interface ServerSocket extends Disposable {

    /**
     * @return the Protocol used by this socket
     */
    public Protocol getProtocol();

    /**
     * Accepts a new incoming connection from a client {@link Socket}. The given hints will be applied to the accepted socket.
     * Blocking, call on a separate thread.
     *
     * @param hints additional {@link SocketHints} applied to the accepted {@link Socket}. Input null to use the default setting
     *              provided by the system.
     * @return the accepted {@link Socket}
     * @throws GdxRuntimeException in case an error occurred
     */
    public Socket accept(SocketHints hints);
}
