package unit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodosTest {

    private static final String BASE_URL = "http://localhost:4567";
    private static HttpClient client = HttpClient.newHttpClient();
    private String createdTodoId;

    /**
     * Unit test module for testing 
     *
     * Structure (following requirements):
     *  - Ensure system is ready to be tested
     *  - Save system state
     *  - Set up initial conditions for the test
     *  - Execute tests
     *  - Assess correctness
     *  - Restore the system to the initial state
     *  - Run in any order
    */

    /** Ensure the system is ready to be tested */
    @BeforeAll
    static void ensureSystemReady() throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/gui")) // Guaranteed endpoint to check the status of the system
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    /** Save system state */
    @BeforeEach
    void saveSystemState(){
        // Only necessary for POST/PUT tests to ensure ressources can be returned to the originial state
    }

    /** Restore system to its initial state */
    @AfterEach
    void restoreSystemState(){
        // Only necessary for POST/PUT/DELETE tests to ensure ressources are returned to the originial state
        if (createdTodoId != null && !createdTodoId.isEmpty()) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/todos/" + createdTodoId))
                        .DELETE()
                        .build();
                
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 200 && deleteResponse.statusCode()!= 204){
                    System.err.println("Failed to clean up. Status: " + deleteResponse.statusCode());
                }
                
                System.out.println("Cleaned up created to do: ");
            } catch (IOException | InterruptedException e) {
                System.err.println("Failed to clean up to do: " + e.getMessage());
            }
        }
    }

    @AfterAll
    static void tearDown(){
        // finalize tests
    }


    // --------------------- /todos ----------------------
    @Test
    public void get_todos_200() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
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
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }
}