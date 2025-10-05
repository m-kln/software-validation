package unit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodosTest {

    private static final String BASE_URL = "http://localhost:4567/todos";
    private static HttpClient client = HttpClient.newHttpClient();
    ObjectMapper objectMapper = new ObjectMapper();
    
    Map<String, Object> projectTodo = Map.of(
        "title", "Project Todo",
        "doneStatus", false,
        "description", "This is a todo for the project"
    );

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
        String jsonBody = objectMapper.writeValueAsString(projectTodo);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void post_todos_202() throws IOException, InterruptedException { 
        String json = "{\"title\":\"Another Todo\",\"doneStatus\":false}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }
}