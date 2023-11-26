package com.notesapp.src;
import java.io.IOException;

public class Main {
    public static void main(String... args) {
        try {
            Server server = new Server("localhost", 3000);
            server.addContext("/notes", new Notes());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}