package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.NetJavaImpl;
import com.badlogic.gdx.net.NetJavaServerSocketImpl;
import com.badlogic.gdx.net.NetJavaSocketImpl;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import org.lwjgl.Sys;

/**
 * LWJGL implementation of the {@link Net} API, it could be reused in other Desktop backends since it doesn't depend on LWJGL.
 */
public class LwjglNet implements Net {

    NetJavaImpl netJavaImpl;

    public LwjglNet(LwjglApplicationConfiguration configuration) {
        netJavaImpl = new NetJavaImpl(configuration.maxNetThreads);
    }

    @Override
    public void sendHttpRequest(HttpRequest httpRequest, HttpResponseListener httpResponseListener) {
        netJavaImpl.sendHttpRequest(httpRequest, httpResponseListener);
    }

    @Override
    public void cancelHttpRequest(HttpRequest httpRequest) {
        netJavaImpl.cancelHttpRequest(httpRequest);
    }

    @Override
    public boolean isHttpRequestPending(HttpRequest httpRequest) {
        return netJavaImpl.isHttpRequestPending(httpRequest);
    }

    @Override
    public ServerSocket newServerSocket(Protocol protocol, String ipAddress, int port, ServerSocketHints hints) {
        return new NetJavaServerSocketImpl(protocol, ipAddress, port, hints);
    }

    @Override
    public ServerSocket newServerSocket(Protocol protocol, int port, ServerSocketHints hints) {
        return new NetJavaServerSocketImpl(protocol, port, hints);
    }

    @Override
    public Socket newClientSocket(Protocol protocol, String host, int port, SocketHints hints) {
        return new NetJavaSocketImpl(protocol, host, port, hints);
    }

    @Override
    public boolean openURI(String URI) {
        return Sys.openURL(URI);
    }

}
