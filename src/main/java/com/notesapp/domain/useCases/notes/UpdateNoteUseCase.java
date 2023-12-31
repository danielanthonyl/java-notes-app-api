package com.notesapp.domain.useCases.notes;

import com.notesapp.dataAccess.NotesRepository;
import com.notesapp.domain.entities.Note;

public class UpdateNoteUseCase {
private NotesRepository notesRepository;

    public UpdateNoteUseCase(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public String execute(Note updateNoteDTO) throws Exception {
        notesRepository.updateNoteById(updateNoteDTO);

        //!DEBT: will later return the updated note.
        return "Note successfully updated.";
    }

}
