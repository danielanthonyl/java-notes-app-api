package com.notesapp.src;

import java.io.IOException;

import com.notesapp.src.adapters.notes.Notes;
import com.notesapp.src.utils.Server;

public class Main {
    public static void main(String... args) {
        try {
            Server server = new Server("localhost", 3000);

            // routes
            server.addContext("/notes", new Notes());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}