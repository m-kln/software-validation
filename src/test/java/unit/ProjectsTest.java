package unit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

@TestMethodOrder(MethodOrderer.Random.class)
public class ProjectsTest {

    private static final String BASE_URL = "http://localhost:4567";
    private static HttpClient client = HttpClient.newHttpClient();
    private String createdProjectId;

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
        if (createdProjectId != null && !createdProjectId.isEmpty()) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                        .DELETE()
                        .build();
                
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 200 && deleteResponse.statusCode()!= 204){
                    System.err.println("Failed to clean up. Status: " + deleteResponse.statusCode());
                }
                
                System.out.println("Cleaned up created project: ");
            } catch (IOException | InterruptedException e) {
                System.err.println("Failed to clean up project: " + e.getMessage());
            }
        }
    }

    @AfterAll
    static void tearDown(){
        // finalize tests
    }


    // // --------------------- /todos ----------------------
    // @Test
    // public void get_todos_200() throws IOException, InterruptedException {
    //     HttpRequest request = HttpRequest.newBuilder()
    //             .uri(URI.create(BASE_URL + "/todos"))
    //             .GET()
    //             .build();

    //     HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    //     assertEquals(200, response.statusCode());
    //     assertTrue(response.body().contains("todos"));
    // }

    // @Test
    // public void post_todos_201() throws IOException, InterruptedException { 
    //     String json = "{\"title\":\"New Todo\",\"doneStatus\":false}";
    //     HttpRequest request = HttpRequest.newBuilder()
    //             .uri(URI.create(BASE_URL + "/todos"))
    //             .POST(HttpRequest.BodyPublishers.ofString(json))
    //             .build();

    //     HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    //     assertEquals(201, response.statusCode());
    // }

    // --------------------- /projects ----------------------

    @Test
    @DisplayName("POST /projects should create a project with the given information")
    void testPostProject() throws IOException, InterruptedException {
        // Arrange
        String jsonBody = """
            {
                "title": "Future Work",
                "completed": false,
                "active": true,
                "description": "Work to be completed in the future"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
            JsonObject json = reader.readObject();

            // All fields are strings in your API response
            createdProjectId = json.getString("id").trim(); // store for cleanup
            String title = json.getString("title");
            boolean completed = Boolean.parseBoolean(json.getString("completed"));
            boolean active = Boolean.parseBoolean(json.getString("active"));
            String description = json.getString("description");
            
            // Assertion
            assertNotNull(createdProjectId, "Created user ID should not be null");
            assertEquals("Future Work", title);
            assertFalse(completed);
            assertTrue(active);
            assertEquals("Work to be completed in the future", description);

            assertEquals(201, response.statusCode(), "Expected HTTP 201 Created");

        }

        //assertTrue(response.body().contains("\"title\": \"Future Work\""));
        //assertTrue(response.body().contains("\"title\": \"Future Work\""));
    }







}