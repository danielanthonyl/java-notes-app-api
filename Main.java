import com.notesapp.Server;
import com.notesapp.Notes;

public class Main {
    public static void main(String... args) {
        Server server = new Server("localhost", 3000); 
        server.addContext("/notes", new Notes());
        server.Start();
    }
}
