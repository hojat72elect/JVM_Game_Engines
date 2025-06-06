package com.badlogic.gdx.tests.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.tests.utils.GdxTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Demonstrates how to do very simple socket programming. Implements a classic PING-PONG sequence, client connects to server,
 * sends message, server sends message back to client. Both client and server run locally. We quit as soon as the client received
 * the PONG message from the server. This example won't work in HTML. Messages are delimited by the new line character, so we can
 * use a {@link BufferedReader}.
 */
public class PingPongSocketExample extends GdxTest {
    @Override
    public void create() {
        // setup a server thread where we wait for incoming connections
        // to the server
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocketHints hints = new ServerSocketHints();
                ServerSocket server = Gdx.net.newServerSocket(Protocol.TCP, "localhost", 9999, hints);
                // wait for the next client connection
                Socket client = server.accept(null);
                // read message and send it back
                try {
                    String message = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine();
                    Gdx.app.log("PingPongSocketExample", "got client message: " + message);
                    client.getOutputStream().write("PONG\n".getBytes());
                } catch (IOException e) {
                    Gdx.app.log("PingPongSocketExample", "an error occured", e);
                }
            }
        }).start();

        // create the client send a message, then wait for the
        // server to reply
        SocketHints hints = new SocketHints();
        Socket client = Gdx.net.newClientSocket(Protocol.TCP, "localhost", 9999, hints);
        try {
            client.getOutputStream().write("PING\n".getBytes());
            String response = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine();
            Gdx.app.log("PingPongSocketExample", "got server message: " + response);
        } catch (IOException e) {
            Gdx.app.log("PingPongSocketExample", "an error occured", e);
        }
    }
}
