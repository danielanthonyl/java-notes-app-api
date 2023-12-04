package com.notesapp.src.adapters.notes;

import java.util.Map;
import java.util.UUID;

import com.notesapp.src.domain.DTOs.notes.GetNotesByIdDTO;
import com.notesapp.src.domain.entities.Note;
import com.notesapp.src.domain.useCases.notes.GetAllNotesUseCase;
import com.notesapp.src.domain.useCases.notes.GetNoteByIdUseCase;
import com.notesapp.src.utils.Response;
import com.notesapp.src.utils.Utils;
import com.sun.net.httpserver.HttpExchange;

public class GetNote {
    GetAllNotesUseCase getAllNotesUseCase;
    GetNoteByIdUseCase getNoteByIdUseCase;
    Response response;
    HttpExchange exchange;

    public GetNote(
            HttpExchange exchange,
            Response response,
            GetAllNotesUseCase getAllNotesUseCase,
            GetNoteByIdUseCase getNoteByIdUseCase) {
        this.getAllNotesUseCase = getAllNotesUseCase;
        this.getNoteByIdUseCase = getNoteByIdUseCase;
        this.response = response;
        this.exchange = exchange;
    }

    public void handle() {
        try {
            Map<String, String> query = Utils.queryParser(exchange.getRequestURI().getQuery());

            // retrieve all notes
            if (query == null) {
                Note[] queryResult = getAllNotesUseCase.execute();

                response.sendResponse(
                        String.format("Successfully listed all notes. There are %d notes", queryResult.length),
                        200,
                        queryResult);

                return;
            }

            // retrieve a note by id
            String id = query.get("id");

            if (id != null) {
                GetNotesByIdDTO getNotesByIdDTO = new GetNotesByIdDTO(UUID.fromString(id));
                Note note = getNoteByIdUseCase.execute(getNotesByIdDTO);

                response.sendResponse(
                        String.format("Successfully retrieved a note by id: %s", id),
                        200,
                        note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
