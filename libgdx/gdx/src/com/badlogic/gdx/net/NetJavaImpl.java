package com.badlogic.gdx.net;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements part of the {@link Net} API using {@link HttpURLConnection}, to be easily reused between the Android and Desktop
 * backends.
 */
public class NetJavaImpl {

    final ObjectMap<HttpRequest, HttpURLConnection> connections;
    final ObjectMap<HttpRequest, HttpResponseListener> listeners;
    final ObjectMap<HttpRequest, Future<?>> tasks;
    private final ThreadPoolExecutor executorService;

    public NetJavaImpl() {
        this(Integer.MAX_VALUE);
    }

    public NetJavaImpl(int maxThreads) {
        final boolean isCachedPool = maxThreads == Integer.MAX_VALUE;
        executorService = new ThreadPoolExecutor(isCachedPool ? 0 : maxThreads, maxThreads, 60L, TimeUnit.SECONDS,
                isCachedPool ? new SynchronousQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            final AtomicInteger threadID = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "NetThread" + threadID.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
        executorService.allowCoreThreadTimeOut(!isCachedPool);
        connections = new ObjectMap<HttpRequest, HttpURLConnection>();
        listeners = new ObjectMap<HttpRequest, HttpResponseListener>();
        tasks = new ObjectMap<HttpRequest, Future<?>>();
    }

    public void sendHttpRequest(final HttpRequest httpRequest, final HttpResponseListener httpResponseListener) {
        if (httpRequest.getUrl() == null) {
            httpResponseListener.failed(new GdxRuntimeException("can't process a HTTP request without URL set"));
            return;
        }

        try {
            final String method = httpRequest.getMethod();
            URL url;

            final boolean doInput = !method.equalsIgnoreCase(HttpMethods.HEAD);
            // should be enabled to upload data.
            final boolean doingOutPut = method.equalsIgnoreCase(HttpMethods.POST) || method.equalsIgnoreCase(HttpMethods.PUT)
                    || method.equalsIgnoreCase(HttpMethods.PATCH);

            if (method.equalsIgnoreCase(HttpMethods.GET) || method.equalsIgnoreCase(HttpMethods.HEAD)) {
                String queryString = "";
                String value = httpRequest.getContent();
                if (value != null && !"".equals(value)) queryString = "?" + value;
                url = new URL(httpRequest.getUrl() + queryString);
            } else {
                url = new URL(httpRequest.getUrl());
            }

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(doingOutPut);
            connection.setDoInput(doInput);
            connection.setRequestMethod(method);
            HttpURLConnection.setFollowRedirects(httpRequest.getFollowRedirects());

            putIntoConnectionsAndListeners(httpRequest, httpResponseListener, connection);

            // Headers get set regardless of the method
            for (Map.Entry<String, String> header : httpRequest.getHeaders().entrySet())
                connection.addRequestProperty(header.getKey(), header.getValue());

            // Set Timeouts
            connection.setConnectTimeout(httpRequest.getTimeOut());
            connection.setReadTimeout(httpRequest.getTimeOut());

            tasks.put(httpRequest, executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Set the content for POST and PUT (GET has the information embedded in the URL)
                        if (doingOutPut) {
                            // we probably need to use the content as stream here instead of using it as a string.
                            String contentAsString = httpRequest.getContent();
                            if (contentAsString != null) {
                                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
                                try {
                                    writer.write(contentAsString);
                                } finally {
                                    StreamUtils.closeQuietly(writer);
                                }
                            } else {
                                InputStream contentAsStream = httpRequest.getContentStream();
                                if (contentAsStream != null) {
                                    OutputStream os = connection.getOutputStream();
                                    try {
                                        StreamUtils.copyStream(contentAsStream, os);
                                    } finally {
                                        StreamUtils.closeQuietly(os);
                                    }
                                }
                            }
                        }

                        connection.connect();

                        final HttpClientResponse clientResponse = new HttpClientResponse(connection);
                        try {
                            HttpResponseListener listener = getFromListeners(httpRequest);

                            if (listener != null) {
                                listener.handleHttpResponse(clientResponse);
                            }
                        } finally {
                            removeFromConnectionsAndListeners(httpRequest);
                            connection.disconnect();
                        }
                    } catch (final Exception e) {
                        connection.disconnect();
                        try {
                            httpResponseListener.failed(e);
                        } finally {
                            removeFromConnectionsAndListeners(httpRequest);
                        }
                    }
                }
            }));
        } catch (Exception e) {
            try {
                httpResponseListener.failed(e);
            } finally {
                removeFromConnectionsAndListeners(httpRequest);
            }
        }
    }

    public void cancelHttpRequest(HttpRequest httpRequest) {
        HttpResponseListener httpResponseListener = getFromListeners(httpRequest);

        if (httpResponseListener != null) {
            httpResponseListener.cancelled();
            cancelTask(httpRequest);
            removeFromConnectionsAndListeners(httpRequest);
        }
    }

    public boolean isHttpRequestPending(HttpRequest httpRequest) {
        return getFromListeners(httpRequest) != null;
    }

    private void cancelTask(HttpRequest httpRequest) {
        Future<?> task = tasks.get(httpRequest);

        if (task != null) {
            task.cancel(false);
        }
    }

    synchronized void removeFromConnectionsAndListeners(final HttpRequest httpRequest) {
        connections.remove(httpRequest);
        listeners.remove(httpRequest);
        tasks.remove(httpRequest);
    }

    synchronized void putIntoConnectionsAndListeners(final HttpRequest httpRequest,
                                                     final HttpResponseListener httpResponseListener, final HttpURLConnection connection) {
        connections.put(httpRequest, connection);
        listeners.put(httpRequest, httpResponseListener);
    }

    synchronized HttpResponseListener getFromListeners(HttpRequest httpRequest) {
        HttpResponseListener httpResponseListener = listeners.get(httpRequest);
        return httpResponseListener;
    }

    static class HttpClientResponse implements HttpResponse {
        private final HttpURLConnection connection;
        private HttpStatus status;

        public HttpClientResponse(HttpURLConnection connection) throws IOException {
            this.connection = connection;
            try {
                this.status = new HttpStatus(connection.getResponseCode());
            } catch (IOException e) {
                this.status = new HttpStatus(-1);
            }
        }

        @Override
        public byte[] getResult() {
            InputStream input = getInputStream();

            // If the response does not contain any content, input will be null.
            if (input == null) {
                return StreamUtils.EMPTY_BYTES;
            }

            try {
                return StreamUtils.copyStreamToByteArray(input, connection.getContentLength());
            } catch (IOException e) {
                return StreamUtils.EMPTY_BYTES;
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }

        @Override
        public String getResultAsString() {
            InputStream input = getInputStream();

            // If the response does not contain any content, input will be null.
            if (input == null) {
                return "";
            }

            try {
                return StreamUtils.copyStreamToString(input, connection.getContentLength(), "UTF8");
            } catch (IOException e) {
                return "";
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }

        @Override
        public InputStream getResultAsStream() {
            return getInputStream();
        }

        @Override
        public HttpStatus getStatus() {
            return status;
        }

        @Override
        public String getHeader(String name) {
            return connection.getHeaderField(name);
        }

        @Override
        public Map<String, List<String>> getHeaders() {
            return connection.getHeaderFields();
        }

        private InputStream getInputStream() {
            try {
                return connection.getInputStream();
            } catch (IOException e) {
                return connection.getErrorStream();
            }
        }
    }
}
