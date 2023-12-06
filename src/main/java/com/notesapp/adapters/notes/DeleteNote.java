package com.notesapp.adapters.notes;

import java.util.Map;
import java.util.UUID;

import com.notesapp.domain.DTOs.notes.DeleteNoteByIdDTO;
import com.notesapp.domain.useCases.notes.DeleteNoteByIdUseCase;
import com.notesapp.domain.useCases.notes.DeleteNotesUseCase;
import com.notesapp.utils.Response;
import com.notesapp.utils.Utils;
import com.sun.net.httpserver.HttpExchange;

public class DeleteNote {
    HttpExchange exchange;
    Response response;
    DeleteNotesUseCase deleteNotesUseCase;
    DeleteNoteByIdUseCase deleteNoteByIdUseCase;

    public DeleteNote(
            HttpExchange exchange,
            Response response,
            DeleteNotesUseCase deleteNotesUseCase,
            DeleteNoteByIdUseCase deleteNoteByIdUseCase) {
        this.exchange = exchange;
        this.response = response;
        this.deleteNoteByIdUseCase = deleteNoteByIdUseCase;
        this.deleteNotesUseCase = deleteNotesUseCase;
    };

    public void handle() {
        try {
            Map<String, String> query = Utils.queryParser(exchange.getRequestURI().getQuery());

            if (query == null) {
                deleteNotesUseCase.execute();
                response.sendResponse("successfully deleted all notes", 200, null);

                return;
            }

            String id = query.get("id");

            DeleteNoteByIdDTO deleteNoteByIdDTO = new DeleteNoteByIdDTO(UUID.fromString(id));
            deleteNoteByIdUseCase.execute(deleteNoteByIdDTO);

            response.sendResponse("note successfully removed.", 200, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
