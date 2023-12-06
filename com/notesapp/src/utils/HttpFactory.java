package com.notesapp.src.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class HttpFactory implements HttpHandler {
    public ObjectMapper mapper;
    public Response response;
    public Database database;
    public HttpExchange exchange;

    public HttpFactory() {
        this.database = new Database();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.exchange = exchange;
        this.response = new Response(exchange, mapper);

        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET" -> get();
            case "POST" -> post();
            case "PATCH" -> patch();
            case "DELETE" -> delete();
            default -> {
                throw new IOException("unkown or not threated method: " + requestMethod);
            }
        }
    }

    public abstract void get();
    public abstract void post();
    public abstract void patch();
    public abstract void delete();
}