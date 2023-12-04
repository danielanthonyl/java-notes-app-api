package com.notesapp.src.domain.useCases.notes;

import com.notesapp.src.dataAccess.NotesRepository;
import com.notesapp.src.domain.entities.Note;

public class AddNoteUseCase {
    private NotesRepository notesRepository;

    public AddNoteUseCase(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public void execute(Note addNoteDTO) {
        //DEBT: add trycatch and remove trycatch from notesRepository.addNote/database.create
        notesRepository.addNote(addNoteDTO);
    }
}
