package com.notesapp.domain.useCases.notes;

import com.notesapp.dataAccess.NotesRepository;
import com.notesapp.domain.entities.Note;

public class GetAllNotesUseCase {
    private NotesRepository notesRepository;

    public GetAllNotesUseCase(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public Note[] execute() throws Exception {
        // DEBT: add trycatch and remove trycatch from
        // notesRepository.addNote/database.create
        Note[] notes = notesRepository.getAllNotes();

        return notes;
    }
}
