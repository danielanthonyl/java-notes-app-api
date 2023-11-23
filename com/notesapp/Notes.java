package com.notesapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Notes implements HttpHandler {
    private List<String> notes = new ArrayList<String>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":

                break;

            case "POST":
                addNote(exchange);
                break;

            default:
                break;
        }
    }

    public void addNote(HttpExchange exchange) {
        try {

            ObjectMapper mapper = new ObjectMapper();

            Example value = mapper.readValue(exchange.getRequestBody(), Example.class);

            System.out.println(value != null);

            if (value != null) {
                Response response = new Response("Note successfully added");
                String jsonResponse = mapper.writeValueAsString(response);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(201, jsonResponse.length());
                exchange.getResponseBody().write(jsonResponse.getBytes());
            } else {
                throw new Exception("Error serializing note");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Example {
    public String name;
    public int age;
}

class Response {
    public String message;

    Response(String msg) {
        this.message = msg;
    }
}