package com.notesapp.src.domain.useCases.notes;

import com.notesapp.src.dataAccess.NotesRepository;
import com.notesapp.src.domain.DTOs.notes.GetNotesByIdDTO;
import com.notesapp.src.domain.entities.Note;

public class GetNoteByIdUseCase {
    private NotesRepository notesRepository;

    public GetNoteByIdUseCase(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public Note execute(GetNotesByIdDTO getNotesByIdDTO) throws Exception {
        // DEBT: add trycatch and remove trycatch from
        // notesRepository.addNote/database.create
        Note notes = notesRepository.getNoteById(getNotesByIdDTO.id);

        return notes;
    }
}
