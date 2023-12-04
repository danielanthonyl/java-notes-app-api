package com.notesapp.src.domain.DTOs.notes;

import java.util.UUID;

/**
 * GetAllNotesDTO
 */
public class GetNotesByIdDTO {
    public UUID id;

    public GetNotesByIdDTO(UUID id) {
        this.id = id;
    }
}

