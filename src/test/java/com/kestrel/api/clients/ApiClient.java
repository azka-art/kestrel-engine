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
 * Precision API hunting arsenal for DummyAPI.io
 * 
 * Features:
 * - Environment-aware configuration
 * - Automatic authentication
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
    private String appId;
    
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
        appId = EnvironmentManager.getAppId();
        
        logger.info("🦅 Kestrel API Client armed for: {}", baseUri);
        logger.debug("🔑 Using app-id: {}***", appId.substring(0, Math.min(appId.length(), 8)));
        
        // Configure RestAssured base URI
        RestAssured.baseURI = baseUri;
        
        // Build default request specification
        requestSpec = given()
            .header("app-id", appId)
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
     * Used for negative testing scenarios
     * @return Unauthenticated RequestSpecification
     */
    public RequestSpecification getUnauthenticatedSpec() {
        logger.warn("⚠️ Kestrel operating without credentials - for negative testing");
        return given()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .log().ifValidationFails();
    }
    
    // ===== USER ENDPOINTS =====
    
    /**
     * GET /user - Retrieve all users
     * @return Response object
     */
    public Response getAllUsers() {
        logger.info("🎯 GET Hunt: /user (all users)");
        return requestSpec
            .when()
            .get("/user")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /user with pagination
     * @param page Page number (0-based)
     * @param limit Number of users per page
     * @return Response object
     */
    public Response getAllUsers(int page, int limit) {
        logger.info("🎯 GET Hunt: /user (page: {}, limit: {})", page, limit);
        return requestSpec
            .queryParam("page", page)
            .queryParam("limit", limit)
            .when()
            .get("/user")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /user/{id} - Retrieve user by ID
     * @param userId User ID
     * @return Response object
     */
    public Response getUserById(String userId) {
        logger.info("🎯 GET Hunt: /user/{}", userId);
        return requestSpec
            .pathParam("id", userId)
            .when()
            .get("/user/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * POST /user/create - Create new user
     * @param userPayload User data as JSON string or object
     * @return Response object
     */
    public Response createUser(Object userPayload) {
        logger.info("🎯 POST Hunt: /user/create");
        return requestSpec
            .body(userPayload)
            .when()
            .post("/user/create")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * PUT /user/{id} - Update user
     * @param userId User ID to update
     * @param userPayload Updated user data
     * @return Response object
     */
    public Response updateUser(String userId, Object userPayload) {
        logger.info("🎯 PUT Hunt: /user/{}", userId);
        return requestSpec
            .pathParam("id", userId)
            .body(userPayload)
            .when()
            .put("/user/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * DELETE /user/{id} - Delete user
     * @param userId User ID to delete
     * @return Response object
     */
    public Response deleteUser(String userId) {
        logger.info("🎯 DELETE Hunt: /user/{}", userId);
        return requestSpec
            .pathParam("id", userId)
            .when()
            .delete("/user/{id}")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    // ===== TAG ENDPOINTS =====
    
    /**
     * GET /tag - Retrieve all tags
     * @return Response object
     */
    public Response getAllTags() {
        logger.info("🎯 GET Hunt: /tag (all tags)");
        return requestSpec
            .when()
            .get("/tag")
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * GET /post/tag/{tag} - Get posts by tag
     * @param tag Tag name
     * @return Response object
     */
    public Response getPostsByTag(String tag) {
        logger.info("🎯 GET Hunt: /post/tag/{}", tag);
        return requestSpec
            .pathParam("tag", tag)
            .when()
            .get("/post/tag/{tag}")
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
     * GET request without authentication (for negative testing)
     * @param endpoint API endpoint
     * @return Response object
     */
    public Response getWithoutAuth(String endpoint) {
        logger.info("🎯 GET Hunt (No Auth): {}", endpoint);
        return getUnauthenticatedSpec()
            .when()
            .get(endpoint)
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * POST request without authentication (for negative testing)
     * @param endpoint API endpoint
     * @param body Request body
     * @return Response object
     */
    public Response postWithoutAuth(String endpoint, Object body) {
        logger.info("🎯 POST Hunt (No Auth): {}", endpoint);
        return getUnauthenticatedSpec()
            .body(body)
            .when()
            .post(endpoint)
            .then()
            .log().ifError()
            .extract().response();
    }
    
    /**
     * Request with invalid app-id (for negative testing)
     * @param endpoint API endpoint
     * @return Response object
     */
    public Response getWithInvalidAuth(String endpoint) {
        logger.info("🎯 GET Hunt (Invalid Auth): {}", endpoint);
        return given()
            .header("app-id", "invalid-app-id-12345")
            .header("Content-Type", "application/json")
            .log().ifValidationFails()
            .when()
            .get(endpoint)
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
     * Get current app ID (masked for security)
     * @return Masked app ID
     */
    public String getMaskedAppId() {
        return appId.substring(0, Math.min(appId.length(), 8)) + "***";
    }
    
    /**
     * Reset client configuration (useful for testing different environments)
     */
    public void resetClient() {
        logger.info("🔄 Resetting Kestrel API Client configuration");
        initializeClient();
    }
}
