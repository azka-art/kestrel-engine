package com.kestrel.api.clients;

import com.kestrel.utils.EnvironmentManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * Kestrel Engine API Client
 * Precision API hunting arsenal for JSONPlaceholder
 * 
 * Features:
 * - Environment-aware configuration
 * - No authentication required (JSONPlaceholder)
 * - Request/Response logging
 * - Error handling
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);
    private RequestSpecification requestSpec;
    private String baseUri;
    
    /**
     * Initialize API Client with environment configuration
     */
    public ApiClient() {
        initializeClient();
    }
    
    /**
     * Initialize client with environment settings
     */
    private void initializeClient() {
        baseUri = EnvironmentManager.getApiUrl();
        
        logger.info("🦅 Kestrel API Client armed for: {}", baseUri);
        logger.info("🔓 Using JSONPlaceholder - No authentication required");
        
        // Configure RestAssured base URI
        RestAssured.baseURI = baseUri;
        
        // Build default request specification (NO authentication headers)
        requestSpec = given()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .log().ifValidationFails();
        
        logger.debug("✅ Kestrel API Client ready for hunt");
    }
    
    /**
     * Get configured request specification
     * @return RequestSpecification with headers and base config
     */
    public RequestSpecification getRequestSpec() {
        return requestSpec;
    }
    
    /**
     * Get request specification without authentication
     * Used for negative testing scenarios (same as normal for JSONPlaceholder)
     * @return Standard RequestSpecification
     */
    public RequestSpecification getUnauthenticatedSpec() {
        logger.info("🎯 Kestrel standard operation - JSONPlaceholder requires no auth");
        return given()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .log().ifValidationFails();
    }
    
    // ===== USER ENDPOINTS =====
    
    /**
     * GET /users - Retrieve all users
     * @return Response object
     */
    public Response getAllUsers() {
        logger.info("🎯 GET Hunt: /users (all users)");
        return requestSpec
            .when()
            .get("/users")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /users with pagination simulation
     * Note: JSONPlaceholder doesn't support real pagination, but we simulate it
     * @param page Page number (0-based)
     * @param limit Number of users per page
     * @return Response object
     */
    public Response getAllUsers(int page, int limit) {
        logger.info("🎯 GET Hunt: /users (simulated pagination - page: {}, limit: {})", page, limit);
        logger.warn("⚠️ JSONPlaceholder doesn't support pagination - returning all users");
        return requestSpec
            .when()
            .get("/users")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /users/{id} - Retrieve user by ID
     * @param userId User ID
     * @return Response object
     */
    public Response getUserById(String userId) {
        logger.info("🎯 GET Hunt: /users/{}", userId);
        return requestSpec
            .pathParam("id", userId)
            .when()
            .get("/users/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * POST /users - Create new user
     * @param userPayload User data as JSON string or object
     * @return Response object
     */
    public Response createUser(Object userPayload) {
        logger.info("🎯 POST Hunt: /users (create user)");
        return requestSpec
            .body(userPayload)
            .when()
            .post("/users")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * PUT /users/{id} - Update user
     * @param userId User ID to update
     * @param userPayload Updated user data
     * @return Response object
     */
    public Response updateUser(String userId, Object userPayload) {
        logger.info("🎯 PUT Hunt: /users/{}", userId);
        return requestSpec
            .pathParam("id", userId)
            .body(userPayload)
            .when()
            .put("/users/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * DELETE /users/{id} - Delete user
     * @param userId User ID to delete
     * @return Response object
     */
    public Response deleteUser(String userId) {
        logger.info("🎯 DELETE Hunt: /users/{}", userId);
        return requestSpec
            .pathParam("id", userId)
            .when()
            .delete("/users/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    // ===== POSTS ENDPOINTS (JSONPlaceholder specific) =====
    
    /**
     * GET /posts - Retrieve all posts
     * @return Response object
     */
    public Response getAllPosts() {
        logger.info("🎯 GET Hunt: /posts (all posts)");
        return requestSpec
            .when()
            .get("/posts")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /posts/{id} - Get post by ID
     * @param postId Post ID
     * @return Response object
     */
    public Response getPostById(String postId) {
        logger.info("🎯 GET Hunt: /posts/{}", postId);
        return requestSpec
            .pathParam("id", postId)
            .when()
            .get("/posts/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /posts?userId={userId} - Get posts by user ID
     * This method provides tag-like filtering functionality
     * @param userId User ID to filter posts by
     * @return Response object
     */
    public Response getPostsByUserId(String userId) {
        logger.info("🎯 GET Hunt: /posts?userId={} (posts by user)", userId);
        return requestSpec
            .queryParam("userId", userId)
            .when()
            .get("/posts")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * POST /posts - Create new post
     * @param postPayload Post data
     * @return Response object
     */
    public Response createPost(Object postPayload) {
        logger.info("🎯 POST Hunt: /posts (create post)");
        return requestSpec
            .body(postPayload)
            .when()
            .post("/posts")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * PUT /posts/{id} - Update post
     * @param postId Post ID to update
     * @param postPayload Updated post data
     * @return Response object
     */
    public Response updatePost(String postId, Object postPayload) {
        logger.info("🎯 PUT Hunt: /posts/{}", postId);
        return requestSpec
            .pathParam("id", postId)
            .body(postPayload)
            .when()
            .put("/posts/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * DELETE /posts/{id} - Delete post
     * @param postId Post ID to delete
     * @return Response object
     */
    public Response deletePost(String postId) {
        logger.info("🎯 DELETE Hunt: /posts/{}", postId);
        return requestSpec
            .pathParam("id", postId)
            .when()
            .delete("/posts/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    // ===== COMMENTS ENDPOINTS (JSONPlaceholder specific) =====
    
    /**
     * GET /comments - Retrieve all comments
     * @return Response object
     */
    public Response getAllComments() {
        logger.info("🎯 GET Hunt: /comments (all comments)");
        return requestSpec
            .when()
            .get("/comments")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /posts/{postId}/comments - Get comments for a post
     * @param postId Post ID
     * @return Response object
     */
    public Response getCommentsByPostId(String postId) {
        logger.info("🎯 GET Hunt: /posts/{}/comments", postId);
        return requestSpec
            .pathParam("postId", postId)
            .when()
            .get("/posts/{postId}/comments")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /comments?postId={postId} - Alternative way to get comments for a post
     * @param postId Post ID
     * @return Response object
     */
    public Response getCommentsByPostIdQuery(String postId) {
        logger.info("🎯 GET Hunt: /comments?postId={}", postId);
        return requestSpec
            .queryParam("postId", postId)
            .when()
            .get("/comments")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    // ===== ALBUMS ENDPOINTS (JSONPlaceholder specific) =====
    
    /**
     * GET /albums - Retrieve all albums
     * @return Response object
     */
    public Response getAllAlbums() {
        logger.info("🎯 GET Hunt: /albums (all albums)");
        return requestSpec
            .when()
            .get("/albums")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /albums/{id} - Get album by ID
     * @param albumId Album ID
     * @return Response object
     */
    public Response getAlbumById(String albumId) {
        logger.info("🎯 GET Hunt: /albums/{}", albumId);
        return requestSpec
            .pathParam("id", albumId)
            .when()
            .get("/albums/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /albums?userId={userId} - Get albums by user ID
     * @param userId User ID
     * @return Response object
     */
    public Response getAlbumsByUserId(String userId) {
        logger.info("🎯 GET Hunt: /albums?userId={}", userId);
        return requestSpec
            .queryParam("userId", userId)
            .when()
            .get("/albums")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    // ===== PHOTOS ENDPOINTS (JSONPlaceholder specific) =====
    
    /**
     * GET /photos - Retrieve all photos
     * @return Response object
     */
    public Response getAllPhotos() {
        logger.info("🎯 GET Hunt: /photos (all photos)");
        return requestSpec
            .when()
            .get("/photos")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /photos/{id} - Get photo by ID
     * @param photoId Photo ID
     * @return Response object
     */
    public Response getPhotoById(String photoId) {
        logger.info("🎯 GET Hunt: /photos/{}", photoId);
        return requestSpec
            .pathParam("id", photoId)
            .when()
            .get("/photos/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /photos?albumId={albumId} - Get photos by album ID
     * @param albumId Album ID
     * @return Response object
     */
    public Response getPhotosByAlbumId(String albumId) {
        logger.info("🎯 GET Hunt: /photos?albumId={}", albumId);
        return requestSpec
            .queryParam("albumId", albumId)
            .when()
            .get("/photos")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    // ===== TODOS ENDPOINTS (JSONPlaceholder specific) =====
    
    /**
     * GET /todos - Retrieve all todos
     * @return Response object
     */
    public Response getAllTodos() {
        logger.info("🎯 GET Hunt: /todos (all todos)");
        return requestSpec
            .when()
            .get("/todos")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /todos/{id} - Get todo by ID
     * @param todoId Todo ID
     * @return Response object
     */
    public Response getTodoById(String todoId) {
        logger.info("🎯 GET Hunt: /todos/{}", todoId);
        return requestSpec
            .pathParam("id", todoId)
            .when()
            .get("/todos/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /todos?userId={userId} - Get todos by user ID
     * @param userId User ID
     * @return Response object
     */
    public Response getTodosByUserId(String userId) {
        logger.info("🎯 GET Hunt: /todos?userId={}", userId);
        return requestSpec
            .queryParam("userId", userId)
            .when()
            .get("/todos")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    // ===== GENERIC HTTP METHODS =====
    
    /**
     * Generic GET request
     * @param endpoint API endpoint
     * @return Response object
     */
    public Response get(String endpoint) {
        logger.info("🎯 GET Hunt: {}", endpoint);
        return requestSpec
            .when()
            .get(endpoint)
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * Generic POST request
     * @param endpoint API endpoint
     * @param body Request body
     * @return Response object
     */
    public Response post(String endpoint, Object body) {
        logger.info("🎯 POST Hunt: {}", endpoint);
        return requestSpec
            .body(body)
            .when()
            .post(endpoint)
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * Generic PUT request
     * @param endpoint API endpoint
     * @param body Request body
     * @return Response object
     */
    public Response put(String endpoint, Object body) {
        logger.info("🎯 PUT Hunt: {}", endpoint);
        return requestSpec
            .body(body)
            .when()
            .put(endpoint)
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * Generic DELETE request
     * @param endpoint API endpoint
     * @return Response object
     */
    public Response delete(String endpoint) {
        logger.info("🎯 DELETE Hunt: {}", endpoint);
        return requestSpec
            .when()
            .delete(endpoint)
            .then()
            .log().ifError()
            .extract().response();
    }
    
    // ===== NEGATIVE TESTING METHODS =====
    
    /**
     * GET request with malformed data (for negative testing)
     * @param endpoint API endpoint
     * @return Response object
     */
    public Response getWithMalformedRequest(String endpoint) {
        logger.info("🎯 GET Hunt (Malformed): {}", endpoint);
        return given()
            .header("Content-Type", "application/xml") // Wrong content type
            .header("Accept", "text/plain") // Wrong accept type
            .log().ifValidationFails()
            .when()
            .get(endpoint)
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * POST request with invalid data (for negative testing)
     * @param endpoint API endpoint
     * @param invalidBody Invalid request body
     * @return Response object
     */
    public Response postWithInvalidData(String endpoint, Object invalidBody) {
        logger.info("🎯 POST Hunt (Invalid Data): {}", endpoint);
        return requestSpec
            .body(invalidBody)
            .when()
            .post(endpoint)
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * Request to non-existent endpoint (for negative testing)
     * @param invalidEndpoint Non-existent endpoint
     * @return Response object
     */
    public Response getInvalidEndpoint(String invalidEndpoint) {
        logger.info("🎯 GET Hunt (Invalid Endpoint): {}", invalidEndpoint);
        return requestSpec
            .when()
            .get(invalidEndpoint)
            .then()
            .log().ifError()
            .extract().response();
    }
    
    // ===== UTILITY METHODS =====
    
    /**
     * Get current API base URI
     * @return Base URI string
     */
    public String getBaseUri() {
        return baseUri;
    }
    
    /**
     * Get current API base URL (alias for getBaseUri)
     * @return Base URL string
     */
    public String getBaseUrl() {
        return baseUri;
    }
    
    /**
     * Get API information
     * @return API info string
     */
    public String getApiInfo() {
        return "JSONPlaceholder - No authentication required";
    }
    
    /**
     * Reset client configuration (useful for testing different environments)
     */
    public void resetClient() {
        logger.info("🔄 Resetting Kestrel API Client configuration");
        initializeClient();
    }
    
    /**
     * Validate if API is reachable
     * @return true if API is responding
     */
    public boolean isApiReachable() {
        try {
            logger.info("🔍 Testing API connectivity...");
            Response response = requestSpec
                .when()
                .get("/users/1")
                .then()
                .extract().response();
            
            boolean isReachable = response.getStatusCode() == 200;
            logger.info("📡 API connectivity: {}", isReachable ? "✅ REACHABLE" : "❌ UNREACHABLE");
            return isReachable;
        } catch (Exception e) {
            logger.error("❌ API connectivity test failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get health check status
     * @return Response object from health check
     */
    public Response getHealthStatus() {
        logger.info("🔍 Checking API health status...");
        return requestSpec
            .when()
            .get("/users/1") // Using user/1 as health check for JSONPlaceholder
            .then()
            .extract().response();
    }
    
    /**
     * Perform bulk operations test
     * @param count Number of requests to perform
     * @return true if all requests succeed
     */
    public boolean performBulkTest(int count) {
        logger.info("🔍 Performing bulk test with {} requests...", count);
        
        for (int i = 1; i <= count; i++) {
            try {
                Response response = getUserById(String.valueOf(i));
                if (response.getStatusCode() != 200 && response.getStatusCode() != 404) {
                    logger.warn("❌ Bulk test failed at request {}: status {}", i, response.getStatusCode());
                    return false;
                }
            } catch (Exception e) {
                logger.error("❌ Bulk test failed at request {}: {}", i, e.getMessage());
                return false;
            }
        }
        
        logger.info("✅ Bulk test completed successfully: {} requests", count);
        return true;
    }
}