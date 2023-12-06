package com.notesapp.domain.useCases.notes;

import com.notesapp.dataAccess.NotesRepository;

public class DeleteNotesUseCase {
    private NotesRepository notesRepository;

    public DeleteNotesUseCase(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public void execute() throws Exception {
        notesRepository.deleteNotes();
    }
}
