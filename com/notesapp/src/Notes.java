package com.notesapp.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Notes implements HttpHandler {
    private ObjectMapper mapper;
    Database database;

    public Notes() {
        this.database = new Database();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            // case "GET" -> getNotes(exchange);
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

            String sql = String.format("CALL add_nt('%s', '%s', '%s')", note.id, note.title, note.body);
            database.create(sql);

            Response response = new Response("Note successfully added");
            response.sendResponse(exchange, mapper, 201);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getNotes(HttpExchange exchange) {
        try {
            Map<String, String> query = queryParser(exchange.getRequestURI().getQuery());

            // retrieve all notes
            if (query == null) {
                List<String> queryResult = database.findAll("SELECT * FROM TABLE(get_nts())");
                List<Note> noteList = new ArrayList<Note>();
                queryResult.forEach(note -> {
                    try {
                        noteList.add(mapper.readValue(note.toString(), Note.class));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });

                Response response = new Response("List of notes successfully retrieved.", noteList);
                response.sendResponse(exchange, mapper, 200);
                return;
            }

            // retrieve a note by id
            String id = query.get("id");

            if (id != null) {
                Database database = new Database();
                String queryResult = database.findUnique(String.format("SELECT * FROM TABLE(get_nts('%s'))", id));

                Response response = new Response(
                        String.format("Successfully retrieved a note by id: %s", id),
                        mapper.readValue(queryResult, Note.class));
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

            database.update(String.format(
                    "CALL ptch_nts('%s', %s, %s)",
                    requestBody.id,
                    requestBody.title != null ? String.format("'%s'", requestBody.title) : "NULL",
                    requestBody.body != null ? String.format("'%s'", requestBody.body) : "NULL"));

            Response response = new Response("Note updated successfully");
            response.sendResponse(exchange, mapper, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNote(HttpExchange exchange) {
        try {
            Map<String, String> query = queryParser(exchange.getRequestURI().getQuery());

            if (query == null) {
                database.delete("CALL dlt_nt()");
                Response response = new Response("successfully deleted all notes");
                response.sendResponse(exchange, mapper, 200);

                return;
            }

            String id = query.get("id");

            database.delete(String.format("CALL dlt_nt('%s')", id));

            Response response = new Response("note successfully removed.");
            response.sendResponse(exchange, mapper, 200);
        } catch (Exception e) {
            try {
                Response response = new Response("Couldn't remove note.");
                response.sendResponse(exchange, mapper, 500);
                e.printStackTrace();
            } catch (IOException error) {
                error.printStackTrace();
            }
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
