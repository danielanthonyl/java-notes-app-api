package com.notesapp.src.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

public class Response {
    public String message;
    public Object data;

    private HttpExchange exchange;
    private ObjectMapper mapper;

    public Response(HttpExchange exchange, ObjectMapper mapper) {
        this.exchange = exchange;
        this.mapper = mapper;
    }

    public void sendResponse(String message, int statusCode, Object data) throws IOException {
        this.data = data;
        this.message = message;
        String jsonResponse = mapper.writeValueAsString(this);

        // content type is always json so no need to dynamically pass
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonResponse.length());
        exchange.getResponseBody().write(jsonResponse.getBytes());
        exchange.close();

        if (statusCode > 399) {
            throw new IOException(message);
        }
    }
}
