package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.*;

/**
 * Android implementation of the {@link Net} API.
 */
public class AndroidNet implements Net {

    // IMPORTANT: The Gdx.net classes are a currently duplicated for JGLFW/LWJGL + Android!
    // If you make changes here, make changes in the other backend as well.
    final AndroidApplicationBase app;
    NetJavaImpl netJavaImpl;

    public AndroidNet(AndroidApplicationBase app, AndroidApplicationConfiguration configuration) {
        this.app = app;
        netJavaImpl = new NetJavaImpl(configuration.maxNetThreads);
    }

    @Override
    public void sendHttpRequest(HttpRequest httpRequest, final HttpResponseListener httpResponseListener) {
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
    public ServerSocket newServerSocket(Protocol protocol, String hostname, int port, ServerSocketHints hints) {
        return new NetJavaServerSocketImpl(protocol, hostname, port, hints);
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
        final Uri uri = Uri.parse(URI);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            // LiveWallpaper and Daydream applications need this flag
            if (!(app.getContext() instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            app.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

}
