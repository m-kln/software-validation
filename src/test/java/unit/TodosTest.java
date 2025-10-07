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
    private String createdCategoryId;
    private String createdTaskId;

    Map<String, Object> todoObj = new HashMap<>() {{
        put("title", "My Todo");
        put("doneStatus", true);
        put("description", "This is a todo for the project");
    }};

    Map<String, Object> catObj = new HashMap<>() {{
        put("title", "Category");
        put("description", "This is a category for the project");
    }};

    Map<String, Object> taskObj = new HashMap<>() {{
        put("title", "New Task");
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
                    System.err.println("Failed to clean up todo. Status: " + deleteResponse.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Failed to clean up to do: " + e.getMessage());
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
                    System.err.println("Failed to clean up cat. Status: " + deleteResponse.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Failed to clean up to do: " + e.getMessage());
            }
        }

        if (createdTaskId != null && !createdTaskId.isEmpty()) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/projects/" + createdTaskId))
                        .DELETE()
                        .build();
                
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 200 && deleteResponse.statusCode()!= 204){
                    System.err.println("Failed to clean up task. Status: " + deleteResponse.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Failed to clean up to do: " + e.getMessage());
            }
        }        
    }

    @AfterAll
    static void tearDown(){
        // finalize tests
    }

    // -------------- Helper Methods ------------------

    /**
     * Creates a todo via POST for tests that require an existing todo
     * @param todoObj: todo object with fields and their values
     * @return id of the new todo
     */
    private String createTodo(Map<String, Object> todoObj) throws IOException, InterruptedException {
        // Create a new todo
        String todoJson = objectMapper.writeValueAsString(todoObj);
        // Send POST request to create a new todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(201, response.statusCode());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        return jsonRoot.get("id").asText();
    }

    /**
     * Creates a category via POST for tests that require an existing category
     * @param catObj: category object with fields and their values
     * @return id of the new category
     */
    private String createCategory(Map<String, Object> catObj) throws IOException, InterruptedException {
        // Create a new todo
        String catJson = objectMapper.writeValueAsString(catObj);
        // Send POST request to create a new todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(catJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(201, response.statusCode());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        return jsonRoot.get("id").asText();
    }

    /**
     * Creates a task via POST for tests that require an existing project
     * @param taskObj: task object with fields and their values
     * @return id of the new task
     */
    private String createTask(Map<String, Object> taskObj) throws IOException, InterruptedException {
        // Create a new todo
        String taskJson = objectMapper.writeValueAsString(taskObj);
        // Send POST request to create a new todo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(201, response.statusCode());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        return jsonRoot.get("id").asText();
    }

    // --------------------- /todos ----------------------

    /**
     * DOCUMENTED: Test GET /todos
     * Return all instances of todo with 200 OK
     */
    @Test
    @DisplayName("GET /todos - Returns all current todo instances (200 OK) ")
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
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("todos"));
        assertTrue(jsonRoot.get("todos").isArray());
    }

    /**
     * DOCUMENTED: Test POST /todos
     * Creates a todo without an id specified in message body
     * Status code: 201 Created
     */
    @Test
    @DisplayName("POST /todos - Create a todo (201 Created)")
    public void testPostTodos201() throws IOException, InterruptedException { 
        // Create a new todo
        String todoJson = objectMapper.writeValueAsString(todoObj);
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
        // Validate todo is created with correct fields
        assertEquals(todoObj.get("title"), jsonRoot.get("title").asText());
        assertEquals(todoObj.get("doneStatus"), jsonRoot.get("doneStatus").asBoolean());
        assertEquals(todoObj.get("description"), jsonRoot.get("description").asText()); 
    }

    /**
     * DOCUMENTED: Test POST /todos
     * Attempts to create a todo without a title field in the message's body
     * and when title field is empty
     * Status code: 400 Bad Request 
     */
    @Test
    @DisplayName("POST /todos - Create a todo with no title and empty title (400 Bad Request)")
    public void testPostTodos400() throws IOException, InterruptedException { 
        // Create a new todo with missing title (a mandatory field)
        String title = todoObj.get("title").toString(); // save title
        todoObj.remove("title");
        String todoJson = objectMapper.writeValueAsString(todoObj);
        todoObj.put("title", title); // restore title

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
        assertTrue(jsonRoot.get("errorMessages").toString().contains("title : field is mandatory"));

        // Create a todo with empty title field
        title = todoObj.get("title").toString(); // save title
        todoObj.put("title", "");
        todoJson = objectMapper.writeValueAsString(todoObj);
        todoObj.put("title", title); // restore title
        // Send request with empty title field
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(400, response.statusCode());
        // Verify the response body contains the right error message
        assertNotNull(response.body());
        jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").toString().contains("Failed Validation: title : can not be empty"));
    }

    /**
     * DOCUMENTED: Test POST /todos
     * Create a todo with a malformed JSON payload
     * Status code: 400 Bad Request 
     */
    @Test
    @DisplayName("POST /todos - Malformed JSON payload (400 Bad Request)")
    public void testPostTodosMalformedJsonPayload() throws IOException, InterruptedException { 
        // Create a JSON with missing " and closing }
        String malformedJson = "{ \"title\": \"Complete session notes, \"doneStatus\": true ";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(malformedJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(400, response.statusCode());
        // Verify the response body contains the right error message
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        // Check if error message contains the MalformedJsonException
        assertTrue(jsonRoot.get("errorMessages").toString().contains("MalformedJsonException"));
    }

    /**
     * DOCUMENTED: Test HEAD /todos
     * Return headers for all the todo instances
     * Status code: 200 OK
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
        // Checks if a list of headers is present and not empty (confirm presence of standard headers)
        assertFalse(response.headers().map().isEmpty());
    }

    /**
     * UNDOCUMENTED: Test PUT /todos
     * Status code: 405 Method Not Allowed
     */
    @Test
    @DisplayName("PUT /todos - 405 Method Not Allowed")
    public void testPutTodos() throws IOException, InterruptedException {
        String todoJson = objectMapper.writeValueAsString(todoObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos"))
                .PUT(HttpRequest.BodyPublishers.ofString(todoJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED: Test DELETE /todos
     * Status code: 405 Method Not Allowed
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

     // --------------------- /todos/:id ----------------------

     /**
      * DOCUMENTED: Test GET /todos/:id
      * Retrieves todo instance associated with id
      * Status code: 200 OK
      */
     @Test
     @DisplayName("GET /todos/:id - Return todo with id (200 OK)")
     public void testGetTodoWithID() throws IOException, InterruptedException {
        String todoID = createTodo(todoObj);
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
     * DOCUMENTED: Test GET /todos/:id
     * Retrieves todo instance associated with invalid id
     * Status code: 404 Not Found
     **/
    @Test
    @DisplayName("GET /todos/:id - Invalid id (404 Not Found)")
    public void testGetTodoWithID404() throws IOException, InterruptedException {
        String todoID = "10000";
        // Now, retrieve the todo by its ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(404, response.statusCode());
        // Verify the response body contains the correct error message
        assertNotNull(response.body());
        JsonNode jsonRoot = new ObjectMapper().readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Could not find an instance with todos/%s", todoID)));
    }

    /**
     * DOCUMENTED: Test POST /todos/:id
     * Amend a specific instance of todo using id with body containing fields to amend
     * Status code: 200 OK
     */
    @Test
    @DisplayName("POST /todos/:id - 200 OK")
    public void testPostTodosWithID200() throws IOException, InterruptedException { 
        String originalTitle = todoObj.get("title").toString();
        String newTitle = "This is an updated title";
        // Create a new todo to update
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Update the todo fields of the created todo
        todoObj.remove("title");
        todoObj.put("title", newTitle);
        String updatedJson = objectMapper.writeValueAsString(todoObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        todoObj.put("title", originalTitle); // restore title
        // Make sure todo was updated
        assertEquals(200, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertEquals(newTitle, jsonRoot.get("title").asText());
        assertEquals(todoObj.get("doneStatus"), jsonRoot.get("doneStatus").asBoolean());
        assertEquals(todoObj.get("description"), jsonRoot.get("description").asText()); 
    }

    /**
     * DOCUMENTED: Test POST /todos/:id
     * Amend a specific instance of todo using id with a nonexisting field
     * Status code: 400 Bad Request
     */
    @Test
    @DisplayName("POST /todos/:id - Nonexisting field (400 Bad Request)")
    public void testPostTodosWithID400() throws IOException, InterruptedException { 
        // Create new todo to ensure there is one to update
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Update created todo with invalid field
        todoObj.put("deadline", "Next Tuesday");
        String updatedJson = objectMapper.writeValueAsString(todoObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        todoObj.remove("deadline"); // remove invalid field
        // Verify response status code
        assertEquals(400, response.statusCode());
        // Check error message
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").toString().contains("Could not find field: deadline"));
    }

    /**
     * DOCUMENTED: Test POST /todos/:id
     * Amend a specific instance of todo using invalid id
     * Status code: 404 Not Found
     */
    @Test
    @DisplayName("POST /todos/:id - Invalid ID (404 Not Found)")
    public void testPostTodosWithID404() throws IOException, InterruptedException { 
        String invalidID = "10000";
        String todoJson = objectMapper.writeValueAsString(todoObj);
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
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("No such todo entity instance with GUID or ID %s found", invalidID)));
    }

    /**
     * DOCUMENTED: Test PUT /todos/:id
     * Amend a specific instance of todo using id with body containing fields to amend
     * Status code: 200 OK
     */
    @Test
    @DisplayName("PUT /todos/:id - 200 OK")
    public void testPutTodosWithID200() throws IOException, InterruptedException {
        String originalTitle = todoObj.get("title").toString();
        String newTitle = "This is an updated title";
        // Create a new todo to update
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Update the todo fields of the created todo
        todoObj.remove("title");
        todoObj.put("title", newTitle);
        String updatedJson = objectMapper.writeValueAsString(todoObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .PUT(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        todoObj.put("title", originalTitle); // restore title
        // Make sure todo was updated
        assertEquals(200, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertEquals(newTitle, jsonRoot.get("title").asText());
        assertEquals(todoObj.get("doneStatus"), jsonRoot.get("doneStatus").asBoolean());
        assertEquals(todoObj.get("description"), jsonRoot.get("description").asText()); 
    }

    /**
     * DOCUMENTED: Test PUT /todos/:id
     * Amend a specific instance of todo without title field
     * Status code: 400 Bad Request
     */
    @Test
    @DisplayName("PUT /todos/:id - No Title in Body (400 Bad Request)")
    public void testPutTodosWithID400() throws IOException, InterruptedException { 
        // Create new todo to ensure there is one to update
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Remove title from created todo
        String title = todoObj.get("title").toString();
        todoObj.remove("title");
        String updatedJson = objectMapper.writeValueAsString(todoObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .PUT(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        todoObj.put("title", title); // Restore title
        // Verify response status code
        assertEquals(400, response.statusCode());
        // Ensure response body fields match the todo sent
        assertNotNull(response.body());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").toString().contains("title : field is mandatory"));
    }

    /**
     * DOCUMENTED: Test PUT /todos/:id
     * Amend a specific instance of todo with invalid ID
     * Status code: 404 Not Found
     */
    @Test
    @DisplayName("PUT /todos/:id - Invalid ID (404 Not Found)")
    public void testPutTodosWithID404() throws IOException, InterruptedException { 
        String invalidID = "10000";
        String todoJson = objectMapper.writeValueAsString(todoObj);
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
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Invalid GUID for %s entity todo", invalidID)));
    }

    
    /**
     * DOCUMENTED: Test DELETE /todos/:id
     * Delete a specific instance of todo with ID
     * Status code: 200 OK   
     **/
    @Test
    @DisplayName("DELETE /todos/:id - 200 OK")
    public void testDeleteTodosWithID200() throws IOException, InterruptedException {
        // Create new todo to ensure there is one to delete
        String todoID = createTodo(todoObj);
        // Delete created todo by its ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Check status code
        assertEquals(200, response.statusCode());

        // Make sure todo was deleted (can't delete it twice since it's the same ID)
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(404, response.statusCode());
    }
    
    /**
     * DOCUMENTED: Test DELETE /todos/:id
     * Delete a specific instance of todo with invalid ID
     * Status code: 400 Not Found
     **/
    @Test
    @DisplayName("DELETE /todos/:id - Invalid ID (404 Not Found)")
    public void testDeleteTodosWithID404() throws IOException, InterruptedException {
        String invalidID = "10000";
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
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Could not find any instances with todos/%s", invalidID)));
    }   

    /**
     * DOCUMENTED: Test HEAD /todos/:id
     * Return headers for a todo instance with id
     * Status code: 200 OK
     */
    @Test
    @DisplayName("HEAD /todos/:id - 200 OK")
    public void testHeadTodosWithID200() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
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
     * DOCUMENTED: Test HEAD /todos/:id
     * Return headers for a todo instance with invalid id
     * Status code: 404 Not Found
     */
    @Test
    @DisplayName("HEAD /todos/:id - Invalid ID (404 Not Found)")
    public void testHeadTodosWithID404() throws IOException, InterruptedException {
        String invalidID = "10000";
        // Checking headers 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidID))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(404, response.statusCode());
    }

    // --------------------- /todos/:id/categories ----------------------
    /**
     * DOCUMENTED: Test GET /todos/:id/categories
     * Return all category items related to todo with id
     * Status code: 200 OK
     */    
    @Test
    @DisplayName("GET /todos/:id/categories - 200 OK")
    public void testGetTodosCategories() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
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
     * BUG: Test GET /todos/:id/categories
     * Retrieve category items for todo with invalid ID
     * Should return code 404 but returns 200 instead
     */    
    @Test
    @DisplayName("GET /todos/:id/categories - Invalid ID (200 OK)")
    public void testGetTodosCategoriesInvalidID() throws IOException, InterruptedException {
        String invalidID = "10000";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidID + "/categories"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Verify status code of the response
        assertEquals(200, response.statusCode());
        // Response contains an empty array of categories
        assertNotNull(response.body());
        JsonNode jsonRoot = new ObjectMapper().readTree(response.body());
        assertTrue(jsonRoot.has("categories") || jsonRoot.has("todos"));
    }

    /**
     * DOCUMENTED: Test POST /todos/:id/categories
     * Create categories relationship between todo with id and category instance
     * represented by id in body of message
     * Status code: 201 Created
     */
    @Test
    @DisplayName("POST /todos/:id/categories - 201 Created")
    public void testPostTodosCategories201() throws IOException, InterruptedException { 
       // Create new todo
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup
        // Create category
        String catID = createCategory(catObj);
        createdCategoryId = catID;
        // Create category relationship to associate with the todo
        catObj.put("id", catID);
        String categoryJson = objectMapper.writeValueAsString(catObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure category relationship was created
        assertEquals(201, response.statusCode());
        catObj.remove("id");

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
        assertTrue(jsonRoot.get("categories").toString().contains(catObj.get("title").toString()));
        assertEquals(catObj.get("title"), jsonRoot.get("categories").get(0).get("title").asText());
    }   

    /**
     * DOCUMENTED: Test POST /todos/:id/categories
     * Create categories relationship between todo with invalid id and category instance    
     * Status code: 404 Not Found
     */
    @Test
    @DisplayName("POST /todos/:id/categories - Invalid ID (404 Not Found)")
    public void testPostTodosCategories04() throws IOException, InterruptedException { 
        String invalidID = "10000";
        String categoryJson = objectMapper.writeValueAsString(catObj);
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
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Could not find parent thing for relationship todos/%s/categories", invalidID)));
    }

    /**
     * UNDOCUMENTED - Test PUT /todos/:id/categories
     * Returns 405 
     */
    @Test
    @DisplayName("PUT /todos/:id/categories - 405 Method Not Allowed")
    public void testPutTodosCategories() throws IOException, InterruptedException {
        String id = "1";
        String categoryJson = objectMapper.writeValueAsString(catObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + id + "/categories"))
                .PUT(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify response status code
        assertEquals(405, response.statusCode());
    }
    
    /**
     * UNDOCUMENTED - Test DELETE /todos/:id/categories
     * Returns 405      
     * */
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
     * DOCUMENTED: Test HEAD /todos/:id/categories
     * Return headers for a category relationship instance with todo id
     * Status code: 200 OK
     */
    @Test
    @DisplayName("HEAD /todos/:id/categories - 200 OK")
    public void testHeadTodosCategories() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
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
     * BUG: Test HEAD /todos/:id/categories
     * Returns 200 OK for invalid todo ID
     */
    @Test
    @DisplayName("HEAD /todos/:id/categories - Invalid ID (200 OK)")
    public void testHeadTodosCategoriesInvalidID() throws IOException, InterruptedException {
        String invalidID = "10000";
        // Checking headers 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidID + "/categories"))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode());
    }
    
    // --------------------- /todos/:id/categories/:id ----------------------
    
    /**
     * UNDOCUMENTED - Test GET /todos/:id/categories/:id
     * Returns 404      
     */
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
     * UNDOCUMENTED - Test POST /todos/:id/categories/:id
     * Returns 404      
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
     * UNDOCUMENTED - Test PUT /todos/:id/categories/:id
     * Returns 405     
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
     * DOCUMENTED: Test DELETE /todos/:id/categories/:id
     * Delete categories relationship between specific todo and category
     * Status code: 200 OK     
     */
    @Test
    @DisplayName("DELETE /todos/:id/categories/:id - 200 OK")
    public void testDeleteTodosCategoriesID200() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new category to associate with the todo
        String categoryJson = objectMapper.writeValueAsString(catObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure category relationship was created
        assertEquals(201, response.statusCode());
        // Get ID of new category
        String categoryID = objectMapper.readTree(response.body()).get("id").asText();        
        createdCategoryId = categoryID; // For clean up
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
     * DOCUMENTED: Test DELETE /todos/:id/categories/:id
     * Delete categories relationship between specific todo and category with INVALID ID
     * Status code: 404 Not Found
     */
    @Test
    @DisplayName("DELETE /todos/:id/categories/:id - Invalid Category ID (404 Not Found)")
    public void testDeleteTodosCategoriesID404() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        String invalidCategoryID = "10000";
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
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Could not find any instances with todos/%s/categories/%s", todoID, invalidCategoryID)));
    }

    /**
     * DOCUMENTED: Test DELETE /todos/:id/categories/:id
     * Delete categories relationship between specific todo (INVALID ID) and category
     * Status code: 400 Bad Request
     */
    @Test
    @DisplayName("DELETE /todos/:id/categories/:id - Invalid Todo ID (400 Bad Request)")
    public void testDeleteTodosCategoriesID400() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new category to associate with the todo
        String categoryJson = objectMapper.writeValueAsString(catObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/categories"))
                .POST(HttpRequest.BodyPublishers.ofString(categoryJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure category relationship was created
        assertEquals(201, response.statusCode());
        // Get ID of new category
        String categoryID = objectMapper.readTree(response.body()).get("id").asText();
        createdCategoryId = categoryID; // For clean up
        // Delete the category relationship but with invalid todo ID
        String invalidTodoId = "10000";
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
        assertTrue(jsonRoot.get("errorMessages").toString().contains("Cannot invoke \\\"uk.co.compendiumdev.thingifier.core.domain.instances.ThingInstance.getRelationships()\\\" because \\\"parent\\\" is null"));
    }

    /**
     * UNDOCUMENTED - Test HEAD /todos/:id/categories/:id
     * Returns 405   
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
    }
    
    // --------------------- /todos/:id/tasksof ----------------------

    /** 
     * DOCUMENTED - Test HEAD /todos/:id/tasksof
     * Retrieves all tasksof relationships related to todo with ID
     * Returns 200 OK  
    */
    @Test
    @DisplayName("GET /todos/:id/tasksof - 200 OK")
    public void testGetTodosTaskof() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
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
    }

    /**
     * DOCUMENTED - Test POST /todos/:id/tasksof
     * Create tasksof relationship between todo with id and tasksof instance
     * Returns 201 Created      
     */
    @Test
    @DisplayName("POST /todos/:id/tasksof - 201 Created")
    public void testPostTodosTasksof201() throws IOException, InterruptedException { 
        // Create new todo
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new task to associate with the todo
        String taskID = createTask(taskObj);
        createdTaskId = taskID; // for cleanup

        // Create tasksof relationship between todo and task
        taskObj.put("id", taskID); // Add task ID to body of request
        String tasksofJson = objectMapper.writeValueAsString(taskObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .POST(HttpRequest.BodyPublishers.ofString(tasksofJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        taskObj.remove("id"); // restore tasksObj body
        
        // Make sure tasksof relationship was created
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        boolean found = false;
        for (JsonNode project : jsonRoot.get("projects")) {
            // Check if todo ID is in the list of tasks of the project linked to todo
            if (project.get("id").asText().equals(taskID)) {
                for (JsonNode task: project.get("tasks")){
                    if (task.get("id").asText().equals(todoID)){
                        found = true;
                        break;
                    }
                }
            }
        }
        assertTrue(found);
    }

    /**
     * DOCUMENTED - Test POST /todos/:id/tasksof
     * Create tasksof relationship between todo with INVALID id and tasksof instance
     * represented by id in body of message
     * Returns 400 Not Found
     */
    @Test
    @DisplayName("POST /todos/:id/tasksof - Invalid ID (404 Not Found)")
    public void testPostTodosTasksof404() throws IOException, InterruptedException { 
        String invalidID = "10000";
        String tasksofJson = objectMapper.writeValueAsString(taskObj);
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
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Could not find parent thing for relationship todos/%s/tasksof", invalidID)));
    }

    /**
     * UNDOCUMENTED: Test PUT /todos/:id/tasksof
     * Returns 405 Method Not Allowed
     */
    @Test
    @DisplayName("PUT /todos/:id/tasksof - 405 Method Not Allowed")
    public void testPutTodosTasksof() throws IOException, InterruptedException {
        String id = "1";
        String tasksofJson = objectMapper.writeValueAsString(taskObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + id + "/tasksof"))
                .PUT(HttpRequest.BodyPublishers.ofString(tasksofJson))
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        // Verify response status code
        assertEquals(405, response.statusCode());
    }
    
    /**
     * UNDOCUMENTED: Test DELETE /todos/:id/tasksof
     * Returns 405 Method Not Allowed     
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
     * DOCUMENTED - Test HEAD /todos/:id/tasksof
     * Returns headers for project items related todo with id
     * Returns 200 OK  
     */
    @Test
    @DisplayName("HEAD /todos/:id/tasksof - 200 OK")
    public void testHeadTodosTasksof() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
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
     * BUG: Test HEAD /todos/:id/tasksof
     * Returns 200 OK for invalid todo ID
     */
    @Test
    @DisplayName("HEAD /todos/:id/tasksof - Invalid ID (200 OK)")
    public void testHeadTodosTasksofInvalidID() throws IOException, InterruptedException {
        String invalidID = "10000";
        // Checking headers 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidID + "/tasksof"))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode());
    }

    // --------------------- /todos/:id/tasksof/:id ----------------------
    
    /**
     * UNDOCUMENTED: Test GET /todos/:id/tasksof/:id
     * Returns 404 Not Found
     */
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
     * UNDOCUMENTED: Test POST /todos/:id/tasksof/:id
     * Returns 404 Not Found     
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
     * UNDOCUMENTED: Test PUT /todos/:id/tasksof/:id
     * Returns 405 Method Not Allowed    
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
     * DOCUMENTED: Test DELETE /todos/:id/tasksof/:id
     * Delete tasksof instance between todo and project using id
     * Returns 200 OK
     */
    @Test
    @DisplayName("DELETE /todos/:id/tasksof/:id - 200 OK")
    public void testDeleteTodosTasksofID200() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create tasksof relationship between todo and task
        String tasksofJson = objectMapper.writeValueAsString(taskObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .POST(HttpRequest.BodyPublishers.ofString(tasksofJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure tasksof relationship was created
        assertEquals(201, response.statusCode());
        JsonNode jsonRoot = objectMapper.readTree(response.body());
        createdTaskId = jsonRoot.get("id").asText(); // cleanup
        // Delete the tasksof relationship
        HttpRequest taskRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof/" + createdTaskId))
                .DELETE()
                .build();
        HttpResponse<Void> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.discarding());
        // Verify response status code
        assertEquals(200, taskResponse.statusCode());
    }

    /**
     * DOCUMENTED: Test DELETE /todos/:id/tasksof/:id
     * Delete tasksof instance between todo and project using invalid task ID
     * Returns 404 Not Found
     */
    @Test
    @DisplayName("DELETE /todos/:id/tasksof/:id - Invalid TaskId (404 Not Found)")
    public void testDeleteTodosTasksofID404() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup
        
        String invalidTaskID = "10000";
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
        assertTrue(jsonRoot.get("errorMessages").toString().contains(String.format("Could not find any instances with todos/%s/tasksof/%s", todoID, invalidTaskID)));
    }

    /**
     * DOCUMENTED: Test DELETE /todos/:id/tasksof/:id
     * Delete tasksof instance between todo and project using id
     * Returns 200 O
     */
    @Test
    @DisplayName("DELETE /todos/:id/categories/:id - 400 Bad Request")
    public void testDeleteTodosTasksofID400() throws IOException, InterruptedException {
        // Create new todo
        String todoID = createTodo(todoObj);
        createdTodoId = todoID; // Save the created todo ID for cleanup

        // Create new tasksof to associate with the todo
        String tasksofJSON = objectMapper.writeValueAsString(taskObj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + todoID + "/tasksof"))
                .POST(HttpRequest.BodyPublishers.ofString(tasksofJSON))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Make sure tasksof relationship was created
        assertEquals(201, response.statusCode());
        // Get ID of new task
        createdTaskId = objectMapper.readTree(response.body()).get("id").asText();
        
        // Delete the tasksof relationship but with invalid todo ID
        String invalidTodoId = "10000";
        HttpRequest taskRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/todos/" + invalidTodoId + "/tasksof/" + createdTaskId))
                .DELETE()
                .build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
        // Verify response status code
        assertEquals(400, taskResponse.statusCode());
        assertNotNull(taskResponse.body());
        JsonNode jsonRoot = objectMapper.readTree(taskResponse.body());
        assertTrue(jsonRoot.has("errorMessages"));
        assertTrue(jsonRoot.get("errorMessages").toString().contains("Cannot invoke \\\"uk.co.compendiumdev.thingifier.core.domain.instances.ThingInstance.getRelationships()\\\" because \\\"parent\\\" is null"));
    }

    /**
     * UNDOCUMENTED: Test HEAD /todos/:id/tasksof/:id
     * Returns 404 Not Found 
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
}

