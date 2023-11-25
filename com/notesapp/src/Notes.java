package com.notesapp.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Notes implements HttpHandler {
    private List<Note> notes = new ArrayList<Note>();
    private ObjectMapper mapper;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.mapper = new ObjectMapper();

        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                getNotes(exchange);
                break;

            case "POST":
                addNote(exchange);
                break;

            case "PATCH":
                patchNote(exchange);
                break;

            case "DELETE":
                deleteNote(exchange);
                break;

            default:
                break;
        }
    }

    public void addNote(HttpExchange exchange) throws IOException {
        Note note = mapper.readValue(exchange.getRequestBody(), Note.class);

        if (note == null) {
            Response response = new Response("Error adding note");
            String jsonResponse = mapper.writeValueAsString(response);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(500, jsonResponse.length());
            exchange.getResponseBody().write(jsonResponse.getBytes());

            exchange.close();
            throw new IOException("Error serializing note");
        }

        Response response = new Response("Note successfully added");
        String jsonResponse = mapper.writeValueAsString(response);

        notes.add(note);
        System.out.println("new note added! There are " + notes.size() + " notes in the list.");

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(201, jsonResponse.length());
        exchange.getResponseBody().write(jsonResponse.getBytes());
        exchange.close();
    }

    public void getNotes(HttpExchange exchange) throws IOException {
        Map<String, String> query = queryParser(exchange.getRequestURI().getQuery());

        // System.out.println(query);

        // retrieve a note by id
        if (query != null) {
            String id = query.get("id");

            if (id == null)
                throw new IOException("couldn't find any id associated with a note");

            Note foundNote = notes.stream()
                    .filter(note -> note.id.equals(UUID.fromString(id)))
                    .findFirst()
                    .orElse(null);

            if (foundNote == null) {
                String errorMessage = String.format("no note found for id: %s", id);
                Response response = new Response(errorMessage);
                response.sendResponse(exchange, mapper, 400);

                throw new IOException(errorMessage);
            }

            Response response = new Response("note retrieved sucessfully", foundNote);
            response.sendResponse(exchange, mapper, 200);
            System.out.println(String.format("user retrieved note with id %s successfully", id));
            return;
        }

        // retrieve all notes
        Response response = new Response("List of notes successfully retrieved.", notes);
        String jsonResponse = mapper.writeValueAsString(response);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.length());
        exchange.getResponseBody().write(jsonResponse.getBytes());
        exchange.close();
    }

    public void patchNote(HttpExchange exchange) throws IOException {
        Note requestBody = mapper.readValue(exchange.getRequestBody(), Note.class);

        if (requestBody.id == null)
            throw new IOException("parameter id is required for patching a note");

        if (requestBody.title == null && requestBody.body == null)
            throw new IOException("either pass a body or a title in order to update a note");

        // notes.replaceAll(note -> {
        // if (note.id.equals(requestBody.id)) {
        // Note updatedNote = new Note();

        // updatedNote.id = requestBody.id;
        // updatedNote.body = requestBody.body != null ? requestBody.body : note.body;
        // updatedNote.title = requestBody.title != null ? requestBody.title :
        // note.title;

        // return updatedNote;
        // }

        // return note;
        // });

        Note[] updatedNote = new Note[1];

        notes.replaceAll(note -> {
            if (note.id.equals(requestBody.id)) {
                updatedNote[0] = note;

                return new Note(
                        note.id,
                        Objects.requireNonNullElse(requestBody.title, note.title),
                        Objects.requireNonNullElse(requestBody.body, note.body));

            }

            return note;

        });

        Response response = new Response("Note updated successfully", updatedNote[0]);
        response.sendResponse(exchange, mapper, 200);
    }

    public void deleteNote(HttpExchange exchange) throws IOException {
        Map<String, String> query = queryParser(exchange.getRequestURI().getQuery());

        String id = query.get("id");

        if (id == null)
            throw new IOException("query parameter id is mandatory for deleting a note.");

        Note[] removedNote = new Note[1];

        Boolean isRemoved = notes.removeIf(note -> {
            Boolean shouldRemove = note.id.equals(UUID.fromString(id));

            if (shouldRemove)
                removedNote[0] = note;

            return shouldRemove;
        });

        if (!isRemoved)
            throw new IOException("Couldn't find note with id " + id);

        Response response = new Response("note successfully removed.", removedNote[0]);
        String jsonResponse = mapper.writeValueAsString(response);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.length());
        exchange.getResponseBody().write(jsonResponse.getBytes());
        exchange.close();
    }

    public Map<String, String> queryParser(String query) {

        if (query == null || !query.contains("="))
            return null;

        String[] pairs = query.split("&");
        Map<String, String> queryMap = new HashMap<>();

        for (String pair : pairs) {
            String[] subPairs = pair.split("=");
            queryMap.put(subPairs[0], subPairs[1]);
        }

        return queryMap;
    }
}

class Note {
    public String title;
    public String body;
    public UUID id;

    public Note() {
        id = UUID.randomUUID();
    }

    public Note(UUID id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

}

class Response {
    public String message;
    public Object data;

    public Response(String msg) {
        this.message = msg;
    }

    public Response(String msg, List<Note> data) {
        this.data = data;
        this.message = msg;
    }

    public Response(String msg, Note data) {
        this.data = data;
        this.message = msg;
    }

    public void sendResponse(HttpExchange exchange, ObjectMapper mapper, int statusCode) throws IOException {
        String jsonResponse = mapper.writeValueAsString(this);

        // content type is always json so no need to dynamically pass
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonResponse.length());
        exchange.getResponseBody().write(jsonResponse.getBytes());
        exchange.close();
    }
}
