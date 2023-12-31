package com.notesapp.adapters.notes;

import com.notesapp.dataAccess.NotesRepository;
import com.notesapp.domain.useCases.notes.AddNoteUseCase;
import com.notesapp.domain.useCases.notes.DeleteNoteByIdUseCase;
import com.notesapp.domain.useCases.notes.DeleteNotesUseCase;
import com.notesapp.domain.useCases.notes.GetAllNotesUseCase;
import com.notesapp.domain.useCases.notes.GetNoteByIdUseCase;
import com.notesapp.domain.useCases.notes.UpdateNoteUseCase;
import com.notesapp.utils.HttpFactory;

public class Notes extends HttpFactory {
    NotesRepository notesRepository;

    public Notes() {
        super();
        this.notesRepository = new NotesRepository(super.database);
    }

    @Override
    public void get() {
        GetAllNotesUseCase getAllNotesUseCase = new GetAllNotesUseCase(notesRepository);
        GetNoteByIdUseCase getNoteByIdUseCase = new GetNoteByIdUseCase(notesRepository);

        GetNote getNote = new GetNote(
                exchange,
                response,
                getAllNotesUseCase,
                getNoteByIdUseCase);

        getNote.handle();
    }

    @Override
    public void post() {
        AddNoteUseCase addNoteUseCase = new AddNoteUseCase(notesRepository);
        AddNote addNote = new AddNote(super.mapper, super.exchange, super.response, addNoteUseCase);

        addNote.handle();
    }

    @Override
    public void patch() {
        UpdateNoteUseCase updateNoteUseCase = new UpdateNoteUseCase(notesRepository);
        PatchNote patchNote = new PatchNote(mapper, exchange, response, updateNoteUseCase);

        patchNote.handle();
    }

    @Override
    public void delete() {
        DeleteNoteByIdUseCase deleteNoteByIdUseCase = new DeleteNoteByIdUseCase(notesRepository);
        DeleteNotesUseCase deleteNotesUseCase = new DeleteNotesUseCase(notesRepository);
        DeleteNote deleteNote = new DeleteNote(exchange, response, deleteNotesUseCase, deleteNoteByIdUseCase);

        deleteNote.handle();
    };
}