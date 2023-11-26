package com.notesapp.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

    public void addNote(HttpExchange exchange) {
        try {
            Note note = mapper.readValue(exchange.getRequestBody(), Note.class);

            if (note == null) {
                Response response = new Response("Error adding note");
                response.sendResponse(exchange, mapper, 500);
            }

            if (note.body == null && note.title == null) {
                Response response = new Response("Title and Body is mandatory.");
                response.sendResponse(exchange, mapper, 400);
            }

            Response response = new Response("Note successfully added");
            response.sendResponse(exchange, mapper, 201);
            notes.add(note);
            System.out.println("new note added! There are " + notes.size() + " notes in the list.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNotes(HttpExchange exchange) {
        try {
            Map<String, String> query = queryParser(exchange.getRequestURI().getQuery());

            // retrieve all notes
            if (query == null) {
                Response response = new Response("List of notes successfully retrieved.", notes);
                response.sendResponse(exchange, mapper, 200);
                return;
            }

            // retrieve a note by id
            String id = query.get("id");

            if (id != null) {
                Note foundNote = notes.stream()
                        .filter(note -> note.id.equals(UUID.fromString(id)))
                        .findFirst()
                        .orElse(null);

                Response response = new Response(
                        String.format("Successfully retrieved a note by id: %s", id),
                        foundNote);
                response.sendResponse(exchange, mapper, 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void patchNote(HttpExchange exchange) {
        try {
            Note requestBody = mapper.readValue(exchange.getRequestBody(), Note.class);

            if (requestBody.id == null) {
                Response response = new Response("parameter id is required for patching a note");
                response.sendResponse(exchange, mapper, 400);
            }

            if (requestBody.title == null && requestBody.body == null) {
                Response response = new Response("either pass a body or a title in order to update a note");
                response.sendResponse(exchange, mapper, 400);
            }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNote(HttpExchange exchange) {
        try {
            Map<String, String> query = queryParser(exchange.getRequestURI().getQuery());

            if (query == null) {
                notes.clear();
                Response response = new Response("successfully deleted all notes", notes);
                response.sendResponse(exchange, mapper, 200);

                return;
            }

            String id = query.get("id");
            Note[] removedNote = new Note[1];

            Boolean isRemoved = notes.removeIf(note -> {
                Boolean shouldRemove = note.id.equals(UUID.fromString(id));

                if (shouldRemove)
                    removedNote[0] = note;

                return shouldRemove;
            });

            if (id == null || !isRemoved) {
                Response response = new Response(
                        String.format("couldn't find or couldn't remove note with provided id %s", id));
                response.sendResponse(exchange, mapper, 400);
            }

            Response response = new Response("note successfully removed.", removedNote[0]);
            response.sendResponse(exchange, mapper, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /*
     * refactor this so instead of having multiple constructors, we will have
     * multiple
     * static classes sendResponse. This sendResponse will do the same as the
     * constructors.
     */
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

        if (statusCode > 399) {
            throw new IOException(message);
        }
    }
}
