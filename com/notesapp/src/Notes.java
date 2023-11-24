package com.notesapp.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Notes implements HttpHandler {
    private List<Note> notes;
    private ObjectMapper mapper;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.notes = new ArrayList<Note>();
        this.mapper = new ObjectMapper();

        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                getNotes(exchange);
                break;

            case "POST":
                addNote(exchange);
                break;

            default:
                break;
        }
    }

    public void addNote(HttpExchange exchange) throws IOException {

        Note note = mapper.readValue(exchange.getRequestBody(), Note.class);

        notes.add(note);

        if (note != null) {
            Response response = new Response("Note successfully added");
            String jsonResponse = mapper.writeValueAsString(response);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, jsonResponse.length());
            exchange.getResponseBody().write(jsonResponse.getBytes());
        } else {
            Response response = new Response("Error adding note");
            String jsonResponse = mapper.writeValueAsString(response);
            exchange.sendResponseHeaders(500, jsonResponse.length());
            exchange.getResponseBody().write(jsonResponse.getBytes());

            throw new IOException("Error serializing note");
        }
    }

    public void getNotes(HttpExchange exchange) throws IOException {
        Response response = new Response("List of notes successfully retrieved.", notes);
        String jsonResponse = mapper.writeValueAsString(response);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.length());
        exchange.getResponseBody().write(jsonResponse.getBytes());
    }
}

class Note {
    public String name;
    public int age;
}

class Response {
    public String message;
    public List<Note> data;

    Response(String msg) {
        this.message = msg;
    }

    Response(String msg, List<Note> data) {
        this.data = data;
        this.message = msg;
    }
}