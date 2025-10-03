package unit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodosTest {

    private static final String BASE_URL = "http://localhost:4567/todos";
    private static HttpClient client = HttpClient.newHttpClient();

    // --------------------- /todos ----------------------
    @Test
    public void get_todos_200() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("todos"));
    }

    @Test
    public void post_todos_201() throws IOException, InterruptedException { 
        String json = "{\"title\":\"New Todo\",\"doneStatus\":false}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }
}