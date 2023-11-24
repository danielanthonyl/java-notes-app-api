package com.notesapp.src;

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
        if(isRunning) {
            throw new RuntimeException("Server is already running");
        }

        if(server != null){
            server.start();
            System.out.println(String.format("Server started at: %s:%d", socketAddress.getHostName(), socketAddress.getPort()));
            this.isRunning = true;
        }
    }

    public void addContext(String path, HttpHandler handler) {
        if(isRunning) {
            throw new RuntimeException("cannot addd contexts after server is running.");
        }

        if(server != null){
            server.createContext(path, handler);
        }
    }

    public void checkStatus() {
        System.out.println(isRunning ? "server is running" : "server is not running");
    }
}
