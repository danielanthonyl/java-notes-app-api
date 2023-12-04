package com.notesapp.src.adapters.notes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notesapp.src.domain.entities.Note;
import com.notesapp.src.domain.useCases.notes.UpdateNoteUseCase;
import com.notesapp.src.utils.Response;
import com.sun.net.httpserver.HttpExchange;

public class PatchNote {
    Response response;
    HttpExchange exchange;
    ObjectMapper mapper;
    UpdateNoteUseCase updateNoteUseCase;

    public PatchNote(ObjectMapper mapper, HttpExchange exchange, Response response,
            UpdateNoteUseCase updateNoteUseCase) {
        this.updateNoteUseCase = updateNoteUseCase;
        this.response = response;
        this.exchange = exchange;
        this.mapper = mapper;
    }

    public void handle() {
        try {
            Note requestBody = mapper.readValue(exchange.getRequestBody(), Note.class);

            if (requestBody.id == null) {
                response.sendResponse("parameter id is required for patching a note", 400, null);
            }

            if (requestBody.title == null && requestBody.body == null) {
                response.sendResponse("either pass a body or a title in order to update a note", 400, null);
            }

            String result = updateNoteUseCase.execute(requestBody);

            response.sendResponse(result, 400, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
