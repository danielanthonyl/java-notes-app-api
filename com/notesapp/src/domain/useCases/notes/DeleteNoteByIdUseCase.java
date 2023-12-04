package com.notesapp.src.domain.useCases.notes;

import com.notesapp.src.dataAccess.NotesRepository;
import com.notesapp.src.domain.DTOs.notes.DeleteNoteByIdDTO;

public class DeleteNoteByIdUseCase {
        private NotesRepository notesRepository;

    public DeleteNoteByIdUseCase(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public void execute(DeleteNoteByIdDTO deleteNoteByIdDTO) throws Exception {
        notesRepository.deleteNoteById(deleteNoteByIdDTO.id);
    }
}
