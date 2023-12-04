package com.notesapp.src.dataAccess;

import java.util.UUID;

import com.notesapp.src.Database;
import com.notesapp.src.domain.entities.Note;

public class NotesRepository {
    private Database database;

    public NotesRepository(Database database) {
        this.database = database;
    }

    public void addNote(Note note) {
        String sql = String.format(
                "CALL add_nt('%s', '%s', '%s')",
                note.id,
                note.title,
                note.body);

        database.create(sql);
    }

    public Note[] getAllNotes() throws Exception {
        String sql = String.format("SELECT * FROM TABLE(get_nts())");
        Note[] notes = database.findAll(sql, Note.class);

        return notes;
    }

    public Note getNoteById(UUID noteId) throws Exception {
        String sql = String.format("SELECT * FROM TABLE(get_nts('%s'))", noteId);
        Note note = database.findUnique(sql, Note.class);

        return note;
    }

    public void updateNoteById(Note note) throws Exception {
        database.update(String.format(
                "CALL ptch_nts('%s', %s, %s)",
                note.id,
                note.title != null ? String.format("'%s'", note.title) : "NULL",
                note.body != null ? String.format("'%s'", note.body) : "NULL"));

    }

    public void deleteNotes() throws Exception {
        database.delete("CALL dlt_nt()");
    }

    public void deleteNoteById(UUID noteId) throws Exception {
        database.delete(String.format("CALL dlt_nt('%s')", noteId));
    }
}
