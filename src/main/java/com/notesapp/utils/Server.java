package com.notesapp.utils;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    private HttpServer server;
    private InetSocketAddress socketAddress;
    private Boolean isRunning;

    public Server(String hostname, int port) throws IOException {
        this.socketAddress = new InetSocketAddress(hostname, port);
        this.server = HttpServer.create(socketAddress, 0);
        this.isRunning = false;
    }

    public void start() {
        if (isRunning) {
            throw new RuntimeException("Server is already running");
        }

        if (server != null) {
            server.start();
            System.out.println(
                    String.format("Server started at: %s:%d", socketAddress.getHostName(), socketAddress.getPort()));
            this.isRunning = true;
        }
    }

    public void stop() {
        if (!isRunning) {
            throw new RuntimeException("Can't stop a server that is not running.");
        }

        if (server != null) {
            server.stop(0);
            System.out.println("Server connection closed.");
            this.isRunning = true;
        }
    }

    public void addContext(String path, HttpHandler handler) {
        if (isRunning) {
            throw new RuntimeException("cannot add contexts after server is running.");
        }

        if (server != null) {
            server.createContext(path, handler);
        }
    }
}
