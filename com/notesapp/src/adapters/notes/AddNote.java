package com.notesapp.src.adapters.notes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notesapp.src.domain.entities.Note;
import com.notesapp.src.domain.useCases.notes.AddNoteUseCase;
import com.notesapp.src.utils.Response;
import com.sun.net.httpserver.HttpExchange;

public class AddNote {
    ObjectMapper mapper;
    HttpExchange exchange;
    Response response;
    AddNoteUseCase addNoteUseCase;

    public AddNote(ObjectMapper mapper, HttpExchange exchange, Response response, AddNoteUseCase addNoteUseCase) {
        this.mapper = mapper;
        this.exchange = exchange;
        this.response = response;
        this.addNoteUseCase = addNoteUseCase;
    };

    public void handle() {
        try {
            Note note = mapper.readValue(exchange.getRequestBody(), Note.class);

            if (note == null) {
                response.sendResponse("Error adding note", 500, null);
            }

            if (note.body == null && note.title == null) {
                response.sendResponse("Title and Body is mandatory.", 400, null);
            }

            addNoteUseCase.execute(note);

            response.sendResponse("Note successfully added.", 201, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
