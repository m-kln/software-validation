package unit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodosTest {

    private static final String BASE_URL = "http://localhost:4567";
    private static HttpClient client = HttpClient.newHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private String createdTodoId;

    Map<String, Object> projectTodo = new HashMap<>() {{
        put("title", "Project Todo");
        put("doneStatus", true);
        put("description", "This is a todo for the project");
    }};

    Map<String, Object> projectCategory = new HashMap<>() {{
        put("title", "Project Category");
        put("description", "This is a category for the project");
    }};

    Map<String, Object> tasksOf = new HashMap<>() {{
        put("title", "Tasksof Relationship");
    }};

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
        // Only necessary for POST/PUT tests to ensure resources can be returned to the originial state
    }

    /** Restore system to its initial state */
    @AfterEach
    void restoreSystemState(){
        // Only necessary for POST/PUT/DELETE tests to ensure ressources are returned to the original state
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

    /**
     * Documented 
     */
    @Test
    @DisplayName("GET /todos - 200 OK")
    public void testGetAllTodos() throws IOException, InterruptedException {
        // Send GET request to retrieve all todos
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Verify status code of the response
        assertEquals(200, response.statusCode());

        // Verify the response body contains a list of todos
        assertNotNull(response.body());
        JsonNode root = objectMapper.readTree(response.body());
        assertTrue(root.has("todos"));
        assertTrue(root.get("todos").isArray());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos - 201 Created")
    public void testPostTodos201() throws IOException, InterruptedException { 
        // Create a new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send POST request to create a new todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(201, response.statusCode());

        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        createdTodoId = jsonRoot.get("id").asText(); // Save the created todo ID for cleanup
        assertEquals(projectTodo.get("title"), jsonRoot.get("title").asText());
        assertEquals(projectTodo.get("doneStatus"), jsonRoot.get("doneStatus").asBoolean());
        assertEquals(projectTodo.get("description"), jsonRoot.get("description").asText()); 
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos - 400 Bad Request")
    public void testPostTodos400() throws IOException, InterruptedException { 
        // Create a new todo with missing title (a mandatory field)
        String title = projectTodo.get("title").toString(); // save title
        projectTodo.remove("title");
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        projectTodo.put("title", title); // restore title

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(400, response.statusCode());
        // Verify the response body contains the right error message
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains("title : field is mandatory"));
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("PUT /todos - 405 Method Not Allowed")
    public void testPutTodos() throws IOException, InterruptedException {
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .PUT(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("DELETE /todos - 405 Method Not Allowed")
    public void testDeleteTodos() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("HEAD /todos - 200 OK")
    public void testHeadTodos() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(200, response.statusCode());
        assertFalse(response.headers().map().isEmpty());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("OPTIONS /todos - 200 OK")
    public void testOptionsTodos() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(200, response.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("Patch /todos - 405 Method Not Allowed")
    public void testPatchTodos() throws IOException, InterruptedException {
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode());
    }

     // --------------------- /todos/:id ----------------------

    @Test
    @DisplayName("GET /todos/:id - 200 OK")
    public void testGetTodoWithID() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to retrieve
        Map<String, Object> testTodo = new HashMap<>() {{
            put("title", "Todo with ID");
            put("description", "GET test for todo with ID");
        }};
        String todoJson = objectMapper.writeValueAsString(testTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        // Now, retrieve the todo by its ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(200, response.statusCode());
        // Verify the response body contains the correct todo
        assertNotNull(response.body());
        JsonNode jsonRoot = new ObjectMapper().readTree(response.body());
        assertTrue(jsonRoot.has("todos"));
        assertTrue(jsonRoot.get("todos").isArray());
        assertEquals(todoID, jsonRoot.get("todos").get(0).get("id").asText());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos/:id - 200 OK")
    public void testPostTodosWithID200() throws IOException, InterruptedException { 
        String originalTitle = projectTodo.get("title").toString();
        String newTitle = "This is an updated title";
        // Create new todo to ensure there is one to update
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Update the todo fields of the created todo
        projectTodo.remove("title");
        projectTodo.put("title", newTitle);
        String updatedJson = objectMapper.writeValueAsString(projectTodo);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        projectTodo.put("title", originalTitle); // restore title
        // Make sure todo was updated
        assertEquals(200, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        createdTodoId = jsonRoot.get("id").asText(); // Save the created todo ID for cleanup
        assertEquals(newTitle, jsonRoot.get("title").asText());
        assertEquals(projectTodo.get("doneStatus"), jsonRoot.get("doneStatus").asBoolean());
        assertEquals(projectTodo.get("description"), jsonRoot.get("description").asText()); 
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos/:id - 400 Bad Request")
    public void testPostTodosWithID400() throws IOException, InterruptedException { 
        // Create new todo to ensure there is one to update
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Update created todo with invalid field
        projectTodo.put("deadline", "Next Tuesday");
        String updatedJson = objectMapper.writeValueAsString(projectTodo);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        projectTodo.remove("deadline"); // remove invalid field
        // Verify response status code
        assertEquals(400, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains("Could not find field: deadline"));
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos/:id - 404 Not Found")
    public void testPostTodosWithID404() throws IOException, InterruptedException { 
        String invalidID = "1000";
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidID))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(404, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains("No such todo entity instance with GUID or ID 1000 found"));
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("PUT /todos/:id - 200 OK")
    public void testPutTodosWithID200() throws IOException, InterruptedException {
        String originalTitle = projectTodo.get("title").toString();
        String newTitle = "This is an updated title";
        // Create new todo to ensure there is one to update
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Update the todo fields of the created todo
        projectTodo.remove("title");
        projectTodo.put("title", newTitle);
        String updatedJson = objectMapper.writeValueAsString(projectTodo);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .PUT(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        projectTodo.put("title", originalTitle); // restore title
        // Make sure todo was updated
        assertEquals(200, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        createdTodoId = jsonRoot.get("id").asText(); // Save the created todo ID for cleanup
        assertEquals(newTitle, jsonRoot.get("title").asText());
        assertEquals(projectTodo.get("doneStatus"), jsonRoot.get("doneStatus").asBoolean());
        assertEquals(projectTodo.get("description"), jsonRoot.get("description").asText());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("PUT /todos/:id - 400 Bad Request")
    public void testPutTodosWithID400() throws IOException, InterruptedException { 
        // Create new todo to ensure there is one to update
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Remove title from created todo
        projectTodo.remove("title");
        String updatedJson = objectMapper.writeValueAsString(projectTodo);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .PUT(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(400, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains("title : field is mandatory"));
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("PUT /todos/:id - 404 Not Found")
    public void testPutTodosWithID404() throws IOException, InterruptedException { 
        String invalidID = "1000";
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidID))
                .PUT(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(404, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains("Invalid GUID for 1000 entity todo"));
    }

    
    /**
     * Documented
     */
    @Test
    @DisplayName("DELETE /todos/:id - 200 OK")
    public void testDeleteTodosWithID200() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to delete
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        // Delete created todo by its ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Make sure todo was deleted
        assertEquals(200, response.statusCode());
    }
    
    /**
     * Documented
     */
    @Test
    @DisplayName("DELETE /todos/:id - 404 Not Found")
    public void testDeleteTodosWithID404() throws IOException, InterruptedException {
        String invalidID = "1000";
        // Delete created todo by its ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidID))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(404, response.statusCode());
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains("Could not find any instances with todos/1000"));
    }   

    /**
     * Documented
     */
    @Test
    @DisplayName("HEAD /todos/:id - 200 OK")
    public void testHeadTodosWithID() throws IOException, InterruptedException {
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        // Checking headers 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        
        assertEquals(200, response.statusCode());
        assertFalse(response.headers().map().isEmpty());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("OPTIONS /todos/:id - 200 OK")
    public void testOptionsTodosWithID() throws IOException, InterruptedException {
                // Create new todo to ensure there is one to delete
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("Patch /todos - 405 Method Not Allowed")
    public void testPatchTodosWithID() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to delete
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode());
    }

    // --------------------- /todos/:id/categories ----------------------
    
    @Test
    @DisplayName("GET /todos/:id/categories - 200 OK")
    public void testGetTodosCategories() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to retrieve
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        // Retrieve categories for the created todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(200, response.statusCode());
        // Verify the response body contains categories
        assertNotNull(response.body());
        JsonNode jsonRoot = new ObjectMapper().readTree(response.body());
        assertTrue(jsonRoot.has("categories"));
        assertTrue(jsonRoot.get("categories").isArray());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos/:id/categories - 201 Created")
    public void testPostTodosCategories201() throws IOException, InterruptedException { 
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new category to associate with the todo
        String categoryJson = objectMapper.writeValueAsString(projectCategory);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure category relationship was created
        assertEquals(201, response.statusCode());

        // Get categories for the todo to verify the association
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(200, response.statusCode());
        // Check that the category is now associated with the todo
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.get("categories").isArray());
        assertTrue(jsonRoot.get("categories").toString().contains(projectCategory.get("title").toString()));
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos/:id/categories - 400 Bad Request")
    public void testPostTodosCategories400() throws IOException, InterruptedException { 
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new category to associate with the todo but with invalid field
        String title = projectCategory.get("title").toString(); // save title
        projectCategory.remove("title");
        String categoryJson = objectMapper.writeValueAsString(projectCategory);
        projectCategory.put("title", title); // restore title
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(400, response.statusCode());
        // Verify the response body contains the right error message
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains("title : field is mandatory"));
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos/:id/categories - 404 Not Found")
    public void testPostTodosCategories04() throws IOException, InterruptedException { 
        String invalidID = "1000";
        String categoryJson = objectMapper.writeValueAsString(projectCategory);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidID + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(404, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains("Could not find parent thing for relationship todos"));
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("PUT /todos/:id/categories - 405 Method Not Allowed")
    public void testPutTodosCategories() throws IOException, InterruptedException {
        String id = "1";
        String categoryJson = objectMapper.writeValueAsString(projectCategory);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + id + "/categories"))
                .PUT(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify response status code
        assertEquals(405, response.statusCode());
    }
    
    /**
     * Undocumented
     */
    @Test
    @DisplayName("DELETE /todos/:id/categories - 405 Method Not Allowed")
    public void testDeleteTodosCategories() throws IOException, InterruptedException {
        String id = "1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + id + "/categories"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("HEAD /todos/:id/categories - 200 OK")
    public void testHeadTodosCategories() throws IOException, InterruptedException {
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        // Checking headers 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        
        assertEquals(200, response.statusCode());
        assertFalse(response.headers().map().isEmpty());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("OPTIONS /todos/:id/categories - 200 OK")
    public void testOptionsTodosCategories() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to delete
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("Patch /todos/:id/categories - 405 Method Not Allowed")
    public void testPatchTodosCategories() throws IOException, InterruptedException {
        String id = "1";
        String categoryJson = objectMapper.writeValueAsString(projectCategory);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + id + "/categories"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode());
    }
    
    // --------------------- /todos/:id/categories/:id ----------------------
    
    @Test
    @DisplayName("GET /todos/:id/categories/:id - 404 Not Found")
    public void testGetTodosCategoriesID() throws IOException, InterruptedException {
        String todoID = "1";
        String categoryID = "1";
        // Retrieve categories for the created todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories/" + categoryID))
                .GET()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify status code of the response
        assertEquals(404, response.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("POST /todos/:id/categories/:id - 404 Not Found")
    public void testPostTodosCategoriesID() throws IOException, InterruptedException { 
        String todoID = "1";
        String categoryID = "1";
        // Retrieve categories for the created todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories/" + categoryID))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify status code of the response
        assertEquals(404, response.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("PUT /todos/:id/categories/:id - 405 Method Not Allowed")
    public void testPutTodosCategoriesID() throws IOException, InterruptedException {
        String todoID = "1";
        String categoryID = "1";
        // Retrieve categories for the created todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories/" + categoryID))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify status code of the response
        assertEquals(405, response.statusCode());
    }
    
    /**
     * Documented
     */
    @Test
    @DisplayName("DELETE /todos/:id/categories/:id - 200 OK")
    public void testDeleteTodosCategoriesID200() throws IOException, InterruptedException {
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new category to associate with the todo
        String categoryJson = objectMapper.writeValueAsString(projectCategory);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure category relationship was created
        assertEquals(201, response.statusCode());
        // Get ID of new category
        String categoryID = objectMapper.readTree(response.body()).get("id").asText();        
        
        // Delete the category relationship
        HttpRequest catRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories/" + categoryID))
                .DELETE()
                .build();
        HttpResponse<Void> catResponse = client.send(catRequest, HttpResponse.BodyHandlers.discarding());
        // Verify response status code
        assertEquals(200, catResponse.statusCode());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("DELETE /todos/:id/categories/:id - 404 Not Found")
    public void testDeleteTodosCategoriesID404() throws IOException, InterruptedException {
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        String invalidCategoryID = "1000";
        // Delete the category relationship
        HttpRequest catRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories/" + invalidCategoryID))
                .DELETE()
                .build();
        HttpResponse<String> catResponse = client.send(catRequest, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(404, catResponse.statusCode());
        assertNotNull(catResponse.body());
        JsonNode jsonRoot = objectMapper.readTree(catResponse.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Could not find any instances with todos/%s/categories/%s", todoID, invalidCategoryID)));
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("DELETE /todos/:id/categories/:id - 400 Bad Request")
    public void testDeleteTodosCategoriesID400() throws IOException, InterruptedException {
         // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new category to associate with the todo
        String categoryJson = objectMapper.writeValueAsString(projectCategory);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure category relationship was created
        assertEquals(201, response.statusCode());
        // Get ID of new category
        String categoryID = objectMapper.readTree(response.body()).get("id").asText();
        
        // Delete the category relationship but with invalid todo ID
        String invalidTodoId = "1000";
        HttpRequest catRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidTodoId + "/categories/" + categoryID))
                .DELETE()
                .build();
        HttpResponse<String> catResponse = client.send(catRequest, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(400, catResponse.statusCode());
        assertNotNull(catResponse.body());
        JsonNode jsonRoot = objectMapper.readTree(catResponse.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        //assertTrue(jsonRoot.get("errorMessages").toString().contains("Cannot invoke \"uk.co.compendiumdev.thingifier.core.domain.instances.ThingInstance.getRelationships()\" because \"parent\" is null"));
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("HEAD /todos/:id/categories/:id - 404 Not Found")
    public void testHeadTodosCategoriesID() throws IOException, InterruptedException {
        // Checking headers
        String todoID = "1";
        String categoryID = "1"; 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories/" + categoryID))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> headerResponse = client.send(request, HttpResponse.BodyHandlers.discarding());
        
        assertEquals(404, headerResponse.statusCode());
        assertFalse(headerResponse.headers().map().isEmpty());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("OPTIONS /todos/:id/categories/:id - 200 OK")
    public void testOptionsTodosCategoriesID() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to delete
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        // Create new category to associate with the todo
        String categoryJson = objectMapper.writeValueAsString(projectCategory);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure category relationship was created
        assertEquals(201, response.statusCode());
        // Get ID of new category
        String categoryID = objectMapper.readTree(response.body()).get("id").asText();

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories/" + categoryID))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> catResponse = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, catResponse.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("Patch /todos/:id/categories/:id - 405 Method Not Allowed")
    public void testPatchTodosCategoriesID() throws IOException, InterruptedException {
        String todoID = "1";
        String categoryID = "1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories/" + categoryID))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode());
    }  
    
    // --------------------- /todos/:id/tasksof ----------------------

    @Test
    @DisplayName("GET /todos/:id/tasksof - 200 OK")
    public void testGetTodosTaskof() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to retrieve
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        // Retrieve tasksofs for the created todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(200, response.statusCode());
        // Verify the response body contains tasksof
        assertNotNull(response.body());
        JsonNode jsonRoot = new ObjectMapper().readTree(response.body());
        assertTrue(jsonRoot.has("projects"));
        assertTrue(jsonRoot.get("projects").isArray());
        //assertEquals(todoID, jsonRoot.get("tasks").get(0).get("id").asText());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos/:id/tasksof - 201 Created")
    public void testPostTodosTasksof201() throws IOException, InterruptedException { 
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new tasksof relationship to associate with the todo
        String tasksofJson = objectMapper.writeValueAsString(tasksOf);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .POST(HttpRequest.BodyPublishers.ofString(tasksofJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure category relationship was created
        assertEquals(201, response.statusCode());

        // Get categories for the todo to verify the association
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(200, response.statusCode());
        // Check that the category is now associated with the todo
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        //assertTrue(jsonRoot.get("tasks").isArray());
        //assertEquals(jsonRoot.get("title").toString(), tasksOf.get("title").toString());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("POST /todos/:id/tasksof - 404 Not Found")
    public void testPostTodosTasksof404() throws IOException, InterruptedException { 
        String invalidID = "1000";
        String tasksofJson = objectMapper.writeValueAsString(tasksOf);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidID + "/tasksof"))
                .POST(HttpRequest.BodyPublishers.ofString(tasksofJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(404, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Could not find parent thing for relationship todos/%s/tasksof", invalidID)));
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("PUT /todos/:id/tasksof - 405 Method Not Allowed")
    public void testPutTodosTasksof() throws IOException, InterruptedException {
        String id = "1";
        String tasksofJson = objectMapper.writeValueAsString(tasksOf);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + id + "/tasksof"))
                .PUT(HttpRequest.BodyPublishers.ofString(tasksofJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify response status code
        assertEquals(405, response.statusCode());
    }
    
    /**
     * Undocumented
     */
    @Test
    @DisplayName("DELETE /todos/:id/tasksof - 405 Method Not Allowed")
    public void testDeleteTodosTasksof() throws IOException, InterruptedException {
        String id = "1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + id + "/tasksof"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("HEAD /todos/:id/categories - 200 OK")
    public void testHeadTodosTasksof() throws IOException, InterruptedException {
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        // Checking headers 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        
        assertEquals(200, response.statusCode());
        assertFalse(response.headers().map().isEmpty());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("OPTIONS /todos/:id/tasksof - 200 OK")
    public void testOptionsTodosTasksof() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to delete
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("Patch /todos/:id/tasksof - 405 Method Not Allowed")
    public void testPatchTodosTasksof() throws IOException, InterruptedException {
        String id = "1";
        String tasksofJson = objectMapper.writeValueAsString(tasksOf);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + id + "/tasksof"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(tasksofJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode());
    }
    
    // --------------------- /todos/:id/categories/:id ----------------------
    
    @Test
    @DisplayName("GET /todos/:id/tasksof/:id - 404 Not Found")
    public void testGetTodosTasksofID() throws IOException, InterruptedException {
        String todoID = "1";
        String taskID = "1";
        // Retrieve tasks for the created todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof/" + taskID))
                .GET()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify status code of the response
        assertEquals(404, response.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("POST /todos/:id/tasksof/:id - 404 Not Found")
    public void testPostTodosTaksofID() throws IOException, InterruptedException { 
        String todoID = "1";
        String taskID = "1";
        // Retrieve tasks for the created todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof/" + taskID))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify status code of the response
        assertEquals(404, response.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("PUT /todos/:id/tasksof/:id - 405 Method Not Allowed")
    public void testPutTodosTasksofID() throws IOException, InterruptedException {
        String todoID = "1";
        String taskID = "1";
        // Retrieve categories for the created todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof/" + taskID))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify status code of the response
        assertEquals(405, response.statusCode());
    }
    
    /**
     * Documented
     */
    @Test
    @DisplayName("DELETE /todos/:id/tasksof/:id - 200 OK")
    public void testDeleteTodosTasksofID200() throws IOException, InterruptedException {
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new tasksof to associate with the todo
        String tasksofJson = objectMapper.writeValueAsString(tasksOf);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .POST(HttpRequest.BodyPublishers.ofString(tasksofJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure tasksof relationship was created
        assertEquals(201, response.statusCode());
        // Get ID of new task
        String taskID = objectMapper.readTree(response.body()).get("id").asText();        
        
        // Delete the tasksof relationship
        HttpRequest taskRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof/" + taskID))
                .DELETE()
                .build();
        HttpResponse<Void> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.discarding());
        // Verify response status code
        assertEquals(200, taskResponse.statusCode());
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("DELETE /todos/:id/tasksof/:id - 404 Not Found")
    public void testDeleteTodosTasksofID404() throws IOException, InterruptedException {
        // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        String invalidTaskID = "1000";
        // Delete the tasksof relationship
        HttpRequest taskRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof/" + invalidTaskID))
                .DELETE()
                .build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(404, taskResponse.statusCode());
        assertNotNull(taskResponse.body());
        JsonNode jsonRoot = objectMapper.readTree(taskResponse.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Could not find any instances with todos/%s/tasksof/%s", todoID, invalidTaskID)));
    }

    /**
     * Documented
     */
    @Test
    @DisplayName("DELETE /todos/:id/categories/:id - 400 Bad Request")
    public void testDeleteTodosTasksofID400() throws IOException, InterruptedException {
         // Create new todo
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new tasksof to associate with the todo
        String tasksofJSON = objectMapper.writeValueAsString(tasksOf);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .POST(HttpRequest.BodyPublishers.ofString(tasksofJSON))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure tasksof relationship was created
        assertEquals(201, response.statusCode());
        // Get ID of new task
        String taskID = objectMapper.readTree(response.body()).get("id").asText();
        
        // Delete the tasksof relationship but with invalid todo ID
        String invalidTodoId = "1000";
        HttpRequest taskRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidTodoId + "/tasksof/" + taskID))
                .DELETE()
                .build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(400, taskResponse.statusCode());
        assertNotNull(taskResponse.body());
        JsonNode jsonRoot = objectMapper.readTree(taskResponse.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").isArray());
        //assertTrue(jsonRoot.get("errorMessages").toString().contains("Cannot invoke \"uk.co.compendiumdev.thingifier.core.domain.instances.ThingInstance.getRelationships()\" because \"parent\" is null"));
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("HEAD /todos/:id/tasksof/:id - 404 Not Found")
    public void testHeadTodosTasksofID() throws IOException, InterruptedException {
        // Checking headers
        String todoID = "1";
        String taskID = "1"; 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof/" + taskID))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> headerResponse = client.send(request, HttpResponse.BodyHandlers.discarding());
        
        assertEquals(404, headerResponse.statusCode());
        assertFalse(headerResponse.headers().map().isEmpty());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("OPTIONS /todos/:id/tasksof/:id - 200 OK")
    public void testOptionsTodosTasksofID() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to delete
        String todoJson = objectMapper.writeValueAsString(projectTodo);
        // Send request to create the new todo
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        // Make sure new todo was created
        assertEquals(201, postResponse.statusCode());
        // Get ID of new todo
        String todoID = objectMapper.readTree(postResponse.body()).get("id").asText();
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        // Create new tasksof to associate with the todo
        String tasksofJson = objectMapper.writeValueAsString(tasksOf);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .POST(HttpRequest.BodyPublishers.ofString(tasksofJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure tasksof relationship was created
        assertEquals(201, response.statusCode());
        // Get ID of new tasksof
        String taskID = objectMapper.readTree(response.body()).get("id").asText();

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof/" + taskID))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> taskResponse = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, taskResponse.statusCode());
    }

    /**
     * Undocumented
     */
    @Test
    @DisplayName("Patch /todos/:id/tasksof/:id - 405 Method Not Allowed")
    public void testPatchTodosTasksofID() throws IOException, InterruptedException {
        String todoID = "1";
        String taskID = "1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof/" + taskID))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode());
    }    

}

