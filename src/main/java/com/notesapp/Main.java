package com.notesapp;

import java.io.IOException;

import com.notesapp.adapters.notes.Notes;
import com.notesapp.utils.Server;

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