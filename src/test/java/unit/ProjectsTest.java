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
    private String createdProjectId2;
    private String createdCategoryId;
    private String createdCategoryId2;
    private String createdTaskId;
    private String createdTaskId2;

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
        // Only necessary for POST/PUT/DELETE tests to ensure ressources that are changed are removed
        if (createdProjectId != null && !createdProjectId.isEmpty()) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                        .DELETE()
                        .build();
                
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 200 && deleteResponse.statusCode()!= 204){
                    //System.err.println("Failed to clean up. Status: " + deleteResponse.statusCode());
                }
                
                //System.out.println("Cleaned up created project: ");
            } catch (IOException | InterruptedException e) {
                //System.err.println("Failed to clean up project: " + e.getMessage());
            }
        }

        if (createdProjectId2 != null && !createdProjectId2.isEmpty()) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/projects/" + createdProjectId2))
                        .DELETE()
                        .build();
                
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 200 && deleteResponse.statusCode()!= 204){
                    //System.err.println("Failed to clean up. Status: " + deleteResponse.statusCode());
                }
                
                //System.out.println("Cleaned up created project: ");
            } catch (IOException | InterruptedException e) {
                //System.err.println("Failed to clean up project: " + e.getMessage());
            }
        }

        if (createdCategoryId != null && !createdCategoryId.isEmpty()) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/categories/" + createdCategoryId))
                        .DELETE()
                        .build();
                
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 200 && deleteResponse.statusCode()!= 204){
                    //System.err.println("Failed to clean up. Status: " + deleteResponse.statusCode());
                }
                
                //System.out.println("Cleaned up created project: ");
            } catch (IOException | InterruptedException e) {
                //System.err.println("Failed to clean up project: " + e.getMessage());
            }
        }

        if (createdCategoryId2 != null && !createdCategoryId2.isEmpty()) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/categories/" + createdCategoryId2))
                        .DELETE()
                        .build();
                
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 200 && deleteResponse.statusCode()!= 204){
                    //System.err.println("Failed to clean up. Status: " + deleteResponse.statusCode());
                }
                
                //System.out.println("Cleaned up created project: ");
            } catch (IOException | InterruptedException e) {
                //System.err.println("Failed to clean up project: " + e.getMessage());
            }
        }

        if (createdTaskId != null && !createdTaskId.isEmpty()) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/todos/" + createdTaskId))
                        .DELETE()
                        .build();
                
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 200 && deleteResponse.statusCode()!= 204){
                    //System.err.println("Failed to clean up. Status: " + deleteResponse.statusCode());
                }
                
                //System.out.println("Cleaned up created project: ");
            } catch (IOException | InterruptedException e) {
                //System.err.println("Failed to clean up project: " + e.getMessage());
            }
        }

        if (createdTaskId2 != null && !createdTaskId2.isEmpty()) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/todos/" + createdTaskId2))
                        .DELETE()
                        .build();
                
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 200 && deleteResponse.statusCode()!= 204){
                    //System.err.println("Failed to clean up. Status: " + deleteResponse.statusCode());
                }
                
                //System.out.println("Cleaned up created project: ");
            } catch (IOException | InterruptedException e) {
                //System.err.println("Failed to clean up project: " + e.getMessage());
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
    void testGetAllProjects() throws IOException, InterruptedException {
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

    @Test
    @DisplayName("HEAD /projects returns headers for all instances of project")
    void testHeadAllProjects() throws IOException, InterruptedException {
        // Arrange
        
        // Create a new project
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

        // Act
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response2 = client.send(request2, HttpResponse.BodyHandlers.discarding());

        // Assert
        try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
            // Retrieve response
            JsonObject json = reader.readObject();

            createdProjectId = json.getString("id").trim(); // store for cleanup
            assertEquals(200, response2.statusCode());
            assertFalse(response2.headers().map().isEmpty());
        }
    }

    @Test
    @DisplayName("DELETE /projects should not be allowed (undocumented)")
    void testDeleteAllProjects() throws IOException, InterruptedException {
        // Arrange
        
        // Create a new project
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

        // Act
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .DELETE()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // Assert
        try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
            // Retrieve response
            JsonObject json = reader.readObject();

            createdProjectId = json.getString("id").trim(); // store for cleanup
            assertEquals(405, response2.statusCode(), "Expected HTTP 405 Method Not Allowed");
        }
    }

    @Test
    @DisplayName("PUT /projects should not be allowed (undocumented)")
    void testPutAllProjects() throws IOException, InterruptedException {
        // Arrange
        
        // Create a new project
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

        // Act
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // Assert
        try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
            // Retrieve response
            JsonObject json = reader.readObject();

            createdProjectId = json.getString("id").trim(); // store for cleanup
            assertEquals(405, response2.statusCode(), "Expected HTTP 405 Method Not Allowed");
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
    void testPutProjectByIdPassing() throws IOException, InterruptedException {
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

    @Test
    @DisplayName("HEAD /projects/:id should return the headers of the instance of project with a specific id")
    void HeadProjectById() throws IOException, InterruptedException {
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
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response2 = client.send(request2, HttpResponse.BodyHandlers.discarding());

        // Assert
        assertEquals(200, response2.statusCode());
        assertFalse(response2.headers().map().isEmpty());
    }   


    // --------------------- /categories ----------------------
    @Test
    @DisplayName("POST /projects/:id/categories should allow a project to be associated to a category")
    void testPostProjectToCategory() throws IOException, InterruptedException {
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

        // Create an initial category
        String jsonBody2 = """
            {
                "title": "Remote",
                "description": ""
            }
            """;

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2 = Json.createReader(new StringReader(response2.body()));
        JsonObject json2 = reader2.readObject();
        createdCategoryId = json2.getString("id").trim(); // store for cleanup

        // Act
        
        // Associate the category with the project
        String jsonBody3 = String.format("""
            {
                "id": "%s"
            }
            """, createdCategoryId);

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3))
                .build();
        
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3.statusCode() == 200 || response3.statusCode() == 201);

        // Get the project back to ensure association was created
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                .GET()
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader3 = Json.createReader(new StringReader(response4.body()))) {
            // Retrieve response 
            JsonObject root = reader3.readObject();

            // Assert
            assertTrue(root.containsKey("projects"));
            JsonArray projectsArray = root.getJsonArray("projects");
            JsonObject project = projectsArray.getJsonObject(0);
            JsonArray categoriesArray = project.getJsonArray("categories");
            
            // Look to see if the category newly associated is in the list of categories of the project
            boolean found = false;
            for (int i = 0; i < categoriesArray.size(); i++) {
                if (categoriesArray.getJsonObject(i).getString("id").equals(createdCategoryId)) {
                    found = true;
                    break;
                }
            }
            
            // Assert
            assertTrue(found, "Category is associated with the project");
            assertEquals(201, response3.statusCode(), "Expected HTTP 201 Created");
        }

    }

    @Test
    @DisplayName("GET /projects/:id/categories should return a project's associated categories")
    void testGetProjectCategories() throws IOException, InterruptedException {
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

        // Create two initial categories
        String jsonBody2a = """
            {
                "title": "Remote",
                "description": ""
            }
            """;

        HttpRequest request2a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2a))
                .build();

        HttpResponse<String> response2a = client.send(request2a, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2a = Json.createReader(new StringReader(response2a.body()));
        JsonObject json2a = reader2a.readObject();
        createdCategoryId = json2a.getString("id").trim(); // store for cleanup

        String jsonBody2b = """
            {
                "title": "Hydrid",
                "description": ""
            }
            """;

        HttpRequest request2b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2b))
                .build();

        HttpResponse<String> response2b = client.send(request2b, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2b = Json.createReader(new StringReader(response2b.body()));
        JsonObject json2b = reader2b.readObject();
        createdCategoryId2 = json2b.getString("id").trim(); // store for cleanup

        
       // Associate the categories with the project
        String jsonBody3a = String.format("""
            {
                "id": "%s"
            }
            """, createdCategoryId);

        HttpRequest request3a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3a))
                .build();
        
        HttpResponse<String> response3a = client.send(request3a, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3a.statusCode() == 201);

        String jsonBody3b = String.format("""
            {
                "id": "%s"
            }
            """, createdCategoryId2);

        HttpRequest request3b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();
        
        HttpResponse<String> response3b = client.send(request3b, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3b.statusCode() == 201);

        // Act
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .GET()
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader3 = Json.createReader(new StringReader(response4.body()))) {
            // Retrieve response 
            JsonObject root = reader3.readObject();

            // Assert
            assertTrue(root.containsKey("categories"));
            JsonArray categoriesArray = root.getJsonArray("categories");

            assertNotNull(categoriesArray, "Retrieved project array should not be null");
            assertTrue(categoriesArray.size() > 0, "There should be at least one project");

            // Look to see if the category newly associated is in the list of categories of the project
            boolean found1 = false;
            boolean found2 = false;
            for (int i = 0; i < categoriesArray.size(); i++) {
                if (categoriesArray.getJsonObject(i).getString("id").equals(createdCategoryId)) {
                    found1 = true;
                    break;
                }
            }
            for (int i = 0; i < categoriesArray.size(); i++) {
                if (categoriesArray.getJsonObject(i).getString("id").equals(createdCategoryId2)) {
                    found2 = true;
                    break;
                }
            }
            
            // Assert
            assertTrue(found1, "Category 1 is associated with the project");
            assertTrue(found2, "Category 2 is associated with the project");
            assertEquals(200, response4.statusCode(), "Expected HTTP 200 OK");
        }
    }

    
    @Test
    @DisplayName("HEAD /projects/:id/categories should return the headers of a project's association to a category")
    void testHeadProjectToCategory() throws IOException, InterruptedException {
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

        // Create an initial category
        String jsonBody2 = """
            {
                "title": "Remote",
                "description": ""
            }
            """;

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2 = Json.createReader(new StringReader(response2.body()));
        JsonObject json2 = reader2.readObject();
        createdCategoryId = json2.getString("id").trim(); 
        
        // Associate the category with the project
        String jsonBody3 = String.format("""
            {
                "id": "%s"
            }
            """, createdCategoryId);

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3))
                .build();
        
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3.statusCode() == 200 || response3.statusCode() == 201);

        // Act
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response4 = client.send(request4, HttpResponse.BodyHandlers.discarding());

        // Assert
        assertEquals(200, response4.statusCode());
        assertFalse(response4.headers().map().isEmpty());

    }
    
    @Test
    @DisplayName("PUT /projects/:id/categories should not allow a project's association to a category to be changed (undocumented)")
    void testPutProjectToCategory() throws IOException, InterruptedException {
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

        // Create an initial category
        String jsonBody2 = """
            {
                "title": "Remote",
                "description": ""
            }
            """;

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2 = Json.createReader(new StringReader(response2.body()));
        JsonObject json2 = reader2.readObject();
        createdCategoryId = json2.getString("id").trim(); 
        
        // Associate the category with the project
        String jsonBody3 = String.format("""
            {
                "id": "%s"
            }
            """, createdCategoryId);

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3))
                .build();
        
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3.statusCode() == 200 || response3.statusCode() == 201);

        // Act
        // Attempt to change the project's association 
        String jsonBody4 = String.format("""
            {
                "id": "%s"
            }
            """, createdCategoryId);
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody4))
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader3 = Json.createReader(new StringReader(response4.body()))) {
            // Assert
            assertEquals(405, response4.statusCode(), "Expected HTTP 405 Method Not Allowed");
        }

    }


    // --------------------- /categories/:id ----------------------
    @Test
    @DisplayName("DELETE /projects/:id/categories should delete a project's associated categories with a specific id")
    void testDeleteProjectCategories() throws IOException, InterruptedException {
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

        // Create two initial categories
        String jsonBody2a = """
            {
                "title": "Remote",
                "description": ""
            }
            """;

        HttpRequest request2a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2a))
                .build();

        HttpResponse<String> response2a = client.send(request2a, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2a = Json.createReader(new StringReader(response2a.body()));
        JsonObject json2a = reader2a.readObject();
        createdCategoryId = json2a.getString("id").trim(); // store for cleanup

        String jsonBody2b = """
            {
                "title": "Hydrid",
                "description": ""
            }
            """;

        HttpRequest request2b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2b))
                .build();

        HttpResponse<String> response2b = client.send(request2b, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2b = Json.createReader(new StringReader(response2b.body()));
        JsonObject json2b = reader2b.readObject();
        createdCategoryId2 = json2b.getString("id").trim(); // store for cleanup

        
       // Associate the categories with the project
        String jsonBody3a = String.format("""
            {
                "id": "%s"
            }
            """, createdCategoryId);

        HttpRequest request3a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3a))
                .build();
        
        HttpResponse<String> response3a = client.send(request3a, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3a.statusCode() == 201);

        String jsonBody3b = String.format("""
            {
                "id": "%s"
            }
            """, createdCategoryId2);

        HttpRequest request3b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();
        
        HttpResponse<String> response3b = client.send(request3b, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3b.statusCode() == 201);

        // Act
        // Delete the first association of one of the categories with the project
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories/" + createdCategoryId))
                .DELETE()
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        
        // Retrieve the project that was associated to the categories
        HttpRequest request5 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                .GET()
                .build();

        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        

        try (JsonReader reader3 = Json.createReader(new StringReader(response5.body()))) {
            // Retrieve response 
            JsonObject root = reader3.readObject();

            // Assert
            assertTrue(root.containsKey("projects"));
            JsonArray projectsArray = root.getJsonArray("projects");
            JsonObject project = projectsArray.getJsonObject(0);
            JsonArray categoriesArray = project.getJsonArray("categories");

            // Look to see if the category newly removed from the project is in the list of categories of the project
            boolean found1 = false;
            boolean found2 = false;
            for (int i = 0; i < categoriesArray.size(); i++) {
                if (categoriesArray.getJsonObject(i).getString("id").equals(createdCategoryId)) {
                    found1 = true;
                    break;
                }
            }
            for (int i = 0; i < categoriesArray.size(); i++) {
                if (categoriesArray.getJsonObject(i).getString("id").equals(createdCategoryId2)) {
                    found2 = true;
                    break;
                }
            }
            
            // Assert
            assertFalse(found1, "Category 1 should not be associated with the project");
            assertTrue(found2, "Category is associated with the project");

            assertEquals(200, response4.statusCode(), "Expected HTTP 200 Created");
        }

    }

    @Test
    @DisplayName("GET /projects/:id/categories should not allow to get project's association to a category using both ids in the path (undocumented)")
    void testGetProjectToCategory() throws IOException, InterruptedException {
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

        // Create an initial category
        String jsonBody2 = """
            {
                "title": "Remote",
                "description": ""
            }
            """;

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2 = Json.createReader(new StringReader(response2.body()));
        JsonObject json2 = reader2.readObject();
        createdCategoryId = json2.getString("id").trim(); 
        
        // Associate the category with the project
        String jsonBody3 = String.format("""
            {
                "id": "%s"
            }
            """, createdCategoryId);

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3))
                .build();
        
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3.statusCode() == 200 || response3.statusCode() == 201);

        // Act
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/categories/" + createdCategoryId))
                .GET()
                .build();

        HttpResponse<Void> response4 = client.send(request4, HttpResponse.BodyHandlers.discarding());

        // Assert
        assertEquals(404, response4.statusCode(), "Expected HTTP 404 Not Found"); //should be 405 method not found  
    }


    // --------------------- /tasks ----------------------
    @Test
    @DisplayName("POST /projects/:id/tasks should create an association between a task (to do item) and a project")
    void testPostProjectToTask() throws IOException, InterruptedException {
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
        

        // Create two initial todo items
        String jsonBody2a = """
            {
                "title": "Make tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2a))
                .build();

        HttpResponse<String> response2a = client.send(request2a, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2a = Json.createReader(new StringReader(response2a.body()));
        JsonObject json2a = reader2a.readObject();
        createdTaskId = json2a.getString("id").trim(); // store for cleanup

        String jsonBody2b = """
            {
                "title": "Run tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2b))
                .build();

        HttpResponse<String> response2b = client.send(request2b, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2b = Json.createReader(new StringReader(response2b.body()));
        JsonObject json2b = reader2b.readObject();
        createdTaskId2 = json2b.getString("id").trim(); // store for cleanup

        // Act
        // Associate the todo items with the project
        String jsonBody3a = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId);

        HttpRequest request3a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3a))
                .build();
        
        HttpResponse<String> response3a = client.send(request3a, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3a.statusCode() == 201);

        String jsonBody3b = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId2);

        HttpRequest request3b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();
        
        HttpResponse<String> response3b = client.send(request3b, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3b.statusCode() == 201);

        // Get the project back to ensure association was created
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                .GET()
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader3 = Json.createReader(new StringReader(response4.body()))) {
            // Retrieve response 
            JsonObject root = reader3.readObject();

            // Assert
            assertTrue(root.containsKey("projects"));
            JsonArray projectsArray = root.getJsonArray("projects");
            JsonObject project = projectsArray.getJsonObject(0);
            JsonArray categoriesArray = project.getJsonArray("tasks");
            
            // Look to see if the todo items newly associated are in the list of todo items of the project
            boolean found1 = false;
            boolean found2 = false;
            for (int i = 0; i < categoriesArray.size(); i++) {
                if (categoriesArray.getJsonObject(i).getString("id").equals(createdTaskId)) {
                    found1 = true;
                    break;
                }
            }
            for (int i = 0; i < categoriesArray.size(); i++) {
                if (categoriesArray.getJsonObject(i).getString("id").equals(createdTaskId2)) {
                    found2 = true;
                    break;
                }
            }
            
            // Assert
            assertTrue(found1, "Category is associated with the project");
            assertTrue(found2, "Category is associated with the project");
            assertEquals(201, response3a.statusCode(), "Expected HTTP 201 Created");
            assertEquals(201, response3b.statusCode(), "Expected HTTP 201 Created");
        }
    }

    @Test
    @DisplayName("GET /projects/:id/tasks should retrieve all associations between tasks (to do items) and a project")
    void testGetProjectToTask() throws IOException, InterruptedException {
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
        

        // Create two initial todo items
        String jsonBody2a = """
            {
                "title": "Make tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2a))
                .build();

        HttpResponse<String> response2a = client.send(request2a, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2a = Json.createReader(new StringReader(response2a.body()));
        JsonObject json2a = reader2a.readObject();
        createdTaskId = json2a.getString("id").trim(); // store for cleanup

        String jsonBody2b = """
            {
                "title": "Run tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2b))
                .build();

        HttpResponse<String> response2b = client.send(request2b, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2b = Json.createReader(new StringReader(response2b.body()));
        JsonObject json2b = reader2b.readObject();
        createdTaskId2 = json2b.getString("id").trim(); // store for cleanup

        // Associate the todo items with the project
        String jsonBody3a = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId);

        HttpRequest request3a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3a))
                .build();
        
        HttpResponse<String> response3a = client.send(request3a, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3a.statusCode() == 201);

        String jsonBody3b = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId2);

        HttpRequest request3b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();
        
        HttpResponse<String> response3b = client.send(request3b, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3b.statusCode() == 201);
        
        // Act
        // Get the project back to ensure association was created
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader3 = Json.createReader(new StringReader(response4.body()))) {
            // Retrieve response 
            JsonObject root = reader3.readObject();

            // Assert
            assertTrue(root.containsKey("todos"));
            JsonArray tasksArray = root.getJsonArray("todos");
            
            // Look to see if the todo items are in the list of todos of the project
            boolean found1 = false;
            boolean found2 = false;
            for (int i = 0; i < tasksArray.size(); i++) {
                if (tasksArray.getJsonObject(i).getString("id").equals(createdTaskId)) {
                    found1 = true;
                    break;
                }
            }
            for (int i = 0; i < tasksArray.size(); i++) {
                if (tasksArray.getJsonObject(i).getString("id").equals(createdTaskId2)) {
                    found2 = true;
                    break;
                }
            }
            
            // Assert
            assertTrue(found1, "Todo item 1 is associated with the project");
            assertTrue(found2, "Todo item 2 is associated with the project");

            assertEquals(200, response4.statusCode(), "Expected HTTP 200 OK");
        }
    }

    @Test
    @DisplayName("HEAD /projects/:id/tasks should return the headers of an association between a task (to do item) and a project")
    void testHeadProjectToTask() throws IOException, InterruptedException {
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
        

        // Create two initial todo items
        String jsonBody2a = """
            {
                "title": "Make tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2a))
                .build();

        HttpResponse<String> response2a = client.send(request2a, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2a = Json.createReader(new StringReader(response2a.body()));
        JsonObject json2a = reader2a.readObject();
        createdTaskId = json2a.getString("id").trim(); // store for cleanup

        String jsonBody2b = """
            {
                "title": "Run tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2b))
                .build();

        HttpResponse<String> response2b = client.send(request2b, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2b = Json.createReader(new StringReader(response2b.body()));
        JsonObject json2b = reader2b.readObject();
        createdTaskId2 = json2b.getString("id").trim(); // store for cleanup

        // Associate the todo items with the project
        String jsonBody3a = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId);

        HttpRequest request3a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3a))
                .build();
        
        HttpResponse<String> response3a = client.send(request3a, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3a.statusCode() == 201);

        String jsonBody3b = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId2);

        HttpRequest request3b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();
        
        HttpResponse<String> response3b = client.send(request3b, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3b.statusCode() == 201);

        // Act
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response4 = client.send(request4, HttpResponse.BodyHandlers.discarding());

        // Assert
        assertEquals(200, response4.statusCode());
        assertFalse(response4.headers().map().isEmpty());
    }

    @Test
    @DisplayName("PUT /projects/:id/tasks should not allow the editing of an association between a task (to do item) and a project (undocumented)")
    void testPutProjectToTask() throws IOException, InterruptedException {
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
        

        // Create two initial todo items
        String jsonBody2a = """
            {
                "title": "Make tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2a))
                .build();

        HttpResponse<String> response2a = client.send(request2a, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2a = Json.createReader(new StringReader(response2a.body()));
        JsonObject json2a = reader2a.readObject();
        createdTaskId = json2a.getString("id").trim(); // store for cleanup

        String jsonBody2b = """
            {
                "title": "Run tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2b))
                .build();

        HttpResponse<String> response2b = client.send(request2b, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2b = Json.createReader(new StringReader(response2b.body()));
        JsonObject json2b = reader2b.readObject();
        createdTaskId2 = json2b.getString("id").trim(); // store for cleanup

        // Associate the todo items with the project
        String jsonBody3a = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId);

        HttpRequest request3a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3a))
                .build();
        
        HttpResponse<String> response3a = client.send(request3a, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3a.statusCode() == 201);

        String jsonBody3b = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId2);

        HttpRequest request3b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();
        
        HttpResponse<String> response3b = client.send(request3b, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3b.statusCode() == 201);

        // Act
        // attempt to modify
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response4.statusCode(), "Expected HTTP 405 Method Not Allowed");
    }


    // --------------------- /tasks/:id ----------------------
    @Test
    @DisplayName("DELETE /projects/:id/tasks/:id should remove the association between tasks (to do items) and a project with specific ids")
    void testDeleteProjectToTask() throws IOException, InterruptedException {
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
        

        // Create two initial todo items
        String jsonBody2a = """
            {
                "title": "Make tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2a))
                .build();

        HttpResponse<String> response2a = client.send(request2a, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2a = Json.createReader(new StringReader(response2a.body()));
        JsonObject json2a = reader2a.readObject();
        createdTaskId = json2a.getString("id").trim(); // store for cleanup

        String jsonBody2b = """
            {
                "title": "Run tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2b))
                .build();

        HttpResponse<String> response2b = client.send(request2b, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2b = Json.createReader(new StringReader(response2b.body()));
        JsonObject json2b = reader2b.readObject();
        createdTaskId2 = json2b.getString("id").trim(); // store for cleanup

        // Associate the todo items with the project
        String jsonBody3a = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId);

        HttpRequest request3a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3a))
                .build();
        
        HttpResponse<String> response3a = client.send(request3a, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3a.statusCode() == 201);

        String jsonBody3b = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId2);

        HttpRequest request3b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();
        
        HttpResponse<String> response3b = client.send(request3b, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3b.statusCode() == 201);

        // Act
        // Delete one of the todo item associations with the project
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks/" + createdTaskId))
                .DELETE()
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        // Get the project back to ensure association was removed
        HttpRequest request5 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());


        try (JsonReader reader3 = Json.createReader(new StringReader(response5.body()))) {
            // Retrieve response 
            JsonObject root = reader3.readObject();

            // Assert
            assertTrue(root.containsKey("todos"));
            JsonArray tasksArray = root.getJsonArray("todos");

            
            // Look to see if the category newly associated is in the list of categories of the project
            boolean found1 = false;
            boolean found2 = false;
            for (int i = 0; i < tasksArray.size(); i++) {
                if (tasksArray.getJsonObject(i).getString("id").equals(createdTaskId)) {
                    found1 = true;
                    break;
                }
            }
            for (int i = 0; i < tasksArray.size(); i++) {
                if (tasksArray.getJsonObject(i).getString("id").equals(createdTaskId2)) {
                    found2 = true;
                    break;
                }
            }
            
            // Assert
            assertFalse(found1, "Todo item 1 should not be associated with the project");
            assertTrue(found2, "Todo item 2 is associated with the project");
            assertEquals(200, response4.statusCode(), "Expected HTTP 200 Ok");
        }
    }

    @Test
    @DisplayName("GET /projects/:id/tasks/:id should return the headers of an association between a task (to do item) and a project with both ids (undocumented)")
    void testGetProjectToTaskWithId() throws IOException, InterruptedException {
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
        

        // Create two initial todo items
        String jsonBody2a = """
            {
                "title": "Make tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2a))
                .build();

        HttpResponse<String> response2a = client.send(request2a, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2a = Json.createReader(new StringReader(response2a.body()));
        JsonObject json2a = reader2a.readObject();
        createdTaskId = json2a.getString("id").trim(); // store for cleanup

        String jsonBody2b = """
            {
                "title": "Run tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2b))
                .build();

        HttpResponse<String> response2b = client.send(request2b, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2b = Json.createReader(new StringReader(response2b.body()));
        JsonObject json2b = reader2b.readObject();
        createdTaskId2 = json2b.getString("id").trim(); // store for cleanup

        // Associate the todo items with the project
        String jsonBody3a = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId);

        HttpRequest request3a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3a))
                .build();
        
        HttpResponse<String> response3a = client.send(request3a, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3a.statusCode() == 201);

        String jsonBody3b = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId2);

        HttpRequest request3b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();
        
        HttpResponse<String> response3b = client.send(request3b, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3b.statusCode() == 201);

        // Act
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks/" + createdTaskId))
                .GET()
                .build();

        HttpResponse<Void> response4 = client.send(request4, HttpResponse.BodyHandlers.discarding());

        // Assert
        assertEquals(404, response4.statusCode(), "Expected HTTP 404 Not Found"); // Would have expeceted HTTP 405 Method Not Allowed
    }

    @Test
    @DisplayName("HEAD /projects/:id/tasks/:id should return the headers of an association between a task (to do item) and a project with both ids (undocumented)")
    void testHeadProjectToTaskWithId() throws IOException, InterruptedException {
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
        

        // Create two initial todo items
        String jsonBody2a = """
            {
                "title": "Make tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2a))
                .build();

        HttpResponse<String> response2a = client.send(request2a, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2a = Json.createReader(new StringReader(response2a.body()));
        JsonObject json2a = reader2a.readObject();
        createdTaskId = json2a.getString("id").trim(); // store for cleanup

        String jsonBody2b = """
            {
                "title": "Run tester",
                "doneStatus": false,
                "description": ""
            }
            """;

        HttpRequest request2b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2b))
                .build();

        HttpResponse<String> response2b = client.send(request2b, HttpResponse.BodyHandlers.ofString());
        JsonReader reader2b = Json.createReader(new StringReader(response2b.body()));
        JsonObject json2b = reader2b.readObject();
        createdTaskId2 = json2b.getString("id").trim(); // store for cleanup

        // Associate the todo items with the project
        String jsonBody3a = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId);

        HttpRequest request3a = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3a))
                .build();
        
        HttpResponse<String> response3a = client.send(request3a, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3a.statusCode() == 201);

        String jsonBody3b = String.format("""
            {
                "id": "%s"
            }
            """, createdTaskId2);

        HttpRequest request3b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody3b))
                .build();
        
        HttpResponse<String> response3b = client.send(request3b, HttpResponse.BodyHandlers.ofString());
        assertTrue(response3b.statusCode() == 201);

        // Act
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId + "/tasks/" + createdTaskId))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response4 = client.send(request4, HttpResponse.BodyHandlers.discarding());

        // Assert
        assertEquals(404, response4.statusCode(), "Expected HTTP 404 Not Found"); // Would have expeceted HTTP 405 Method Not Allowed
        assertFalse(response4.headers().map().isEmpty());
    }

    

    // --------------------- Miscellaneous FAILING TESTS -------------------
    @Test
    @DisplayName("PUT /projects/:id should allow a project's instances to be amended but fails since requires full object attributes")
    void testPutProjectByIdFailing() throws IOException, InterruptedException {
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
            assertEquals("", title); // unexpected behaviour becomes empty because was not included
            assertEquals("Work to be completed in the future", description);

            assertEquals(200, response2.statusCode(), "Expected HTTP 200 OK");
        }
        
    }

    @Test
    @DisplayName("POST /projects should create a project with the given information but fails because of malformed JSON")
    void testPostProjectFailing() throws IOException, InterruptedException {
        // Arrange
        
        // Parameters for a new project
        String jsonBody = """
            {
                "title": "Future Work",
                "completed": "false",
                "active": "true",
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
        assertEquals(400, response.statusCode(), "Expected HTTP 400 Bad Request");
    }

    @Test
    @DisplayName("DELETE /projects/:id should not allow a project's instances to be deleted once they already have been deleted")
    void testDeleteProjectByIdFailing() throws IOException, InterruptedException {
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

        // Attempting to delete the project again
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + createdProjectId))
                .DELETE()
                .build();
        
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode(), "Expected HTTP 404 Not Found");
        
        
    }

    @Test
    @DisplayName("POST /projects should create a project with the given information but does not handle a duplicate project properly")
    void testPostProjectDuplicate() throws IOException, InterruptedException {
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

        // POST Request 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonReader reader = Json.createReader(new StringReader(response.body()));
        JsonObject json = reader.readObject();

        createdProjectId = json.getString("id").trim(); // store for cleanup
        String title = json.getString("title");
        boolean completed = Boolean.parseBoolean(json.getString("completed"));
        boolean active = Boolean.parseBoolean(json.getString("active"));
        String description = json.getString("description");

        // Act
        String jsonBody2 = """
            {
                "title": "Future Work 5",
                "completed": false,
                "active": true,
                "description": "Work to be completed in the future"
            }
            """;

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody2))
                .build();
        
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        try (JsonReader reader2 = Json.createReader(new StringReader(response2.body()))) {
            // Retrieve response
            JsonObject json2 = reader2.readObject();

            createdProjectId2 = json2.getString("id").trim(); // store for cleanup
            String title2 = json2.getString("title");
            boolean completed2 = Boolean.parseBoolean(json2.getString("completed"));
            boolean active2 = Boolean.parseBoolean(json2.getString("active"));
            String description2 = json2.getString("description");
            
            // Assert
            
            assertNotEquals(title, title2);
            assertEquals(completed, completed2);
            assertEquals(active, active2);
            assertEquals(description, description2);
            assertNotEquals(createdProjectId, createdProjectId2); // Should not be created since all information is duplicated yet still created with differnt id

            assertEquals(201, response.statusCode(), "Expected HTTP 201 Created");
            assertEquals(201, response2.statusCode(), "Expected HTTP 201 Created");

        }
    }

}