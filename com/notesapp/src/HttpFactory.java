package com.notesapp.src;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.notesapp.src.utils.Response;

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

        // AddNote addNote = new AddNote(mapper, exchange, response, database);
        // GetNote getNote = new GetNote(exchange, response, database);
        // PatchNote patchNote = new PatchNote(mapper, exchange, response, database);
        // DeleteNote deleteNote = new DeleteNote(exchange, response, database);

        switch (requestMethod) {
            // case "GET" -> getNote.handle();
            case "GET" -> get();
            case "POST" -> post();
            case "PATCH" -> patch();
            case "DELETE" -> delete();
            default -> {
                // do nothing.
            }
        }
    }

    public abstract void get();
    public abstract void post();
    public abstract void patch();
    public abstract void delete();
}