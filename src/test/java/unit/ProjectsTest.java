package unit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.*;

import javax.json.Json;
import javax.json.JsonArray;
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

    // --------------------- /projects ----------------------

    @Test
    @DisplayName("POST /projects should create a project with the given information")
    void testPostProject() throws IOException, InterruptedException {
        // Arrange
        
        // Parameters for a new project
        String jsonBody = """
            {
                "title": "Future Work",
                "completed": false,
                "active": true,
                "description": "Work to be completed in the future"
            }
            """;

        // Act

        // POST Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
            // Retrieve response
            JsonObject json = reader.readObject();

            createdProjectId = json.getString("id").trim(); // store for cleanup
            String title = json.getString("title");
            boolean completed = Boolean.parseBoolean(json.getString("completed"));
            boolean active = Boolean.parseBoolean(json.getString("active"));
            String description = json.getString("description");
            
            // Assert
            assertNotNull(createdProjectId, "Created project ID should not be null");
            assertEquals("Future Work", title);
            assertFalse(completed);
            assertTrue(active);
            assertEquals("Work to be completed in the future", description);

            assertEquals(201, response.statusCode(), "Expected HTTP 201 Created");
        }
    }

    @Test
    @DisplayName("GET /projects should return all current instances")
    void GetAllProjects() throws IOException, InterruptedException {
        // Arrange
        
        // Act
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
            // Retrieve response 
            JsonObject root = reader.readObject();

            // Assert
            assertTrue(root.containsKey("projects"));
            JsonArray projectsArray = root.getJsonArray("projects");
            assertNotNull(projectsArray, "Retrieved project array should not be null");
            assertTrue(projectsArray.size() > 0, "There should be at least one project");

            assertEquals(200, response.statusCode(), "Expected HTTP 200 OK");
        }
    }

    // --------------------- /projects/:id ----------------------

    @Test
    @DisplayName("GET /projects/:id should return the instance of project with a specific id")
    void GetProjectById() throws IOException, InterruptedException {
        // Arrange

        // Creating a project to get 
        String jsonBody = """
            {
                "title": "Future Work",
                "completed": false,
                "active": true,
                "description": ""
            }
            """;

        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        JsonReader reader = Json.createReader(new StringReader(response.body()));
        JsonObject json = reader.readObject();
        createdProjectId = json.getString("id").trim(); // store for cleanup


        // Act
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                .GET()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());


        try (JsonReader reader2 = Json.createReader(new StringReader(response2.body()))) {
            // Retrieve response
            JsonObject root = reader2.readObject();

            // Assert
            assertTrue(root.containsKey("projects"));
            JsonArray projectsArray = root.getJsonArray("projects");
            JsonObject project = projectsArray.getJsonObject(0);

            String projectIdString = project.getString("id").trim(); // store for cleanup
            String title = project.getString("title");
            boolean completed = Boolean.parseBoolean(project.getString("completed"));
            boolean active = Boolean.parseBoolean(project.getString("active"));
            String description = project.getString("description");
            
            // Assert
            assertEquals(createdProjectId, projectIdString);
            assertEquals("Future Work", title);
            assertFalse(completed);
            assertTrue(active);
            assertEquals("", description);

            assertEquals(200, response2.statusCode(), "Expected HTTP 200 Created");
        }
    }   

    @Test
    @DisplayName("POST /projects/:id should allow a project's instances to be amended")
    void testPostProjectById() throws IOException, InterruptedException {
        // Arrange

        // Create an initial project
        String jsonBody = """
            {
                "title": "Future Work",
                "completed": false,
                "active": true,
                "description": ""
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        JsonReader reader = Json.createReader(new StringReader(response.body()));
        JsonObject json = reader.readObject();
        createdProjectId = json.getString("id").trim(); // store for cleanup
        
        // Amend the created project's attribute
        String jsonBody2 = """
            {
                "description": "Work to be completed in the future"
            }
            """;

        // Act
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2))
                .build();
        
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader2 = Json.createReader(new StringReader(response2.body()))) {
            // Retrieve response
            JsonObject json2 = reader2.readObject();

            createdProjectId = json2.getString("id").trim(); // store for cleanup
            String title = json2.getString("title");
            boolean completed = Boolean.parseBoolean(json2.getString("completed"));
            boolean active = Boolean.parseBoolean(json2.getString("active"));
            String description = json2.getString("description");
            
            // Assert
            assertNotNull(createdProjectId, "Returned project ID should not be null");
            assertEquals("Future Work", title);
            assertFalse(completed);
            assertTrue(active);
            assertEquals("Work to be completed in the future", description);

            assertEquals(200, response2.statusCode(), "Expected HTTP 200 Created");
        }
        
    }

    @Test
    @DisplayName("PUT /projects/:id should allow a project's instances to be amended")
    void testPutProjectById() throws IOException, InterruptedException {
        // Arrange
        // Create initial project 
        String jsonBody = """
            {
                "title": "Future Work",
                "completed": false,
                "active": true,
                "description": ""
            }
            """;

        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        JsonReader reader = Json.createReader(new StringReader(response.body()));
        JsonObject json = reader.readObject();
        createdProjectId = json.getString("id").trim(); // store for cleanup
        
        // Send the amended project attributes
        String jsonBody2 = """
            {
                "title": "Future Work",
                "completed": false,
                "active": true,
                "description": "Work to be completed in the future"
            }
            """;

        // Act
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody2))
                .build();
        
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader2 = Json.createReader(new StringReader(response2.body()))) {
            // Retrieve Response
            JsonObject json2 = reader2.readObject();

            createdProjectId = json2.getString("id").trim(); // store for cleanup
            String title = json2.getString("title");
            boolean completed = Boolean.parseBoolean(json2.getString("completed"));
            boolean active = Boolean.parseBoolean(json2.getString("active"));
            String description = json2.getString("description");
            
            // Assert
            assertNotNull(createdProjectId, "Returned project ID should not be null");
            assertEquals("Future Work", title);
            assertFalse(completed);
            assertTrue(active);
            assertEquals("Work to be completed in the future", description);

            assertEquals(200, response2.statusCode(), "Expected HTTP 200 OK");
        }
        
    }

    @Test
    @DisplayName("DELETE /projects/:id should allow a project's instances to be deleted")
    void testDeleteProjectById() throws IOException, InterruptedException {
        // Arrange
        // Create initial project
        String jsonBody = """
            {
                "title": "Future Work",
                "completed": false,
                "active": true,
                "description": ""
            }
            """;

        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        JsonReader reader = Json.createReader(new StringReader(response.body()));
        JsonObject json = reader.readObject();
        createdProjectId = json.getString("id").trim(); // store for cleanup

        // Act
        // Delete project
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                .DELETE()
                .build();
        
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response2.statusCode(), "Expected HTTP 200 OK");

        // Attempt to retrieve the same project
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                .GET()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        
        try (JsonReader reader2 = Json.createReader(new StringReader(response3.body()))) {
            // Retrieve response
            JsonObject root = reader2.readObject();

            // Assert
            JsonArray projectsArray = root.getJsonArray("projects");
            assertTrue(projectsArray == null || projectsArray.size() == 0, "This project should be deleted");
            assertEquals(200, response2.statusCode(), "Expected HTTP 200 Created");
        }
        
    }

}