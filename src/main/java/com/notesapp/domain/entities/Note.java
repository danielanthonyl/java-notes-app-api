package com.notesapp.domain.entities;

import java.util.UUID;

public class Note {
    public String title;
    public String body;
    public UUID id;

    public Note() {
        id = UUID.randomUUID();
    }

    public Note(UUID id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }
}

