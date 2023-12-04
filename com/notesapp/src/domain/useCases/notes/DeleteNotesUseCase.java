package com.notesapp.src.domain.useCases.notes;

import com.notesapp.src.dataAccess.NotesRepository;

public class DeleteNotesUseCase {
    private NotesRepository notesRepository;

    public DeleteNotesUseCase(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public void execute() throws Exception {
        notesRepository.deleteNotes();
    }
}
